package com.hybris.poc.dirs;

import org.mockserver.client.server.MockServerClient;

import java.io.IOException;

/**
 * Created by i303813 on 05/02/15.
 */
public interface MockFeeder {

    MockServerClient feed(final MockServerClient mockServerClient) throws MockFeedException;
}
