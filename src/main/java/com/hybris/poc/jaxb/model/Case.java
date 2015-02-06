package com.hybris.poc.jaxb.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by i303813 on 05/02/15.
 */
public class Case {

    //private String method;

    private When when;

    private Then then;

    public void setThen(Then then) {
        this.then = then;
    }

    public Then getThen() {
        return then;
    }


    public void setWhen(When when) {
        this.when = when;
    }


    public When getWhen() {
        return when;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
