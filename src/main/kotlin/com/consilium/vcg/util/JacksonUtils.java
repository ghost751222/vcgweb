package com.consilium.vcg.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JacksonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> String toJsonString(T cls) throws JsonProcessingException {
        return objectMapper.writeValueAsString(cls);
    }


    public static JsonNode toJsonNode(String jsonData) throws JsonProcessingException, IOException {
        return objectMapper.readTree(jsonData);
    }


    public static <T> T toClass(String jsonData, Class<T> type) throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(jsonData, type);
    }


    public static List<Map<String, Object>> getMapList(Map<String, Object> map, String field) {
        Object o = map.get(field);
        return o == null ? new ArrayList<Map<String, Object>>() : (List<Map<String, Object>>) o;
    }

    public static Map<String, Object> jsonStrToMap(String json) throws JsonParseException, JsonMappingException, IOException{
        return jsonStrToObject(json, new TypeReference<Map<String, Object>>(){});
    }

    public static <T> T jsonStrToObject(String json, TypeReference<T> typeReference) throws JsonParseException, JsonMappingException, IOException {
        return new ObjectMapper().readValue(json, typeReference);
    }

    public static String objectToJsonStr(Object o) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(o);
    }

    public static List<String> jsonStrToList(String jsonData) throws IOException {
        return jsonStrToObject(jsonData,new TypeReference<List<String>>(){});
    }

}
