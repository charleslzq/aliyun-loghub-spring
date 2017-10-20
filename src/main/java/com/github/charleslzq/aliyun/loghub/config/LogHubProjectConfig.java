package com.github.charleslzq.aliyun.loghub.config;

import com.aliyun.openservices.log.producer.ProjectConfig;
import lombok.Data;

/**
 * Created by Eric Charles on 2/27/2017.
 */
@Data
public class LogHubProjectConfig {

    /**
     * the project name
     */
    private String project;

    /**
     * the endpoint to access alibaba loghub service
     */
    private String endpoint;
}
