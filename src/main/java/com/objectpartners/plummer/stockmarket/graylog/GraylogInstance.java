package com.objectpartners.plummer.stockmarket.graylog;

import com.objectpartners.plummer.stockmarket.data.MongoInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;

@Named
@Singleton
public class GraylogInstance implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraylogInstance.class);

    // Hint to Spring that we need to run this config AFTER Mongo is started
    @Inject
    private MongoInstance mongo;

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

    @Override
    public void destroy() throws Exception {
        Process startProcess = Runtime.getRuntime().exec(
                "./graylog/graylog-"+graylogVersion+"/bin/graylogctl stop");

        int result = startProcess.waitFor();
        LOGGER.info("Graylog stop process completed with status {}", result);
    }
}
