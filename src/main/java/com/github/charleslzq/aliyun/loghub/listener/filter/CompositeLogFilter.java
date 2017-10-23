package com.github.charleslzq.aliyun.loghub.listener.filter;

import com.aliyun.openservices.log.common.Logs;

import java.util.List;

public class CompositeLogFilter implements LogFilter {
    private final List<LogFilter> logFilters;

    public CompositeLogFilter(List<LogFilter> logFilters) {
        this.logFilters = logFilters;
    }

    @Override
    public boolean accept(Logs.Log log) {
        return logFilters.stream().allMatch(logFilter -> logFilter.accept(log));
    }
}
