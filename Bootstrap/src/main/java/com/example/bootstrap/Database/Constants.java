package com.example.bootstrap.Database;

public interface Constants {
    default String getSchema(){
        String schema = "{\n" +
                "  \"definitions\": {},\n" +
                "  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n" +
                "  \"type\": \"object\",\n" +
                "  \"properties\": {\n" +
                "    \"Id\": {\n" +
                "      \"title\": \"Id\",\n" +
                "      \"type\": \"string\",\n" +
                "      \"default\": 0\n" +
                "    },\n" +
                "    \"Password\": {\n" +
                "      \"title\": \"Password\",\n" +
                "      \"type\": \"string\",\n" +
                "      \"default\": \"\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"required\": [\"Id\", \"Password\"],\n" +
                "  \"additionalProperties\": false\n" +
                "}";
        return schema;
    }

    default String getManager(){
        String manager = "{\"Id\" : \"id1111\",\"Password\" : \"password\"}";
        return manager;
    }
}
