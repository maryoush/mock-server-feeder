package com.hybris.poc.dirs;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hybris.poc.jaxb.CaseMarshaller;
import com.hybris.poc.jaxb.MapUnmarshaller;
import com.hybris.poc.jaxb.model.Case;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.matchers.Times;
import org.mockserver.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by i303813 on 05/02/15.
 */
public class LocalFilesystemMockFeeder implements MockFeeder {

    private static enum ALLOWED_FILE {
        GET("get.json"), POST("post.json"), PUT("put.json"), DELETE("delete.json");

        final String fileName;

        ALLOWED_FILE(final String fileName) {
            this.fileName = fileName;
        }

        HttpRequest withMethod(final HttpRequest given) {
            return given.withMethod(name());
        }


    }


    private static final Logger LOG = Logger.getLogger(LocalFilesystemMockFeeder.class.getName());

    private final CaseMarshaller marshaller = new CaseMarshaller();

    private final MapUnmarshaller unmarshaller = new MapUnmarshaller();

    private final String rootFolder;


    private ALLOWED_FILE asAllowedFile(final Path file) {
        final String fileName = file.getFileName().toFile().getName();
        if (fileName.equalsIgnoreCase(ALLOWED_FILE.GET.fileName)) {
            return ALLOWED_FILE.GET;
        } else if (fileName.equalsIgnoreCase(ALLOWED_FILE.POST.fileName)) {
            return ALLOWED_FILE.POST;
        } else if (fileName.equalsIgnoreCase(ALLOWED_FILE.PUT.fileName)) {
            return ALLOWED_FILE.PUT;
        } else if (fileName.equalsIgnoreCase(ALLOWED_FILE.DELETE.fileName)) {
            return ALLOWED_FILE.DELETE;
        }
        return null;
    }


    public LocalFilesystemMockFeeder(final String root) {
        this.rootFolder = root;
    }


    @Override
    public MockServerClient feed(final MockServerClient mockServer) {
        final Path root = Paths.get(rootFolder);


        LOG.fine("starting " + rootFolder);

        try {
            Files.walkFileTree(root, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    LOG.fine("relative " + (root.relativize(file)));

                    final ALLOWED_FILE fileEnum = asAllowedFile(file);


                    if (fileEnum != null) {

                        final Case caseAbstraction = marshaller.marshall(Files.newInputStream(file));
                        final HttpRequest request = getHttpRequest(root.relativize(file.getParent()), caseAbstraction);

                        mockServer.when(fileEnum.withMethod(request), getTimes(caseAbstraction))
                                .respond(
                                        getHttpResponse(caseAbstraction)
                                );


                        LOG.info("Decorated  mock - " + mockServer);

                        if (file.getFileName().toString().contains("{") &&
                                file.getFileName().toString().contains("}")) {
                            LOG.fine("placeholder ...");
                        }

                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            throw new MockFeedException("Could not load local file configuration for " + rootFolder, e);
        }

        return mockServer;

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

    private HttpRequest getHttpRequest(Path relativePath, Case caseAbstraction) {

        return getHttpRequest(("/" + relativePath.toString()).replace("{", "").replace("}", ""), caseAbstraction);
    }

    private HttpRequest getHttpRequest(String relativePath, Case caseAbstraction) {
        LOG.info("Mocking path '" + relativePath + "'");
        LOG.info("with headers " + caseAbstraction.getWhen().getHeaderParams());

        return HttpRequest.request()//
                .withHeaders(asHeaders(caseAbstraction.getWhen().getHeaderParams()))
                .withQueryStringParameters(asParams(caseAbstraction.getWhen().getQueryParams()))
                .withPath(relativePath);
    }


    private List<Header> asHeaders(final Map<String, String> params) {
        final ImmutableList.Builder<Header> headers = ImmutableList.builder();

        params.forEach((key, value) -> headers.add(new Header(key, Lists.newArrayList(Splitter.on(",").split(value)))));

        return headers.build();
    }


    private Body asBody(final Object params) throws IOException {
        //final ImmutableList.Builder<Header> headers = ImmutableList.builder();

        Body body = StringBody.exact(asString(params));

        return body;
    }


    private List<Parameter> asParams(final Map<String, String> queryParamsMap) {
        final ImmutableList.Builder<Parameter> headers = ImmutableList.builder();

        queryParamsMap.forEach((key, value) -> headers.add(new Parameter(key, Lists.newArrayList(Splitter.on(",").split(value)))));

        return headers.build();
    }


    private String asString(final Object params) throws IOException {
        //final ImmutableList.Builder<Header> headers = ImmutableList.builder();

        return (unmarshaller.marshall(params));
    }


}
