package com.cred.segmentation.platform.application;

import com.cred.segmentation.platform.application.model.RuleDefinitionRequest;
import com.cred.segmentation.platform.application.model.SegmentCreationRequest;
import com.cred.segmentation.platform.application.service.SegmentPipelineService;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.cred.segmentation.platform.application.repository")
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.cred.segmentation.platform"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}