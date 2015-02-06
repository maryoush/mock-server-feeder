package com.hybris.poc.jaxb.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

/**
 * Created by i303813 on 05/02/15.
 */
public class When {

    private Map<String, String> headerParams;

    private Map<String, String> queryParams;

    private Map<String, String> body;

    private Integer times;


    public void setHeaderParams(Map<String, String> headerParams) {
        this.headerParams = headerParams;
    }

    public Map<String, String> getHeaderParams() {
        return headerParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }


    public void setBody(Map<String, String> body) {
        this.body = body;
    }

    public Map<String, String> getBody() {
        return body;
    }


    public void setTimes(Integer times) {
        this.times = times;
    }


    public Integer getTimes() {
        return times;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
