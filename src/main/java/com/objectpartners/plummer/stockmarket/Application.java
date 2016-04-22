package com.objectpartners.plummer.stockmarket;

import com.objectpartners.plummer.stockmarket.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableAutoConfiguration
@Import({
        SchedulingConfiguration.class,
        SwaggerConfiguration.class,
        WebMvcConfiguration.class
})
@ComponentScan("com.objectpartners.plummer.stockmarket")
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext application = SpringApplication.run(Application.class, args);
        LOGGER.info("Application started, registering shutdown hook...");
        application.registerShutdownHook();
    }
}
