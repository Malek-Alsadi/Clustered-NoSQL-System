package com.atypon.demo.Database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public interface JsonArraymaker {
    default void createEmptyJsonArray(File file){
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode arrayNode = objectMapper.createArrayNode();

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.set("myArray", arrayNode);
        try {
            objectMapper.writeValue(file, objectNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
