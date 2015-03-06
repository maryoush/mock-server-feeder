package com.hybris.poc.feeder;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hybris.poc.jaxb.StringMarshaller;
import com.hybris.poc.jaxb.model.Case;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by i303813 on 06/03/15.
 */
public class MockServerConfigurer {

    private static final Logger LOG = Logger.getLogger(MockServerConfigurer.class.getName());

    private final StringMarshaller unmarshaller = new StringMarshaller();

    private final MockServerClient mockServer;

    public MockServerConfigurer(MockServerClient mockServer) {
        this.mockServer = mockServer;
    }


    public void onCase(final Path path, final LocalFilesystemMockFeeder.ACTION action, final Case caseAbstraction)
            throws IOException {

        final HttpRequest request = getHttpRequest(path, caseAbstraction);

        LOG.info("Decorating   mock for path " + path + " and method " + action.name());

        mockServer.when(action.withRequest(request), getTimes(caseAbstraction))
                .respond(getHttpResponse(caseAbstraction));


        LOG.info("Decorated  mock - " + mockServer);

    }


    private HttpResponse getHttpResponse(Case caseAbstraction) throws IOException {
        return HttpResponse.response()
                .withStatusCode(caseAbstraction.getThen().getResponseStatus())
                .withHeaders(asHeaders(caseAbstraction.getThen().getResponseHeaders()))
                .withBody(asString(caseAbstraction.getThen().getResponseBody()))
                .withDelay(new Delay(TimeUnit.MILLISECONDS, caseAbstraction.getThen().getResponseDelay()));
    }

    private Times getTimes(Case caseAbstraction) {
        LOG.info("times  " + (caseAbstraction.getWhen().getTimes() == null ? " always " : caseAbstraction.getWhen().getTimes()));
        return caseAbstraction.getWhen().getTimes() == null ? Times.unlimited() : Times.exactly(caseAbstraction.getWhen().getTimes());
    }

    private HttpRequest getHttpRequest(Path relativePath, Case caseAbstraction) throws IOException {

        return getHttpRequest(("/" + relativePath.toString()).replace("{", "").replace("}", ""), caseAbstraction);
    }

    private HttpRequest getHttpRequest(String relativePath, Case caseAbstraction) throws IOException {
        LOG.info("Mocking path '" + relativePath + "'");
        LOG.info("with headers " + caseAbstraction.getWhen().getHeaderParams());

        return HttpRequest.request()//
                .withHeaders(asHeaders(caseAbstraction.getWhen().getHeaderParams()))
                .withQueryStringParameters(asParams(caseAbstraction.getWhen().getQueryParams()))
                .withBody(asBody(caseAbstraction.getWhen().getBody()))
                .withPath(relativePath);
    }


    private List<Header> asHeaders(final Map<String, String> params) {

        if (params == null) {
            return Collections.emptyList();
        }
        final ImmutableList.Builder<Header> headers = ImmutableList.builder();

        params.forEach((key, value) -> headers.add(new Header(key, Lists.newArrayList(Splitter.on(",").split(value)))));

        return headers.build();
    }


    private Body asBody(final String params) throws IOException {
        //final ImmutableList.Builder<Header> headers = ImmutableList.builder();

        if (params == null) {
            return null;
        }

        LOG.info("Providing body "+params);

        final Body body = StringBody.json(params);

        return body;
    }


    private List<Parameter> asParams(final Map<String, String> queryParamsMap) {

        if (queryParamsMap == null) {
            return Collections.emptyList();
        }


        final ImmutableList.Builder<Parameter> headers = ImmutableList.builder();

        queryParamsMap.forEach((key, value) -> headers.add(new Parameter(key, Lists.newArrayList(Splitter.on(",").split(value)))));

        return headers.build();
    }


    private String asString(final Object params) throws IOException {
        //final ImmutableList.Builder<Header> headers = ImmutableList.builder();

        return (unmarshaller.marshall(params));
    }

}
