package com.github.charleslzq.aliyun.loghub.config;

import com.aliyun.openservices.log.producer.ProjectConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ConfigurationProperties(
        prefix = "ali.log-hub"
)
@Data
public class LogHubProjectProperties {
    /**
     * available accounts list
     */
    private List<LogHubAccountConfig> accounts = new ArrayList<>();

    /**
     * generate project configs for all accounts
     * @return
     */
    public List<ProjectConfig> generateProjectConfig() {
        return accounts.stream()
                .map(LogHubAccountConfig::generateProjectConfig)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
