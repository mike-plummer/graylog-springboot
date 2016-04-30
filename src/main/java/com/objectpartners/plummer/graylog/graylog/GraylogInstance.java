package com.objectpartners.plummer.graylog.graylog;

import com.objectpartners.plummer.graylog.data.MongoInstance;
import io.airlift.airline.Cli;
import org.graylog2.bootstrap.CliCommand;
import org.graylog2.bootstrap.CliCommandsProvider;
import org.graylog2.bootstrap.commands.Help;
import org.graylog2.bootstrap.commands.ShowVersion;
import org.graylog2.inputs.gelf.http.GELFHttpInput;
import org.graylog2.inputs.gelf.udp.GELFUDPInput;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.rest.models.dashboards.requests.AddWidgetRequest;
import org.graylog2.rest.models.dashboards.requests.CreateDashboardRequest;
import org.graylog2.rest.models.system.inputs.extractors.requests.CreateExtractorRequest;
import org.graylog2.rest.models.system.inputs.requests.InputCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

@Named
@Singleton
public class GraylogInstance {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraylogInstance.class);

    // Hint to Spring that we need to run this config AFTER Mongo is started
    @Inject
    private MongoInstance mongo;

    @Inject
    private GraylogRestInterface graylogRestInterface;

    @Value("${graylog.configFile}")
    private String graylogConfigFile;

    @Inject
    protected TaskScheduler scheduler;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        System.setProperty("java.library.path", "./sigar-libs");
        final Cli.CliBuilder<CliCommand> builder = Cli.<CliCommand>builder("graylog")
                .withDefaultCommand(Help.class)
                .withCommands(Help.class, ShowVersion.class);

        ServiceLoader commandsProviders = ServiceLoader.load(CliCommandsProvider.class);

        for (Object commandsProvider : commandsProviders) {
            CliCommandsProvider command = (CliCommandsProvider) commandsProvider;
            command.addTopLevelCommandsOrGroups(builder);
        }

        final Cli<CliCommand> cli = builder.build();
        final Runnable command = cli.parse("server", "-f", graylogConfigFile);
        final Thread graylogThread = new Thread(command);

        /*
        This is a nasty workaround to get Graylog's Swagger instance working when embedded within SpringBoot.
        DocumentationBrowserResource uses the SystemClassLoader (which it 100% should not be doing) which means
        it fails to load resources when run in an environment like SpringBoot
         */
        try {
            Field sclField = ReflectionUtils.findField(ClassLoader.class, "scl");
            ReflectionUtils.makeAccessible(sclField);
            ReflectionUtils.setField(sclField, null, new DecoratingClassLoader() {
                @Override
                public URL getResource(String name) {
                    if (name.startsWith("swagger/")) {
                        return Thread.currentThread().getContextClassLoader().getResource(name);
                    }
                    return super.getResource(name);
                }
            });
        } catch(Exception e) {
            LOGGER.error("Failed to replace SystemClassLoader, this means Graylog's Swagger won't work.", e);
        }

        graylogThread.start();

        LOGGER.info("Graylog started");

        configureGraylog();
    }

    private void configureGraylog() {
        try {
            graylogRestInterface.inputExists("test");
            LOGGER.info("Graylog rest interface ready to go!");

            setupUdpInput();
            setupHttpInput();
            setupDashboard();
        } catch (Exception e) {
            LOGGER.warn("Graylog rest interface not ready yet, rescheduling...");
            delay(this::configureGraylog, 1);
        }
    }

    private void delay(Runnable runnable, int delaySeconds) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, delaySeconds);
        scheduler.schedule(runnable, cal.getTime());
    }

    private void setupUdpInput() {
        String inputName = "SpringBoot GELF UDP";
        if (graylogRestInterface.inputExists(inputName)) {
            LOGGER.info("Graylog UDP input already exists, skipping...");
            return;
        }
        setupInput(inputName, GELFUDPInput.class, 12201);
    }

    private void setupHttpInput() {
        String inputName = "SpringBoot GELF HTTP";
        if (graylogRestInterface.inputExists(inputName)) {
            LOGGER.info("Graylog HTTP input already exists, skipping...");
            return;
        }
        String inputId = setupInput(inputName, GELFHttpInput.class, 12202);

        Map<String, Object> extractorConfig = new HashMap<>();
        extractorConfig.put("key_separator", "_");
        extractorConfig.put("list_separator", ", ");
        extractorConfig.put("kv_separator", "=");
        CreateExtractorRequest extractorRequest = CreateExtractorRequest.create(
                "Quote JSON",
                "copy",
                "full_message",
                "",
                "json",
                extractorConfig,
                Collections.emptyMap(),
                "none",
                "",
                0
        );
        graylogRestInterface.createExtractor(inputId, extractorRequest);
        LOGGER.info("Graylog JSON extractor created.");
    }

    private String setupInput(String name, Class<? extends MessageInput> inputType, int port) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("use_null_delimiter", true);
        properties.put("bind_address", "0.0.0.0");
        properties.put("port", port);

        InputCreateRequest request = InputCreateRequest.create(
                name,
                inputType.getName(),
                true,
                properties,
                null);

        String inputId = graylogRestInterface.createInput(request);
        LOGGER.info(String.format("%s input created.", name));
        return inputId;
    }

    private void setupDashboard() {
        String dashboardName = "Stock Market";
        if (graylogRestInterface.dashboardExists(dashboardName)) {
            LOGGER.info("Graylog dashboard already exists, skipping...");
            return;
        }
        CreateDashboardRequest dashboardRequest = CreateDashboardRequest.create(
                dashboardName,
                "Measurements from Stock Market example application"
        );

        String dashboardId = graylogRestInterface.createDashboard(dashboardRequest);
        LOGGER.info("Graylog dashboard created.");

        Map<String, Object> timerangeMap = new HashMap<String, Object>() {{
            put("type", "relative");
            put("range", 300);
        }};

        AddWidgetRequest widgetRequest = AddWidgetRequest.create(
                "Stock Quotes - Symbols",
                "QUICKVALUES",
                10,
                new HashMap<String, Object>(){{
                        put("timerange", timerangeMap);
                        put("field", "symbol");
                        put("show_pie_chart", true);
                        put("query", "");
                        put("show_data_table", true);
                }}
        );
        graylogRestInterface.createWidget(dashboardId, widgetRequest);
        LOGGER.info("Symbol widget created.");

        widgetRequest = AddWidgetRequest.create(
                "Generated Quote Count",
                "SEARCH_RESULT_COUNT",
                10,
                new HashMap<String, Object>(){{
                    put("timerange", timerangeMap);
                    put("lower_is_better", false);
                    put("trend", false);
                    put("query", "_exists_:symbol");
                }}
        );
        graylogRestInterface.createWidget(dashboardId, widgetRequest);
        LOGGER.info("Quote count widget created.");

        widgetRequest = AddWidgetRequest.create(
                "Quote Generation Time (us)",
                "FIELD_CHART",
                10,
                new HashMap<String, Object>(){{
                    put("timerange", new HashMap<String, Object>() {{
                        put("type", "relative");
                        put("range", 300);
                    }});
                    put("valuetype", "mean");
                    put("renderer", "line");
                    put("interpolation", "linear");
                    put("field", "elapsed_time");
                    put("interval", "minute");
                    put("rangeType", "relative");
                    put("relative", 300);
                    put("query", "_exists_:elapsed_time");
                }}
        );
        graylogRestInterface.createWidget(dashboardId, widgetRequest);
        LOGGER.info("Quote generation time widget created.");

        widgetRequest = AddWidgetRequest.create(
                "Error Count",
                "SEARCH_RESULT_COUNT",
                10,
                new HashMap<String, Object>(){{
                    put("timerange", new HashMap<String, Object>() {{
                        put("type", "relative");
                        put("range", 300);
                    }});
                    put("lower_is_better", false);
                    put("trend", false);
                    put("query", "_exists_:stacktrace");
                }}
        );
        graylogRestInterface.createWidget(dashboardId, widgetRequest);
        LOGGER.info("Error count widget created.");
    }
}
