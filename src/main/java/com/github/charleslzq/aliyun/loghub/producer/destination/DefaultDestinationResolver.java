package com.github.charleslzq.aliyun.loghub.producer.destination;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "ali.log-hub.producer"
)
@Data
public class DefaultDestinationResolver implements DestinationResolver<LogHubDestination> {
    /**
     * separator of log-hub destination config string
     */
    private String separator = ":";

    @Override
    public LogHubDestination resolveDestination(String destination) {
        String[] parts = destination.split(separator);
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("Wrong format of destination " + destination + " with separator " + separator);
        }
        String project = parts[0];
        String store = parts[1];
        String topic = parts.length == 3 ? parts[2] : "";
        return new LogHubDestination(project, store, topic);
    }
}
