package com.hybris.poc.jaxb.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;

/**
 * Created by i303813 on 05/02/15.
 */
public class Then {

    private Map<String, String> responseHeaders;

    private List<Map<String, String>> responseBody;

    private int responseStatus;
    private int responseDelay;

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }


    public void setResponseBody(List<Map<String, String>> responseBody) {
        this.responseBody = responseBody;
    }


    public List<Map<String, String>> getResponseBody() {
        return responseBody;
    }


    public void setResponseDelay(int responseDelay) {
        this.responseDelay = responseDelay;
    }


    public int getResponseDelay() {
        return responseDelay;
    }


    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }


    public int getResponseStatus() {
        return responseStatus;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
