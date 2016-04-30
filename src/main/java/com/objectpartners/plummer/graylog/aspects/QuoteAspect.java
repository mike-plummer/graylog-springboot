package com.objectpartners.plummer.graylog.aspects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.objectpartners.plummer.graylog.domain.QuoteResource;
import com.objectpartners.plummer.graylog.graylog.GelfMessage;
import com.objectpartners.plummer.graylog.graylog.GraylogRestInterface;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.TimeUnit;

@Aspect
@Named
public class QuoteAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteAspect.class);

    @Inject
    protected ObjectMapper mapper;

    @Inject
    protected GraylogRestInterface graylog;

    @Around("execution(* com.objectpartners.plummer.graylog.service.QuoteService.*(..))")
    public QuoteResource errorThrown(ProceedingJoinPoint joinPoint) throws Throwable {
        Stopwatch timer = Stopwatch.createStarted();

        QuoteResource quote = (QuoteResource) joinPoint.proceed();

        // Log message via Graylog UDP Logback Appender
        LOGGER.info("Generated quote - {}@{}", quote.getSymbol(), quote.getPrice());

        // Log message via Graylog HTTP Input
        GelfMessage message = new GelfMessage();
        message.setShortMessage(String.format("Quote %s@%2.2f", quote.getSymbol(), quote.getPrice()));
        message.setFullMessage(mapper.writeValueAsString(quote));
        message.getAdditionalProperties().put("elapsed_time", timer.stop().elapsed(TimeUnit.MICROSECONDS));
        graylog.logEvent(message);

        return quote;
    }
}
