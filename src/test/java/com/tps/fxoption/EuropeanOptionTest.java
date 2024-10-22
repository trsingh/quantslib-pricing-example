package com.tps.fxoption;

import org.junit.jupiter.api.Test;
import org.quantlib.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class EuropeanOptionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EuropeanOptionTest.class);

    @Test
    public void testEuropeanOption() {
        var localDate = LocalDate.of(2023, 7, 28);
        var today = Date.of(localDate);

        Settings.instance().setEvaluationDate(today);

        // Set up the option parameters
        var strikePrice = 105.0;
        var maturityDate = LocalDate.of(2023, 12, 31);

        // Create the option objects
        var payoff = new PlainVanillaPayoff(Option.Type.Call, strikePrice);
        var exercise = new EuropeanExercise(Date.of(maturityDate));
        var europeanOption = new EuropeanOption(payoff, exercise);

        // Create the market data object
        var dayCounter = new Actual360();
        var calendar = new NullCalendar();

        var spotPrice = 100.0;
        var spotPriceHandle = new QuoteHandle(new SimpleQuote(spotPrice));

        var riskFreeRate = 0.05;
        var riskFreeRateHandle = new QuoteHandle(new SimpleQuote(riskFreeRate));

        var volatility = 0.2;
        var volatilityHandle = new QuoteHandle(new SimpleQuote(volatility));

        var yieldTermStructure = new YieldTermStructureHandle(new FlatForward(today, riskFreeRateHandle, dayCounter));
        var blackVolTermStructure = new BlackVolTermStructureHandle(new BlackConstantVol(today, calendar, volatilityHandle, dayCounter));

        // Create the AnalyticEuropeanEngine
        var process = new BlackScholesProcess(spotPriceHandle, yieldTermStructure, blackVolTermStructure);
        var engine = new AnalyticEuropeanEngine(process);

        // Calculate the NPV for the European option
        europeanOption.setPricingEngine(engine);
        var npv = europeanOption.NPV();

        // Print the result
        LOGGER.info("Option price: {}", npv);
    }
}