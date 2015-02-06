package com.hybris.poc.jaxb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.poc.jaxb.model.Case;
import com.hybris.poc.jaxb.model.Then;
import com.hybris.poc.jaxb.model.When;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by i303813 on 06/02/15.
 */
public class CaseMarshaller {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CaseMarshaller() {

        objectMapper.registerSubtypes(Case.class, When.class, Then.class);

        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, false);

        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    }

    public Case marshall(final InputStream stream) throws IOException {
        return objectMapper.readValue(stream, Case.class);
    }

}
