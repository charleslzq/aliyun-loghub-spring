package com.github.charleslzq.aliyun.loghub.config;

import com.aliyun.openservices.log.producer.ProjectConfig;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class LogHubAccountConfig {

    /**
     * the accessId of which holds the appropriate authority
     */
    private String accessId;

    /**
     * the accessKey of which holds the appropriate authority
     */
    private String accessKey;

    /**
     * available loghub projects
     */
    private List<LogHubProjectConfig> projects = new ArrayList<>();

    /**
     * generate project configs for this account
     *
     * @return
     */
    public List<ProjectConfig> generateProjectConfig() {
        return projects.stream()
                .map(project -> new ProjectConfig(project.getProject(), project.getEndpoint(), accessId, accessKey))
                .collect(Collectors.toList());
    }
}
