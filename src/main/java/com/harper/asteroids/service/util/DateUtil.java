package com.harper.asteroids.service.util;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class DateUtil {


    public static LocalDate getEndDateOfCurrentWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        return today.with(TemporalAdjusters.nextOrSame(firstDayOfWeek)).plus(7, ChronoUnit.DAYS);
    }
    public static LocalDate getStartDateOfCurrentWeek() {
        LocalDate today = LocalDate.now();
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        return today.with(TemporalAdjusters.nextOrSame(firstDayOfWeek));
    }

    public static boolean isDateInCurrentWeek(long epochDate) {

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime targetDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochDate), ZoneId.systemDefault());
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        return targetDateTime.isAfter(currentDateTime.toLocalDate().with(TemporalAdjusters.nextOrSame(firstDayOfWeek)).atTime(LocalTime.MIN)) &&
                targetDateTime.isBefore(currentDateTime.toLocalDate().with(TemporalAdjusters.nextOrSame(firstDayOfWeek)).plus(7,ChronoUnit.DAYS).atTime(LocalTime.MAX));
    }
}