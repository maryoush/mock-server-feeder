package com.hybris.poc.feeder;

import com.hybris.poc.jaxb.CaseUnmarshaller;
import com.hybris.poc.jaxb.model.Case;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.model.HttpRequest;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

/**
 * Created by i303813 on 05/02/15.
 */
public class LocalFilesystemMockFeeder implements MockFeeder {

    private static final Logger LOG = Logger.getLogger(LocalFilesystemMockFeeder.class.getName());

    public static enum ACTION {
        GET("get.json"), POST("post.json"), PUT("put.json"), DELETE("delete.json");

        final String fileName;

        ACTION(final String fileName) {
            this.fileName = fileName;
        }

        HttpRequest withRequest(final HttpRequest given) {
            return given.withMethod(name());
        }

    }

    private final CaseUnmarshaller marshaller = new CaseUnmarshaller();


    private final String rootFolder;


    private ACTION asAllowedFile(final Path file) {
        final String fileName = file.getFileName().toFile().getName();
        if (fileName.equalsIgnoreCase(ACTION.GET.fileName)) {
            return ACTION.GET;
        } else if (fileName.equalsIgnoreCase(ACTION.POST.fileName)) {
            return ACTION.POST;
        } else if (fileName.equalsIgnoreCase(ACTION.PUT.fileName)) {
            return ACTION.PUT;
        } else if (fileName.equalsIgnoreCase(ACTION.DELETE.fileName)) {
            return ACTION.DELETE;
        }
        return null;
    }


    public LocalFilesystemMockFeeder(final String root) {
        this.rootFolder = root;
    }


    @Override
    public void feed(MockServerConfigurer caseInterceptor) {
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



                    final ACTION action = asAllowedFile(file);



                    if (action != null) {

                        LOG.info("relative for action "+action+" , " + (root.relativize(file)));

                        final Case caseAbstraction = marshaller.unmarshall(Files.newInputStream(file));

                        final Path path = root.relativize(file.getParent());

                        caseInterceptor.onCase(path, action, caseAbstraction);



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

    }


}
