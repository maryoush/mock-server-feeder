package com.hybris.poc;

import com.hybris.poc.feeder.LocalFilesystemMockFeeder;
import com.hybris.poc.feeder.MockFeeder;
import com.hybris.poc.feeder.MockServerClientRunner;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.mockserver.client.server.MockServerClient;

import java.net.URISyntaxException;
import java.util.logging.Logger;

/**
 * Created by i303813 on 05/02/15.
 */
public class StandaloneClientTester {

    private static final Logger LOG = Logger.getLogger(StandaloneClientTester.class.getName());


    private static final String ROOT_FOLDER = "mockserver";


    @Before
    public static void main(final String[] args) throws InterruptedException, URISyntaxException {

        final MockFeeder feeder = new LocalFilesystemMockFeeder(ROOT_FOLDER);


        MockServerClientRunner.newRunner()
                .withHost("localhost")
                .withPort(8001)
                .withMockFeeder(feeder)
                .start(true);

    }



}
