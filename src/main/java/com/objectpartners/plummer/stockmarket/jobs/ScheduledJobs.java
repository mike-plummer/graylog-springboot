package com.objectpartners.plummer.stockmarket.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.objectpartners.plummer.stockmarket.domain.QuoteResource;
import com.objectpartners.plummer.stockmarket.graylog.GelfMessage;
import com.objectpartners.plummer.stockmarket.graylog.GraylogRestInterface;
import com.objectpartners.plummer.stockmarket.service.QuoteService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.TimeUnit;

@Named
public class ScheduledJobs {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteService.class);

    @Inject
    protected ObjectMapper mapper;

    @Inject
    protected GraylogRestInterface graylog;

    @Inject
    protected QuoteService quoteService;

    @Scheduled(initialDelay = 15000L, fixedDelay = 500L)
    public void generateEvents() throws Exception {
        Stopwatch timer = Stopwatch.createStarted();
        String symbol = RandomStringUtils.randomAlphabetic(3).toUpperCase();
        QuoteResource quote = quoteService.quote(symbol);

        GelfMessage message = new GelfMessage();
        message.setShortMessage(String.format("Quote %s@%2.2f", quote.getSymbol(), quote.getPrice()));
        message.setFullMessage(mapper.writeValueAsString(quote));
        message.getAdditionalProperties().put("elapsed_time", timer.stop().elapsed(TimeUnit.MICROSECONDS));

        // Log message via Graylog HTTP Input
        graylog.logEvent(message);

        // Log message via Graylog UDP Logback Appender
        LOGGER.info("Generated quote - {}@{}", quote.getSymbol(), quote.getPrice());

        throw new Exception("Error during quote generation");
    }
}
