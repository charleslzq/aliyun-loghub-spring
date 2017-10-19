package com.github.charleslzq.aliyun.loghub.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(
        prefix = "ali.log-hub"
)
@Data
public class LogHubProjectProperties {
    /**
     * available loghub projects
     */
    private List<LogHubProjectConfig> projects = new ArrayList<>();
}
