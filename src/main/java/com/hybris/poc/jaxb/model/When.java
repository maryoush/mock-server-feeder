package com.hybris.poc.jaxb.model;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hybris.poc.jaxb.StringBodySerializer;
import com.hybris.poc.jaxb.StringMarshaller;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

/**
 * Created by i303813 on 05/02/15.
 */

public class When {

    private Map<String, String> headerParams;

    private Map<String, String> queryParams;

    private String body;

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


    @JsonDeserialize( using = StringBodySerializer.class)
    public void setBody(String body) {
        this.body = body;
    }


    public String getBody() {
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
