package com.objectpartners.plummer.stockmarket.service;

import com.objectpartners.plummer.stockmarket.domain.Exchange;
import com.objectpartners.plummer.stockmarket.domain.QuoteResource;
import org.apache.commons.lang3.RandomUtils;

import javax.inject.Named;

@Named
public class QuoteService {

    public QuoteResource quote(String symbol) {
        QuoteResource quote = new QuoteResource(symbol,
                RandomUtils.nextDouble(0, 100),
                Exchange.values()[RandomUtils.nextInt(0, Exchange.values().length)]);

        return quote;
    }
}
