package com.hybris.poc;

import com.google.common.collect.ImmutableMap;
import com.hybris.poc.feeder.LocalFilesystemMockFeeder;
import com.hybris.poc.feeder.MockFeeder;
import com.hybris.poc.feeder.MockServerClientRunner;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * Created by i303813 on 05/02/15.
 */
public class ClientTester {

    private static final Logger LOG = Logger.getLogger(ClientTester.class.getName());


    private static final Client client = ClientBuilder.newClient(new RepositoryClientConfig());

    private static final String ROOT_FOLDER = ClientTester.class.getClassLoader().getResource("mockserver").getPath();

    private MockServerClient clientMock;

    @Before
    public void prepare() {

        final MockFeeder feeder = new LocalFilesystemMockFeeder(ROOT_FOLDER);

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
        LOG.fine(response.readEntity(String.class));
    }


    @Test
    public void assurePostWithBody() {


        SimpleBody simpleBody = new SimpleBody();
        simpleBody.given = "value";
        simpleBody.other = "thing";

        final Response response = client.target("http://localhost:8001")
                .path("testTenant")
                .path("configurations")
                .queryParam("pageNumber", 1)
                .request()
                .header("hybris-tenant", "foo")
                .header("hybris-app", "bar")
                .buildPost(Entity.entity(simpleBody, MediaType.APPLICATION_JSON_TYPE))
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


    @Test
    public void assureCallWithTimeout() throws ExecutionException, InterruptedException {

        final Invocation invocation = client.target("http://localhost:8001")
                .path("testTenant")
                .path("configurations")
                .path("key2")
                .request()
                .buildDelete();


        Future<Response> timeoutedResponse = invocation.submit();

        try {
            timeoutedResponse.get(1, TimeUnit.SECONDS);
            Assert.fail("Should have timeout ....");
        } catch (TimeoutException e) {
            //fine here
        }

        try {
            Response response = timeoutedResponse.get(3, TimeUnit.SECONDS);
            Assert.assertEquals(500, response.getStatus());
        } catch (TimeoutException e) {
            Assert.fail("Should have not timeout now ....");
        }

    }


    /**
     * Body should match criteria :
     * <p/>
     * {@code
     * <p/>
     * "given" : "some-value"
     * <p/>
     * }
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void assureResponseForBodyHavingGivenValue() throws ExecutionException, InterruptedException {


        Assert.assertEquals(299, responseForMethodAndBody(HttpMethod.PUT, ImmutableMap.of("given", "some-value")).getStatus());

        Assert.assertEquals(404, responseForMethodAndBody(HttpMethod.PUT, ImmutableMap.of("given", "")).getStatus());

        Assert.assertEquals(404, responseForMethodAndBody(HttpMethod.PUT, ImmutableMap.of("given", "someBlah")).getStatus());


    }


    /**
     * Body should match criteria :
     * <p/>
     * {@code
     * <p/>
     * header :
     * "header-key": ".*"
     * <p/>
     * }
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void assureResponseForHeaderWithWildCard() throws ExecutionException, InterruptedException {


        Assert.assertEquals(221, responseForMethodHeadersAndBody(HttpMethod.POST, ImmutableMap.of("header-key", "some"), ImmutableMap.of("key", "value")).getStatus());

        Assert.assertEquals(221, responseForMethodHeadersAndBody(HttpMethod.POST, ImmutableMap.of("header-key", "other"), ImmutableMap.of("key", "value")).getStatus());

        Assert.assertEquals(404, responseForMethodHeadersAndBody(HttpMethod.POST, ImmutableMap.of("header-other", "other"), ImmutableMap.of("key", "value")).getStatus());

        Assert.assertEquals(404, responseForMethodHeadersAndBody(HttpMethod.POST, ImmutableMap.of("header-other", "other"), ImmutableMap.of("key", "value")).getStatus());


    }

    private Response responseForMethodAndBody(final String method, final Map body) {
        final Invocation invocation = client.target("http://localhost:8001")
                .path("testTenant")
                .path("configurations")
                .path("key2")
                .request()
                .build(method, Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));


        return invocation.invoke();
    }

    private Response responseForMethodHeadersAndBody(final String method, final Map<String, String> headers, final Map body) {
        final Invocation.Builder invocationBuilder = client.target("http://localhost:8001")
                .path("testTenant")
                .path("configurations")
                .path("key2")
                .request();

        headers.entrySet().forEach(entry -> invocationBuilder.header(entry.getKey(), entry.getValue()));

        return invocationBuilder.build(method, Entity.entity(body, MediaType.APPLICATION_JSON_TYPE)).invoke();

    }


    /**
     * Body should match criteria :
     * <p/>
     * {@code
     * <p/>
     * ".*" : "someValue"
     * <p/>
     * }
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void assureBodyShouldHaveAnyKeyWithSomeValue() throws ExecutionException, InterruptedException {

//        Map simpleBody = new SimpleBody();
//        simpleBody.setOther("thing");
//
//        final Invocation invocation = client.target("http://localhost:8001")
//                .path("testTenant")
//                .path("configurations")
//                .path("key2")
//                .request()
//                .buildPut(Entity.entity(simpleBody, MediaType.APPLICATION_JSON_TYPE));
//
//
//        Response response = invocation.invoke();
//        Assert.assertEquals(201, response.getStatus());


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


    private static class SimpleBody {
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

    private static class SimpleBodyGiven {

        String given;

        public void setGiven(String given) {
            this.given = given;
        }

        public String getGiven() {
            return given;
        }
    }

}
