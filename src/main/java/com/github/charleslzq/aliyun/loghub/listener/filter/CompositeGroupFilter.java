package com.github.charleslzq.aliyun.loghub.listener.filter;

import com.aliyun.openservices.log.common.LogGroupData;

import java.util.List;

public class CompositeGroupFilter implements LogGroupFilter {
    private final List<LogGroupFilter> groupFilters;

    public CompositeGroupFilter(List<LogGroupFilter> groupFilters) {
        this.groupFilters = groupFilters;
    }

    @Override
    public boolean accept(LogGroupData logGroupData) {
        return groupFilters.stream().allMatch(logGroupFilter -> logGroupFilter.accept(logGroupData));
    }
}
