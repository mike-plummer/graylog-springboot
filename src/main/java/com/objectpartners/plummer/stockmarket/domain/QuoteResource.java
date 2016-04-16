package com.objectpartners.plummer.stockmarket.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class QuoteResource extends AbstractResource {
    private String symbol;
    private Double price;
    private Exchange exchange;
}
