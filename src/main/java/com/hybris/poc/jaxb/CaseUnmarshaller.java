package com.hybris.poc.jaxb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hybris.poc.jaxb.model.Case;
import com.hybris.poc.jaxb.model.Then;
import com.hybris.poc.jaxb.model.When;

import java.io.IOException;
import java.io.InputStream;

/**
 * Un-marshalls the stream into {@link com.hybris.poc.jaxb.model.Case} rooted jaxb model.
 */
public class CaseUnmarshaller {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CaseUnmarshaller() {

        objectMapper.registerSubtypes(Case.class, When.class, Then.class);

        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, false);

        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        //objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);



//         SimpleModule testModule = new SimpleModule("StringModule", new Version(1, 0, 0, null))
//                     .addDeserializer(Body.class, new StringMarshaller());
//                 mapper.registerModule(testModule);

    }

    public Case unmarshall(final InputStream stream) throws IOException {
        return objectMapper.readValue(stream, Case.class);
    }

}
