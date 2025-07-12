package com.cred.segmentation.platform.application.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.pipeline.config.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfiguration {
}

