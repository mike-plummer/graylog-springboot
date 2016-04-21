package com.objectpartners.plummer.stockmarket;

import com.objectpartners.plummer.stockmarket.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
