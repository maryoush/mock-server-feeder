package com.hybris.poc.feeder;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;

import java.util.logging.Logger;

/**
 * Created by i303813 on 05/02/15.
 */
public class MockServerClientRunner {


    private static final Logger LOG = Logger.getLogger(LocalFilesystemMockFeeder.class.getName());

    public static MockServerClientRunner newRunner()
    {
        return new MockServerClientRunner();
    }

    private String host = "localhost";
    private int port = 8000;

    //null object feeder
    private MockFeeder feeder = null;

    private MockServerClientRunner(){
        //
    }

    public MockServerClientRunner withHost(final String ghost) {
        this.host = ghost;
        return this;
    }


    public MockServerClientRunner withPort(final int gport) {
        this.port = gport;
        return this;
    }


    public MockServerClientRunner withMockFeeder(final MockFeeder gfeeder) {
        this.feeder = gfeeder;
        return this;
    }


    public MockServerClient start(final boolean debugMode) {

        LOG.info("Starting mock rest endpoint (" + (debugMode ? "debug mode" : "") + ")  at "
                + host + " : " + port);

        ClientAndServer.startClientAndServer(port);

        MockServerClient mockServerClient = new MockServerClient(host, port);
        if (debugMode) {
            mockServerClient.dumpToLog();
        }

        MockServerConfigurer caseInterceptor = new MockServerConfigurer(mockServerClient);


        feeder.feed(caseInterceptor);


        LOG.info("Started and configured mock rest endpoint (" + (debugMode ? "debug mode" : "") + ")  at "
                + host + " : " + port);

        return mockServerClient;
    }


    public MockServerClient start() {
        return start(false);
    }


}
