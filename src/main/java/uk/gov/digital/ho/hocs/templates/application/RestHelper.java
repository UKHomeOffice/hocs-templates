package uk.gov.digital.ho.hocs.templates.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Base64;

import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static uk.gov.digital.ho.hocs.templates.application.LogEvent.EVENT;
import static uk.gov.digital.ho.hocs.templates.application.LogEvent.REST_HELPER_GET;

@Slf4j
@Component
public class RestHelper {


    private RestTemplate restTemplate;

    private RequestData requestData;

    @Autowired
    public RestHelper(RestTemplate restTemplate, RequestData requestData) {
        this.restTemplate = restTemplate;
        this.requestData = requestData;
    }

    public <R> R get(String serviceBaseURL, String url, Class<R> responseType) {
        log.info("RestHelper making GET request to {}{}", serviceBaseURL, url, value(LogEvent.EVENT, REST_HELPER_GET));
        ResponseEntity<R> response = restTemplate.exchange(String.format("%s%s", serviceBaseURL, url), HttpMethod.GET, new HttpEntity<>(null, createAuthHeaders()), responseType);
        return response.getBody();
    }

    public <R> R get(String serviceBaseURL, String url, ParameterizedTypeReference<R> responseType) {
        log.info("RestHelper making GET request to {}{}", serviceBaseURL, url, value(EVENT, REST_HELPER_GET));
        ResponseEntity<R> response = restTemplate.exchange(String.format("%s%s", serviceBaseURL, url), HttpMethod.GET, new HttpEntity<>(null, createAuthHeaders()), responseType);
        return response.getBody();
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(RequestData.GROUP_HEADER, requestData.groups());
        headers.add(RequestData.USER_ID_HEADER, requestData.userId());
        headers.add(RequestData.CORRELATION_ID_HEADER, requestData.correlationId());
        return headers;
    }

}
