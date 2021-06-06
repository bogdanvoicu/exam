package com.luxon.assignment.dto;

import com.luxon.assignment.entity.Account;
import com.luxon.assignment.enums.Instrument;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class ExchangeRequestDto {

    @NotNull
    private Integer accountId;

    @NotNull
    private ExchangeType exchangeType;

    @NotNull
    private Instrument input;

    private Instrument output;

    private Double amount;

    private String walletAddress;




    //TODO - add more relevant fields here for some generic request

    public enum ExchangeType {
        BUY,SELL,SEND
    }
}
