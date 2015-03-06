package com.hybris.poc.jaxb;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.hybris.poc.jaxb.model.Case;
import com.hybris.poc.jaxb.model.Then;
import com.hybris.poc.jaxb.model.When;

import java.io.IOException;

/**
 * Marshals any object in JSON string.
 */
public class StringMarshaller{

    private final ObjectWriter ow;

    public StringMarshaller() {

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
