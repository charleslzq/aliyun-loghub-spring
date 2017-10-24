package com.github.charleslzq.aliyun.loghub.config.producer;

import com.aliyun.openservices.log.producer.ProducerConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Charles on 2/27/2017.
 */
@ConfigurationProperties(
        prefix = "ali.log-hub.producer"
)
@Data
public class LogHubProducerProperties {
    /**
     * specify ip or host name as source field of loghub message
     */
    private SourceType source = SourceType.HOST_IP;
    /**
     * aliyun loghub producer configuration
     */
    private ProducerConfig config = new ProducerConfig();
}
