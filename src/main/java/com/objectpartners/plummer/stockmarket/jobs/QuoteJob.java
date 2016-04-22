package com.objectpartners.plummer.stockmarket.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.InetAddress;
import java.net.UnknownHostException;

@Named
public class QuoteJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteService.class);

    @Inject
    protected ObjectMapper mapper;

    @Inject
    protected GraylogRestInterface graylog;

    @Inject
    protected QuoteService quoteService;

    @Scheduled(initialDelay = 15000L, fixedDelay = 1000L)
    public void quoteViaTcpGelf() {
        String symbol = RandomStringUtils.randomAlphabetic(3).toUpperCase();
        QuoteResource quote = quoteService.quote(symbol);
        LOGGER.info("Generated quote - {}@{}", quote.getSymbol(), quote.getPrice());
    }

    @Scheduled(initialDelay = 15000L, fixedDelay = 1000L)
    public void quoteViaHttpGelf() throws UnknownHostException, JsonProcessingException {
        String symbol = RandomStringUtils.randomAlphabetic(3).toUpperCase();
        QuoteResource quote = quoteService.quote(symbol);

        GelfMessage message = new GelfMessage();
        message.setHost(InetAddress.getLocalHost().getHostName());
        message.setTimestamp(System.currentTimeMillis());
        message.setLevel(4);
        message.setShortMessage(String.format("Quote %s@%f", quote.getSymbol(), quote.getPrice()));
        message.setFullMessage(mapper.writeValueAsString(quote));
        graylog.logEvent(message);
    }
}
