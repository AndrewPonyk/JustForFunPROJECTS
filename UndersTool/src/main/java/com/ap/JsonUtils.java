package com.ap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Map;

public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, String> loadJson(String path) {
        if (!new java.io.File(path).exists()) {
            return null;
        }

        try {
            String content = FileUtils.readFileToString(new File(path));

            return (Map<String, String>) objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
