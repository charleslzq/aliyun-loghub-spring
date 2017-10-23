package com.github.charleslzq.aliyun.loghub.producer;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogHubDestination {
    private final String project;
    private final String store;
    private String topic;
}
