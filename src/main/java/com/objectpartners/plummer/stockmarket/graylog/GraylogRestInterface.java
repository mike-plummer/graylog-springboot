package com.objectpartners.plummer.stockmarket.graylog;

import com.google.common.collect.Lists;
import org.apache.tomcat.util.codec.binary.Base64;
import org.graylog2.rest.models.dashboards.requests.AddWidgetRequest;
import org.graylog2.rest.models.dashboards.requests.CreateDashboardRequest;
import org.graylog2.rest.models.dashboards.responses.CreateDashboardResponse;
import org.graylog2.rest.models.dashboards.responses.DashboardList;
import org.graylog2.rest.models.system.inputs.extractors.requests.CreateExtractorRequest;
import org.graylog2.rest.models.system.inputs.requests.InputCreateRequest;
import org.graylog2.rest.models.system.inputs.responses.InputCreated;
import org.graylog2.rest.models.system.inputs.responses.InputsList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
        restTemplate = new RestTemplate();
    }

    public String createInput(InputCreateRequest request) {
        HttpEntity<InputCreateRequest> entity = new HttpEntity<>(request, buildHeaders());
        ResponseEntity<InputCreated> response = restTemplate.postForEntity(uriBuilder.cloneBuilder().path("system/inputs").toUriString(), entity, InputCreated.class);
        return response.getBody().id();
    }

    public boolean inputExists(String inputName) {
        HttpEntity<InputCreateRequest> entity = new HttpEntity<>(null, buildHeaders());
        ResponseEntity<InputsList> response = restTemplate.exchange(uriBuilder.cloneBuilder().path("system/inputs").toUriString(), HttpMethod.GET, entity, InputsList.class);
        InputsList inputs = response.getBody();
        return inputs.inputs().stream().anyMatch(input -> inputName.equals(input.title()));
    }

    public void logEvent(GelfMessage message) {
        HttpEntity<GelfMessage> entity = new HttpEntity<>(message, buildHeaders());
        restTemplate.postForEntity(uriBuilder.cloneBuilder().port(12202).path("gelf").toUriString(), entity, null);
    }

    public void createExtractor(String inputId, CreateExtractorRequest extractorRequest) {
        HttpEntity<CreateExtractorRequest> entity = new HttpEntity<>(extractorRequest, buildHeaders());
        restTemplate.postForEntity(uriBuilder.cloneBuilder().path("system/inputs/"+inputId+"/extractors").toUriString(), entity, null);
    }

    public String createDashboard(CreateDashboardRequest dashboardRequest) {
        HttpEntity<CreateDashboardRequest> entity = new HttpEntity<>(dashboardRequest, buildHeaders());
        ResponseEntity<CreateDashboardResponse> response = restTemplate.postForEntity(uriBuilder.cloneBuilder().path("dashboards").toUriString(), entity, CreateDashboardResponse.class);
        return response.getBody().dashboardId();
    }

    public boolean dashboardExists(String dashboardName) {
        HttpEntity<InputCreateRequest> entity = new HttpEntity<>(null, buildHeaders());
        ResponseEntity<ConcreteDashboardList> response = restTemplate.exchange(uriBuilder.cloneBuilder().path("dashboards").toUriString(), HttpMethod.GET, entity, ConcreteDashboardList.class);
        DashboardList dashboards = response.getBody();
        return dashboards.dashboards().stream().anyMatch(dashboard -> dashboardName.equals(dashboard.get("title")));
    }

    public void createWidget(String dashboardId, AddWidgetRequest widgetRequest) {
        HttpEntity<AddWidgetRequest> entity = new HttpEntity<>(widgetRequest, buildHeaders());
        restTemplate.postForEntity(uriBuilder.cloneBuilder().path("dashboards/"+dashboardId+"/widgets").toUriString(), entity, null);
    }

    private HttpHeaders buildHeaders() {
        String auth = graylogUsername + ":" + graylogPassword;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII) );
        final String authHeader = "Basic " + new String( encodedAuth );

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authHeader );
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
