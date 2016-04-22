package com.objectpartners.plummer.stockmarket.graylog;

import com.google.common.collect.Lists;
import org.apache.tomcat.util.codec.binary.Base64;
import org.graylog2.rest.models.system.inputs.requests.InputCreateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Named;
import java.nio.charset.StandardCharsets;

@Named
public class GraylogRestInterface {

    @Value("${graylog.username}")
    private String graylogUsername;

    @Value("${graylog.password}")
    private String graylogPassword;

    private final UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
            .scheme("http").host("localhost").port(12900);

    private final RestTemplate restTemplate;

    public GraylogRestInterface() {
        String auth = graylogUsername + ":" + graylogPassword;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII) );
        final String authHeader = "Basic " + new String( encodedAuth );

        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader );
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
            return execution.execute(request, body);
        };

        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new InterceptingClientHttpRequestFactory(restTemplate.getRequestFactory(), Lists.newArrayList(interceptor)));
    }

    public void createInput(InputCreateRequest request) {
        HttpEntity<InputCreateRequest> entity = new HttpEntity<>(request);
        restTemplate.postForEntity(uriBuilder.cloneBuilder().path("system/inputs").toUriString(), entity, null);
    }

    public void logEvent(GelfMessage message) {
        HttpEntity<GelfMessage> entity = new HttpEntity<>(message);
        restTemplate.postForEntity(uriBuilder.cloneBuilder().port(12202).path("gelf").toUriString(), entity, null);
    }
}
