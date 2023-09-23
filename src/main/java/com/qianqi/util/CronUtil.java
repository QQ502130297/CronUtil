package com.qianqi.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author qianqi
 * @date 2023/9/22 15:57
 * @description 将时间转换为Cron表达式
 */
public class CronUtil {

    /**
     * @description 生成【activeStartTime, activeEndTime】这段时间范围内executeTime时间点执行的cron表达式
     * @param activeStartTime 任务开始生效时间
     * @param activeEndTime 任务结束生效时间
     * @param executeTime 任务开始执行时间 格式 hour:minute:second
     * case:
     *     入参: activeStartTime = 2023-08-21, activeEndTime = 2024-01-10, executeTime = 17:04:00
     *     出参: 从2023-08-21至2024-01-10每天的17:04:00都会执行的cron表达式
     * */
    public static String generateCronExpressionString(Date activeStartTime, Date activeEndTime,Date executeTime) {
        LocalDate start = LocalDates.toLocalDate(activeStartTime);
        LocalDate end = LocalDates.toLocalDate(activeEndTime);
        LocalDateTime localDateTime = LocalDates.toLocalDateTime(executeTime);
        LocalTime time = localDateTime.toLocalTime();
        return generateCronExpressionString(start, end, time);
    }

    public static String generateCronExpressionString(String activeStartTime, String activeEndTime,String executeTime) {
        LocalDate start = LocalDates.parseLocalDate(activeStartTime);
        LocalDate end = LocalDates.parseLocalDate(activeEndTime);
        LocalTime time = LocalDates.parseLocalTime(executeTime);
        return generateCronExpressionString(start, end, time);
    }

    public static String generateCronExpressionString(LocalDate startDate, LocalDate endDate, LocalTime time) {
        List<String> cronList = generateCronExpression(startDate, endDate, time);
        StringBuilder cronExpress = new StringBuilder();
        for (String cron : cronList) {
            cronExpress.append(cron).append(",");
        }
        if(cronExpress.length() > 0){
            cronExpress.deleteCharAt(cronExpress.length() - 1);
        }
        return cronExpress.toString();
    }


    /**
     * @description 生成每天指定时间执行的cron表达式
     * @param executeTime 任务开始执行时间 格式 hour:minute:second
     * case:
     *     入参: executeTime = 17:04:00
     *     出参: 每天的17:04:00都会执行的cron表达式
     * */
    public static String generateCronExpression(String executeTime) {
        LocalTime time = LocalDates.parseLocalTime(executeTime);
        return String.format("%d %d %d * * ? *", time.getSecond(), time.getMinute(), time.getHour());
    }


    public static List<String> generateCronExpression(LocalDate startDate, LocalDate endDate, LocalTime time) {
        if(startDate.isAfter(endDate)){
            throw new RuntimeException("参数异常:开始时间不能小于结束时间");
        }
        String timeCron = String.format("%d %d %d", time.getSecond(), time.getMinute(), time.getHour());

        List<String> cronList = new ArrayList<>();
        String firstCron = firstCron(startDate, endDate, timeCron);
        List<String> middleCronList = middleCron(startDate, endDate, timeCron);
        String lastCron = lastCron(startDate, endDate, timeCron);

        if(StringUtils.isNotBlank(firstCron)){
            cronList.add(firstCron);
        }
        if(middleCronList != null && middleCronList.size() > 0){
            cronList.addAll(middleCronList);
        }
        if(StringUtils.isNotBlank(lastCron)){
            cronList.add(lastCron);
        }
        return cronList;
    }

    /**
     * 无论什么范围的时间, 最多可由5个cron表达式表达
     * */
    public static List<String> generateCronExpression(String activeStartTime, String activeEndTime,String executeTime) {
        LocalDate start = LocalDates.parseLocalDate(activeStartTime);
        LocalDate end = LocalDates.parseLocalDate(activeEndTime);
        LocalTime time = LocalDates.parseLocalTime(executeTime);
        return generateCronExpression(start, end, time);
    }

    private static String firstCron(LocalDate startDate, LocalDate endDate, String timeCron) {
        int startYear = startDate.getYear();
        int startMonth = startDate.getMonthValue();
        int startDay = startDate.getDayOfMonth();

        int endYear = endDate.getYear();
        int endMonth = endDate.getMonthValue();
        int endDay = endDate.getDayOfMonth();

        // 同年同月直接返回
        if(endYear == startYear && startMonth == endMonth){
            String dayRange = startDay == endDay ? String.valueOf(startDay) : String.format("%d-%d", startDay, endDay);
            return String.format("%s %s %d ? %d", timeCron, dayRange, startMonth, startYear);
        }
        int maxDayOfMouth = startDate.lengthOfMonth();
        String dayRange = startDay == maxDayOfMouth ? String.valueOf(startDay) : String.format("%d-%d", startDay, maxDayOfMouth);
        return String.format("%s %s %d ? %d", timeCron, dayRange, startMonth, startYear);
    }


    private static List<String> middleCron(LocalDate startDate, LocalDate endDate, String timeCron) {
        int startYear = startDate.getYear();
        int startMonth = startDate.getMonthValue();

        int endYear = endDate.getYear();
        int endMonth = endDate.getMonthValue();

        if(startYear == endYear){
            if(startMonth + 1 >= endMonth){
                return null;
            }
            // 同年月份相差1以上 2023-09-07 ~ 2023-12-21
            String monthRange = startMonth + 1 == endMonth - 1 ? String.valueOf(startMonth + 1) : String.format("%d-%d", startMonth + 1, endMonth - 1);
            return Collections.singletonList(String.format("%s * %s ? %d", timeCron, monthRange, startYear));
        }
        // 不同年
        List<String> middleCronList = new ArrayList<>(3);
        int maxMonthOfYear = 12;
        if(startMonth < maxMonthOfYear){
            String monthRange = startMonth + 1 == maxMonthOfYear ? "12" : String.format("%d-12", startMonth + 1);
            middleCronList.add(String.format("%s * %s ? %d", timeCron, monthRange, startYear));
        }
        String middleYearCron = middleYearCron(startDate, endDate, timeCron);
        if(StringUtils.isNotBlank(middleYearCron)){
            middleCronList.add(middleYearCron);
        }
        if(endMonth > 1){
            String monthRange = endMonth - 1 == 1 ? "1" : String.format("1-%d", endMonth - 1);
            middleCronList.add(String.format("%s * %s ? %d", timeCron, monthRange, endYear));
        }
        return middleCronList;
    }


    private static String middleYearCron(LocalDate startDate, LocalDate endDate, String timeCron) {
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        // 相差至少2年以上
        if(startYear == endYear || startYear + 1 == endYear){
            return null;
        }
        String yearRange = startYear + 1 == endYear - 1 ? String.valueOf(startYear + 1) : String.format("%d-%d", startYear + 1, endYear - 1);
        return String.format("%s * * ? %s", timeCron, yearRange);
    }

    private static String lastCron(LocalDate startDate, LocalDate endDate, String timeCron) {
        int startYear = startDate.getYear();
        int startMonth = startDate.getMonthValue();

        int endYear = endDate.getYear();
        int endMonth = endDate.getMonthValue();
        int endDay = endDate.getDayOfMonth();
        // 同年同月直接返回
        if(endYear == startYear && startMonth == endMonth){
            return null;
        }
        String dayRange = endDay == 1 ? "1" : String.format("1-%d", endDay);
        return String.format("%s %s %d ? %d", timeCron, dayRange, endMonth, endYear);
    }
}
