package com.objectpartners.plummer.graylog.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectpartners.plummer.graylog.graylog.GraylogRestInterface;
import com.objectpartners.plummer.graylog.service.QuoteService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ScheduledJobs {

    @Inject
    protected ObjectMapper mapper;

    @Inject
    protected GraylogRestInterface graylog;

    @Inject
    protected QuoteService quoteService;

    @Scheduled(initialDelay = 15000L, fixedDelay = 500L)
    public void generateQuote() throws Exception {
        String symbol = RandomStringUtils.randomAlphabetic(3).toUpperCase();
        quoteService.quote(symbol);
    }

    @Scheduled(initialDelay = 15000L, fixedDelay = 1000L)
    public void generateException() throws Exception {
        throw new Exception("Error during quote generation");
    }
}
