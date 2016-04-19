package com.objectpartners.plummer.stockmarket.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.lease.Releasable;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is basically a stripped-down version of {@link ElasticsearchAutoConfiguration} - as of SpringBoot 1.3.3
 * the version of ElasticSearch in use is 1.5.2 but Graylog requires 2.1+. Building the Elasticsearch node manually
 * allows us to more easily use the newer version. Once SpringBoot 1.4 drops the version of ElasticSearch should be
 * upgraded to a compatible version thus this class should go away then.
 */
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchInstance implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchAutoConfiguration.class);
    private static final Map<String, String> DEFAULTS;

    static {
        Map<String, String> defaults = new LinkedHashMap<>();
        defaults.put("http.enabled", String.valueOf(false));
        defaults.put("node.local", String.valueOf(true));
        defaults.put("path.home", ".");
        DEFAULTS = Collections.unmodifiableMap(defaults);
    }

    @Inject
    private ElasticsearchProperties properties;

    private Releasable releasable;

    @Bean
    public Client elasticsearchClient() {
        Settings.Builder settings = Settings.settingsBuilder();
        DEFAULTS.entrySet().stream()
                .filter(entry -> !properties.getProperties().containsKey(entry.getKey()))
                .forEach(entry -> settings.put(entry.getKey(), entry.getValue()));
        settings.put(properties.getProperties());
        Node node = new NodeBuilder().settings(settings).clusterName(properties.getClusterName()).node();
        this.releasable = node;
        return node.client();
    }

    @Override
    public void destroy() throws Exception {
        if (this.releasable != null) {
            LOGGER.info("Closing Elasticsearch client");
            this.releasable.close();
        }
    }
}
