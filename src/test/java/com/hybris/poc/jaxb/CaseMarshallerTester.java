package com.hybris.poc.jaxb;

import com.hybris.poc.jaxb.model.Case;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by i303813 on 05/02/15.
 */
public class CaseMarshallerTester {


    @Test
    public void assureSerialization() throws IOException {
        CaseUnmarshaller marshaller = new CaseUnmarshaller();
        try (final InputStream stream =
                     CaseMarshallerTester.class.getClassLoader()
                             .getResourceAsStream("mockserver/{testTenant}/configurations/get.json")) {

            final Case jaxb = marshaller.unmarshall(stream);

            Assert.assertNotNull(jaxb);

            Assert.assertNotNull(jaxb.getWhen());
            Assert.assertNotNull(jaxb.getWhen().getTimes());
            Assert.assertNotNull(jaxb.getWhen().getQueryParams());


            Assert.assertNotNull(jaxb.getThen());
            Assert.assertNotNull(jaxb.getThen().getResponseStatus());
            Assert.assertNotNull(jaxb.getThen().getResponseDelay());
            Assert.assertNotNull(jaxb.getThen().getResponseBody());


        }
    }

//    public static void main(String[] args) throws IOException {
//
//        CaseMarshaller marshaller = new CaseMarshaller();
//        try (final InputStream stream =
//                     CaseMarshallerTester.class.getClassLoader()
//                             .getResourceAsStream("configurations/get.json")) {
//
//            final Case jaxb = marshaller.unmarshall(stream);
//
//            System.out.println(jaxb);
//
//            System.out.println(jaxb.getWhen().getBody());
//
//            System.out.println(jaxb.getWhen().getQueryParams());
//
//            System.out.println(jaxb.getThen().getResponseDelay());
//
//            System.out.println(jaxb.getThen().getResponseBody());
//
//
//        }
//
//
//        try (final InputStream stream =
//                     CaseMarshallerTester.class.getClassLoader()
//                             .getResourceAsStream("configurations/post.json")) {
//
//            final Case jaxb = marshaller.unmarshall(stream);
//
//            System.out.println(jaxb);
//
//
//        }
//
//
//    }

}
