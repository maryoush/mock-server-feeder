package com.hybris.poc.jaxb;

import com.hybris.poc.jaxb.model.Case;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by i303813 on 05/02/15.
 */
public class CaseMarshallerTester {


    public static void main(String[] args) throws IOException {

        CaseMarshaller marshaller = new CaseMarshaller();
        try (final InputStream stream =
                     CaseMarshallerTester.class.getClassLoader()
                             .getResourceAsStream("configurations/get.json")) {

            final Case jaxb = marshaller.marshall(stream);

            System.out.println(jaxb);

            System.out.println(jaxb.getWhen().getBody());

            System.out.println(jaxb.getWhen().getQueryParams());

            System.out.println(jaxb.getThen().getResponseDelay());

            System.out.println(jaxb.getThen().getResponseBody());


        }


        try (final InputStream stream =
                     CaseMarshallerTester.class.getClassLoader()
                             .getResourceAsStream("configurations/post.json")) {

            final Case jaxb = marshaller.marshall(stream);

            System.out.println(jaxb);


        }


    }

}
