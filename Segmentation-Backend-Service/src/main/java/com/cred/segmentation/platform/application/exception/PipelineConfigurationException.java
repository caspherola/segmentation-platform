package com.cred.segmentation.platform.application.exception;

public class PipelineConfigurationException extends RuntimeException {

    public PipelineConfigurationException(String message) {
        super(message);
    }

    public PipelineConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
