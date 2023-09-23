package com.qianqi.util;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * @author qianqi
 */
public final class LocalDates {

    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    public static final String YYYYMMDD_PATTERN = "yyyyMMdd";

    public static final String YYYYMMDDHHMMSS_PATTERN = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS_PATTERN_1 = "yyyy/MM/dd HH:mm:ss";

    public static final String YYYY_MM_DD_HH_MM_SS_PATTERN_2 = "yyyy/MM/dd HH/mm/ss";

    public static final String YYYY_MM_DD_HHMMSS_PATTERN = "yyyy/MM/dd HHmmss";

    public static final String HHMMSS_PATTERN = "HHmmss";

    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    public static final String HH_MM_SS_PATTERN = "HH/mm/ss";

    public static final String YYYY_MM_DD_PATTERN = "yyyy/MM/dd";

    public static final ZoneOffset CHINA_OFFSET = ZoneOffset.of("+8");

    public static final LocalDateTime DEFAULT_LOCAL_DATE_TIME = LocalDateTime.parse("1970-01-01 00:00:00",
            DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN));

    public static LocalDateTime parseLocalDateTime(CharSequence text) {
        if (StringUtils.isNotEmpty(text)) {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN));
        }
        return null;
    }

    public static LocalDateTime parseLocalDateTime(CharSequence text, String pattern) {
        if (StringUtils.isNotEmpty(text) && StringUtils.isNotEmpty(pattern)) {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
        }
        return null;
    }

    public static LocalDate parseLocalDate(CharSequence text) {
        if (StringUtils.isNotEmpty(text)) {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN));
        }
        return null;
    }

    public static LocalDate parseLocalDate(CharSequence text, String pattern) {
        if (StringUtils.isNotEmpty(text) && StringUtils.isNotEmpty(pattern)) {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern));
        }
        return null;
    }

    public static LocalTime parseLocalTime(CharSequence text) {
        if (StringUtils.isNotEmpty(text)) {
            return LocalTime.parse(text, DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN));
        }
        return null;
    }

    public static LocalTime parseLocalTime(CharSequence text, String pattern) {
        if (StringUtils.isNotEmpty(text) && StringUtils.isNotEmpty(pattern)) {
            return LocalTime.parse(text, DateTimeFormatter.ofPattern(pattern));
        }
        return null;
    }

    public static String formatInstant(Instant instant) {
        if (Objects.nonNull(instant)) {
            return DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN).format(instant);
        }
        return "";
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        if (Objects.nonNull(localDateTime) && StringUtils.isNotEmpty(pattern)) {
            return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
        }
        return "";
    }

    public static String formatLocalDate(LocalDate localDate) {
        if (Objects.nonNull(localDate)) {
            return localDate.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN));
        }
        return "";
    }

    public static String formatLocalDate(LocalDate localDate, String pattern) {
        if (Objects.nonNull(localDate)) {
            return localDate.format(DateTimeFormatter.ofPattern(pattern));
        }
        return "";
    }

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        if (Objects.nonNull(localDateTime)) {
            return localDateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN));
        }
        return "";
    }

    public static LocalDate toLocalDate(Date date) {
        if (Objects.nonNull(date)) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (Objects.nonNull(date)) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }

    public static Date toDate(LocalDateTime date) {
        if (Objects.nonNull(date)) {
            return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
        }
        return null;
    }

    public static Date toDate(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(date.atStartOfDay());
    }

    public static Long toEpochSecond(LocalDateTime date) {
        return toEpochSecond(date, CHINA_OFFSET);
    }

    public static Long toEpochSecond(LocalDateTime date, ZoneOffset offset) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.toEpochSecond(offset);
    }

    public static Long toEpochMilli(LocalDateTime date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toEpochMilli(date, CHINA_OFFSET);
    }

    public static Long toEpochMilli(LocalDateTime date, ZoneOffset offset) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.toInstant(offset).toEpochMilli();
    }

    public static String formatLocalTime(LocalTime localTime) {
        return formatLocalTime(localTime, DEFAULT_TIME_PATTERN);
    }

    public static String formatLocalTime(LocalTime localTime, String pattern) {
        return Objects.nonNull(localTime) ? localTime.format(DateTimeFormatter.ofPattern(pattern)) : "";
    }
}
