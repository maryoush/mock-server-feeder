package com.hybris.poc.jaxb;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

/**
 * Marshals any object in JSON string.
 */
public class StringBodySerializer extends JsonDeserializer<String> {

//    private final StringMarshaller marshaller;
//
//    public StringBodySerializer(StringMarshaller marshaller) {
//        this.marshaller = marshaller;
//    }
//
//
//    @Override
//    public void serialize(final Body value, final JsonGenerator jsonGenerator, final SerializerProvider provider) throws IOException, JsonProcessingException {
//        jsonGenerator.writeStartObject();
//        jsonGenerator.writeString(marshaller.marshall(value));
//        jsonGenerator.writeEndObject();
//    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final ObjectMapper objectMapper = (ObjectMapper)jsonParser.getCodec();

        final TreeNode node = objectMapper.readTree(jsonParser);
        if( node instanceof TextNode)
        {
            return ((TextNode)node).asText();
        }
        return objectMapper.writeValueAsString(node);

    }
}
