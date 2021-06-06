package com.luxon.assignment.service;

import com.luxon.assignment.dto.ExchangeRequestDto;
import com.luxon.assignment.entity.Account;
import com.luxon.assignment.entity.Balance;
import com.luxon.assignment.repository.AccountRepository;
import com.luxon.assignment.repository.BalanceRepository;
import com.luxon.assignment.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final AccountRepository accountRepository;
    private final RateRepository rateRepository;
    private final BalanceRepository balanceRepository;

    /**
     * Steps:
     * 1. validate if the source has the amount
     * 2. based on operation call the right function
     *
     * @param exchangeRequestDto
     * @return
     */
    @Transactional
    public ResponseEntity<?> execute(ExchangeRequestDto exchangeRequestDto) {
        try {
            if (validateAmount(exchangeRequestDto)) {

                switch (exchangeRequestDto.getExchangeType()) {
                    case BUY:
                        buy(exchangeRequestDto);
                        return new ResponseEntity<>("Buy operation completed", HttpStatus.OK);
                    case SELL:
                        sell(exchangeRequestDto);
                        break;
                    case SEND:
                        send(exchangeRequestDto);
                        break;
                }

            } else return new ResponseEntity<>(
                    "Amount not enough",
                    HttpStatus.BAD_REQUEST);

        } catch (Exception ex) {
            return new ResponseEntity<>(
                    ex.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(200);
    }

    @PostConstruct
    public void doSome() {
        Account account = accountRepository.save(Account.builder().id(1).name("Jack Black").build());
        List<Balance> userBalance = Collections.singletonList(Balance.builder().account(account).build());
        account.setBalances(userBalance);
    }

    /**
     * check if for the current account the amount to be exchanged/sell is smaller that the ballance
     *
     * @param exchangeRequestDto
     * @return
     */
    private boolean validateAmount(ExchangeRequestDto exchangeRequestDto) throws Exception {
        Account account = accountRepository.findById(exchangeRequestDto.getAccountId()).orElseThrow(() -> new Exception("Account does not exist!"));
        List<Balance> balances = account.getBalances().stream()
                .filter(x -> x.getInstrument().equals(exchangeRequestDto.getInput()))
                .collect(Collectors.toList());

        if (balances.size() == 0) {
            throw new Exception("Account does not have open instrument !");
        } else return exchangeRequestDto.getAmount() <= balances.get(0).getQty();

    }

    private void buy(ExchangeRequestDto exchangeRequestDto) {
        Account account = accountRepository.findById(exchangeRequestDto.getAccountId()).get();
        Balance sourceBalance = account.getBalances().stream()
                .filter(x -> x.getInstrument().equals(exchangeRequestDto.getInput()))
                .collect(Collectors.toList()).get(0);
        sourceBalance.setQty(sourceBalance.getQty() - exchangeRequestDto.getAmount());

        List<Balance> destinationBalances = account.getBalances().stream()
                .filter(x -> x.getInstrument().equals(exchangeRequestDto.getOutput()))
                .collect(Collectors.toList());
        if (destinationBalances.size() == 0) {
            Balance newBalance = new Balance();
            newBalance.setInstrument(exchangeRequestDto.getOutput());
            newBalance.setAccount(account);
            destinationBalances.add(newBalance);
        }
        Balance destinationBalance = destinationBalances.get(0);

        destinationBalance.setQty(destinationBalance.getQty() +
                (exchangeRequestDto.getAmount() * rateRepository.findByInstrumentAndResultInstrument(exchangeRequestDto.getInput(), exchangeRequestDto.getOutput()).getValue()));

        balanceRepository.saveAll(account.getBalances());
    }

    private void sell(ExchangeRequestDto exchangeRequestDto) {
        throw new RuntimeException("Not implemented");
    }

    private void send(ExchangeRequestDto exchangeRequestDto) {
        throw new RuntimeException("Not implemented");
    }
}
