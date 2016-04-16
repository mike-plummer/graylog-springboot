package com.objectpartners.plummer.stockmarket.config;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Set;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

    @Value("${info.build.version}")
    private String buildVersion;

    @Value("${info.build.description}")
    private String projectDescription;

    @Value("${info.build.name}")
    private String projectName;

    @Bean
    public Docket stockmarketApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .consumes(apiContentTypes())
                .produces(apiContentTypes())
                .groupName("stockmarket")
                .apiInfo(apiInfo())
                .select()
                .apis(basePackage("com.objectpartners.plummer.stockmarket"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(projectName)
                .description(projectDescription)
                .contact(new Contact("Mike Plummer", "https://mike-plummer.github.io", "mike.plummer@objectpartners.com"))
                .license("MIT")
                .version(buildVersion)
                .build();
    }

    private Set<String> apiContentTypes() {
        return Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE);
    }
}
