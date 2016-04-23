package com.objectpartners.plummer.stockmarket.graylog;

import com.objectpartners.plummer.stockmarket.data.MongoInstance;
import org.graylog2.inputs.gelf.http.GELFHttpInput;
import org.graylog2.inputs.gelf.tcp.GELFTCPInput;
import org.graylog2.rest.models.system.inputs.requests.InputCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Named
@Singleton
public class GraylogInstance implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraylogInstance.class);

    // Hint to Spring that we need to run this config AFTER Mongo is started
    @Inject
    private MongoInstance mongo;

    @Inject
    private GraylogRestInterface graylogRestInterface;

    @Value("${graylog.configFile}")
    private String graylogConfigFile;

    @Value("${graylog.version}")
    private String graylogVersion;

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("./graylog/graylog-"+graylogVersion+"/bin/graylogctl", "start");
        builder.environment().put("GRAYLOG_CONF", graylogConfigFile);
        Process process = builder.start();

        int result = process.waitFor();
        LOGGER.info("Graylog start process completed with status {}", result);
    }

    @Scheduled(initialDelay = 10000L, fixedDelay = Long.MAX_VALUE)
    public void setupTcpInput() {
        String inputName = "SpringBoot GELF TCP";
        if (graylogRestInterface.inputExists(inputName)) {
            return;
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put("use_null_delimiter", true);
        properties.put("bind_address", "0.0.0.0");
        properties.put("port", 12201);

        InputCreateRequest request = InputCreateRequest.create(
                inputName,
                GELFTCPInput.class.getName(),
                true,
                properties,
                null);

        graylogRestInterface.createInput(request);
    }

    @Scheduled(initialDelay = 10000L, fixedDelay = Long.MAX_VALUE)
    public void setupHttpInput() {
        String inputName = "SpringBoot GELF HTTP";
        if (graylogRestInterface.inputExists(inputName)) {
            return;
        }
        Map<String, Object> properties = new HashMap<>();
        properties.put("use_null_delimiter", true);
        properties.put("bind_address", "0.0.0.0");
        properties.put("port", 12202);

        InputCreateRequest request = InputCreateRequest.create(
                inputName,
                GELFHttpInput.class.getName(),
                true,
                properties,
                null);

        graylogRestInterface.createInput(request);
    }

    @Override
    public void destroy() throws Exception {
        Process startProcess = Runtime.getRuntime().exec(
                "./graylog/graylog-"+graylogVersion+"/bin/graylogctl stop");

        int result = startProcess.waitFor();
        LOGGER.info("Graylog stop process completed with status {}", result);
    }
}
