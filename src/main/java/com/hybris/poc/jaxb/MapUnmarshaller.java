package com.hybris.poc.jaxb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hybris.poc.jaxb.model.Case;
import com.hybris.poc.jaxb.model.Then;
import com.hybris.poc.jaxb.model.When;

import java.io.IOException;

/**
 * Created by i303813 on 06/02/15.
 */
public class MapUnmarshaller {

    private final ObjectWriter ow;

    public MapUnmarshaller() {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerSubtypes(Case.class, When.class, Then.class);

        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, false);

        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        ow = objectMapper.writer();

    }

    public String marshall(final Object object) throws IOException {
        ow.withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }

}
