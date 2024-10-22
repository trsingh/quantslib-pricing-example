package com.tps.swap;

import org.junit.jupiter.api.Test;
import org.quantlib.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VanillaSwapPricingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VanillaSwapPricingTest.class);

    @Test
    public void priceSwap() {
        // Set up the evaluation date
        Date todaysDate = new Date(21, Month.October, 2024);
        Settings.instance().setEvaluationDate(todaysDate);

        // Market data
        double nominal = 1000000.0;

        double fixedRate = 0.04;
        double floatingSpread = 0.0; // No spread for this example

        Frequency fixedLegFrequency = Frequency.Annual;
        Frequency floatingLegFrequency = Frequency.Quarterly;
        BusinessDayConvention fixedLegConvention = BusinessDayConvention.Unadjusted;
        BusinessDayConvention floatingLegConvention = BusinessDayConvention.ModifiedFollowing;

        DayCounter fixedLegDayCounter = new Thirty360(Thirty360.Convention.ISDA);
        DayCounter floatingLegDayCounter = new Actual360();

        IborIndex sofr3m = new Sofr(new YieldTermStructureHandle(
                new FlatForward(todaysDate, 0.03, new Actual360())));

        // Schedule
        Date startDate = new Date(21, Month.October, 2024);
        Date endDate = new Date(21, Month.October, 2029);

        Schedule fixedSchedule = new Schedule(startDate, endDate, new Period(fixedLegFrequency), new UnitedStates(UnitedStates.Market.SOFR),
                fixedLegConvention, fixedLegConvention, DateGeneration.Rule.Forward, false);

        Schedule floatingSchedule = new Schedule(startDate, endDate, new Period(floatingLegFrequency), new UnitedStates(UnitedStates.Market.SOFR),
                floatingLegConvention, floatingLegConvention, DateGeneration.Rule.Forward, false);

        // Swap
        VanillaSwap swap = new VanillaSwap(VanillaSwap.Type.Payer, nominal, fixedSchedule, fixedRate, fixedLegDayCounter,
                floatingSchedule, sofr3m, floatingSpread, floatingLegDayCounter);

        // Pricing engine
        var discountingTermStructure = new YieldTermStructureHandle(
                new FlatForward(todaysDate, 0.01, new Actual360()));

        swap.setPricingEngine(new DiscountingSwapEngine(discountingTermStructure));

        // Output results
        LOGGER.info("NPV: " + swap.NPV());
        LOGGER.info("Fair Spread: " + swap.fairSpread());
        LOGGER.info("Fair Rate: " + swap.fairRate());

    }
}
