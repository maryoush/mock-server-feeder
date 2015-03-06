package com.hybris.poc;

import com.hybris.poc.feeder.LocalFilesystemMockFeeder;
import com.hybris.poc.feeder.MockFeeder;
import com.hybris.poc.feeder.MockServerClientRunner;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.mockserver.client.server.MockServerClient;

import java.io.Console;
import java.util.logging.Logger;

/**
 * Created by i303813 on 05/02/15.
 */
public class StandaloneClientTester {

    private static final Logger LOG = Logger.getLogger(StandaloneClientTester.class.getName());

    // private static final Client client = ClientBuilder.newClient(new RepositoryClientConfig());

    private static final String ROOT_FOLDER = StandaloneClientTester.class.getClassLoader().getResource("mockserver").getPath();


    @Before
    public static void main(final String[] args) throws InterruptedException {

        final MockFeeder feeder = new LocalFilesystemMockFeeder(ROOT_FOLDER);

        MockServerClientRunner runner = new MockServerClientRunner();


        MockServerClient clientMock = runner
                .withHost("localhost")
                .withPort(8001)
                .withMockFeeder(feeder)
                .start(true);


       // Thread.sleep(1000 * 10);

    }


    private static class RepositoryClientConfig extends ClientConfig {
        /**
         * Configures the client for repository service access.
         */
        public RepositoryClientConfig() {
            register(JacksonFeature.class);
            register(new LoggingFilter(Logger.getLogger(RepositoryClientConfig.class.getName() + ".jersey"), false));
        }
    }


}
