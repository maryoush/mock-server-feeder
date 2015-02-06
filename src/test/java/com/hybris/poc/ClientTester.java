package com.hybris.poc;

import com.hybris.poc.dirs.LocalFilesystemMockFeeder;
import com.hybris.poc.dirs.MockFeeder;
import com.hybris.poc.dirs.MockServerClientRunner;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

/**
 * Created by i303813 on 05/02/15.
 */
public class ClientTester {

    private static final Client client = ClientBuilder.newClient(new RepositoryClientConfig());

    private static final String ROOT_FOLDER = ClientTester.class.getClassLoader().getResource("mockserver").getPath();

    private MockServerClient clientMock;

    @Before
    public void prepare() {

        MockFeeder feeder = new LocalFilesystemMockFeeder(ROOT_FOLDER);

        MockServerClientRunner runner = new MockServerClientRunner();


        clientMock = runner
                .withHost("localhost")
                .withPort(8001)
                .withMockFeeder(feeder)
                .start(true);

    }

    @After
    public void cleanUp() {

        if (clientMock != null) {
            clientMock.stop();
        }
    }

    @Test
    public void assureQueryParams() {
        final Response response = client.target("http://localhost:8001")
                .path("testTenant")
                .path("configurations")
                .queryParam("pageNumber", 1)
                .queryParam("pageSize", 2)
                .request()
                .header("hybris-tenant", "foo")
                .header("hybris-app", "bar")
                .buildGet()
                .invoke();


        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("[{\"foo\":\"bar\",\"baa\":\"1\"},{\"fuu\":\"bar\",\"bar\":\"1\"}]",
                response.readEntity(String.class));
    }


    @Test
    public void assureQueryParamsNotMatching() {
        final Response response = client.target("http://localhost:8001")
                .path("testTenant")
                .path("configurations")
                .queryParam("pageNumber", 10)
                .queryParam("pageSize", 20)
                .request()
                .header("hybris-tenant", "foo")
                .header("hybris-app", "bar")
                .buildGet()
                .invoke();


        Assert.assertEquals(404, response.getStatus());
        System.out.println(response.readEntity(String.class));
    }


    @Test
    public void assurePostWithBody() {


        BodyCase3 bodyCase3 = new BodyCase3();
        bodyCase3.given = "value";
        bodyCase3.other = "thing";

        final Response response = client.target("http://localhost:8001")
                .path("testTenant")
                .path("configurations")
                .queryParam("pageNumber", 1)
                .request()
                .header("hybris-tenant", "foo")
                .header("hybris-app", "bar")
                .buildPost(Entity.entity(bodyCase3, MediaType.APPLICATION_JSON_TYPE))
                .invoke();


        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("where_this_is", response.getLocation().toString());

        Assert.assertEquals("[{\"foo\":\"bar\",\"baz\":\"1\"}]", response.readEntity(String.class));
    }


    @Test
    public void assureSubPathWithTimes() {

        final Invocation invocation = client.target("http://localhost:8001")
                .path("testTenant")
                .path("configurations")
                .path("key1")
                .queryParam("pageNumber", 1)
                .request()
                .header("hybris-tenant", "foo")
                .header("hybris-app", "bar")
                .buildDelete();


        Assert.assertEquals(201, invocation.invoke().getStatus());
        Assert.assertEquals(201, invocation.invoke().getStatus());

        Response response = invocation.invoke();


        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals(null, response.getHeaderString("result-count"));
    }

    private static class RepositoryClientConfig extends ClientConfig


    {
        /**
         * Configures the client for repository service access.
         */
        public RepositoryClientConfig() {
            register(JacksonFeature.class);
            register(new LoggingFilter(Logger.getLogger(RepositoryClientConfig.class.getName() + ".jersey"), false));
        }
    }


    private static class BodyCase3 {
        String given;
        String other;

        public String getGiven() {
            return given;
        }

        public String getOther() {
            return other;
        }

        public void setOther(String other) {
            this.other = other;
        }

        public void setGiven(String given) {
            this.given = given;
        }
    }

}
