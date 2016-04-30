package com.objectpartners.plummer.graylog.service;

import com.objectpartners.plummer.graylog.domain.Exchange;
import com.objectpartners.plummer.graylog.domain.QuoteResource;
import org.apache.commons.lang3.RandomUtils;

import javax.inject.Named;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Named
public class QuoteService {

    public QuoteResource quote(String symbol) {
        return new QuoteResource(symbol,
                BigDecimal.valueOf(RandomUtils.nextDouble(0, 100)).setScale(2, RoundingMode.HALF_UP).doubleValue(),
                Exchange.values()[RandomUtils.nextInt(0, Exchange.values().length)]);
    }
}
