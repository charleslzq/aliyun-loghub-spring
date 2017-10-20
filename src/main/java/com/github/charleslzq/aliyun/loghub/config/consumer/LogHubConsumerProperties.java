package com.github.charleslzq.aliyun.loghub.config.consumer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(
        prefix = "ali.log-hub"
)
@Data
public class LogHubConsumerProperties {

    /**
     * the working consumers
     */
    private Map<String, LogHubConsumerConfig> consumers;
}
