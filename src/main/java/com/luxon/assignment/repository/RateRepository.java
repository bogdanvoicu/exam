package com.luxon.assignment.repository;

import com.luxon.assignment.entity.Account;
import com.luxon.assignment.entity.Rate;
import com.luxon.assignment.enums.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Integer> {
    Rate findByInstrumentAndResultInstrument(Instrument instrument, Instrument resultInstrument);
}
