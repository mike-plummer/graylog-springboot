package com.objectpartners.plummer.graylog.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class QuoteResource {
    private String symbol;
    private Double price;
    private Exchange exchange;
}
