package com.cred.segmentation.platform.application.util;

import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PipelineTemplateLoader {
    private static final String TEMPLATE_BASE_PATH = "/Users/senyarav/workspace/segmentation-platform/";

    public static JSONObject loadTemplate(String templateName) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(TEMPLATE_BASE_PATH + templateName)));
            return new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load pipeline template: " + templateName, e);
        }
    }
}
