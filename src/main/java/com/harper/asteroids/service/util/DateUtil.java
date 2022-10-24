package com.harper.asteroids.service.util;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateUtil {


    public static LocalDate getEndDateOfCurrentWeek() {
        LocalDate today = LocalDate.now();
        //DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
        DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(6);
        return today.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
    }

    public static boolean isDateInCurrentWeek(long epochDate) {

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime targetDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochDate), ZoneId.systemDefault());
        //DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;
        DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(6);

        return targetDateTime.isAfter(currentDateTime.toLocalDate().with(TemporalAdjusters.nextOrSame(firstDayOfWeek)).atTime(LocalTime.MIN)) &&
                targetDateTime.isBefore(currentDateTime.toLocalDate().with(TemporalAdjusters.nextOrSame(lastDayOfWeek)).atTime(LocalTime.MAX));
    }
}