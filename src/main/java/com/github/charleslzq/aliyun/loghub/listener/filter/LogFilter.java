package com.github.charleslzq.aliyun.loghub.listener.filter;

import com.aliyun.openservices.log.common.Logs;

import java.util.List;

/**
 * Created by Charles on 3/3/2017.
 */
public interface LogFilter {
    boolean accept(Logs.Log log);

    class AcceptAll implements LogFilter {
        @Override
        public boolean accept(Logs.Log log) {
            return true;
        }
    }

    class Composite implements LogFilter {
        private final List<LogFilter> logFilters;

        public Composite(List<LogFilter> logFilters) {
            this.logFilters = logFilters;
        }

        @Override
        public boolean accept(Logs.Log log) {
            return logFilters.stream().allMatch(logFilter -> logFilter.accept(log));
        }
    }
}
