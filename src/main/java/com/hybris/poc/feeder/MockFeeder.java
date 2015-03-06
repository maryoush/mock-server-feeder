package com.hybris.poc.feeder;

/**
 * Created by i303813 on 05/02/15.
 */
public interface MockFeeder {

    void feed(MockServerConfigurer caseInterceptor) throws MockFeedException;
}
