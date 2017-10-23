package com.github.charleslzq.aliyun.loghub.listener.filter;

import com.aliyun.openservices.log.common.LogGroupData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Charles on 3/3/2017.
 */
public interface LogGroupFilter {
    boolean accept(LogGroupData logGroupData);

    class AcceptAll implements LogGroupFilter {
        @Override
        public boolean accept(LogGroupData logGroupData) {
            return true;
        }
    }

    class Composite implements LogGroupFilter {
        private final List<LogGroupFilter> groupFilters;

        public Composite(List<LogGroupFilter> groupFilters) {
            this.groupFilters = groupFilters;
        }

        @Override
        public boolean accept(LogGroupData logGroupData) {
            return groupFilters.stream().allMatch(logGroupFilter -> logGroupFilter.accept(logGroupData));
        }
    }

    class Topics implements LogGroupFilter {
        private final Set<String> topics = new HashSet<>();

        public Topics(String[] topics) {
            this.topics.addAll(Arrays.asList(topics));
        }

        @Override
        public boolean accept(LogGroupData logGroupData) {
            return topics.contains(logGroupData.GetLogGroup().getTopic());
        }
    }
}
