package com.qianqi.util;

import org.junit.Test;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * @author qianqi
 * @date 2023/9/15 18:26
 * @desc  测试Cron表达式
 */
public class TestCronUtil {

    @Test
    public void test(){
        assert checkCronExpression("2023-12-14", "2023-12-26", "12:34:27");
        assert checkCronExpression("2023-12-14", "2024-01-26", "12:34:27");
        assert checkCronExpression("2023-05-14", "2025-08-14", "12:34:27");
        assert checkCronExpression("2023-08-14", "2024-12-14", "12:34:27");
        assert checkCronExpression("2023-01-14", "2028-12-04", "12:34:27");
        assert checkCronExpression("2023-12-29", "2024-01-01", "12:34:27");
        assert checkCronExpression("2023-01-14", "2023-01-14", "12:34:27");
    }

    public boolean checkCronExpression(String activeStartTime, String activeEndTime,String executeTime){
        List<String> cronList = CronUtil.generateCronExpression(activeStartTime, activeEndTime, executeTime);
        List<Date> executionTimes = buildExecutionTimes(activeStartTime, activeEndTime, executeTime);
        return checkEquals(cronList, executionTimes);
    }

    private List<Date> buildExecutionTimes(String activeStartTime, String activeEndTime,String executeTime){
        List<Date> executionTimes = new ArrayList<>();
        LocalDateTime start = LocalDates.parseLocalDateTime(activeStartTime + " " + executeTime);
        LocalDateTime end = LocalDates.parseLocalDateTime(activeEndTime + " " + executeTime);
        while(start.isBefore(end) || start.isEqual(end)){
            executionTimes.add(LocalDates.toDate(start));
            start = start.plusDays(1);
        }
        return executionTimes;
    }

    private boolean checkEquals(List<String> cronExpressions, List<Date> expectExecTimes){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expectExecTimes.get(0));
        calendar.add(Calendar.SECOND, -1);

        List<Date> actualExecTimes = new ArrayList<>();

        for (String cronExpression : cronExpressions) {
            if(!CronExpression.isValidExpression(cronExpression)){
                return false;
            }
            actualExecTimes.addAll(getNextExecution(cronExpression, calendar.getTime(), expectExecTimes.size()));
            if(actualExecTimes.size() > 0){
                calendar.setTime(actualExecTimes.get(actualExecTimes.size() - 1));
            }
        }
        if(actualExecTimes.size() != expectExecTimes.size()){
            return false;
        }
        System.out.println();
        System.out.println("cronExpression :" + cronExpressions);
        System.out.println("actualExecTimes:" + actualExecTimes.size() + ", expectExecTimes:" + expectExecTimes.size());
        for (int i = 0; i < actualExecTimes.size(); i++) {
            Date actual = actualExecTimes.get(i);
            Date expect = expectExecTimes.get(i);
            if(actual.getTime() != expect.getTime()){
                return false;
            }
        }
        return true;
    }

    /**
     * 返回下一个执行时间根据给定的Cron表达式
     *
     * @param cronExpression Cron表达式
     * @return Date 下次Cron表达式执行时间
     */
    private List<Date> getNextExecution(String cronExpression, Date currentTime, int count) {
        List<Date> executionTimes = new ArrayList<>();
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            CronExpression cron = new CronExpression(cronExpression);

            // 生成接下来的count个执行时间点
            for (int i = 0; i < count; i++) {
                Date nextExecution = cron.getNextValidTimeAfter(calendar.getTime());
                if(nextExecution == null){
                    return executionTimes;
                }
                executionTimes.add(nextExecution);
                calendar.setTime(nextExecution);
                calendar.add(Calendar.SECOND, 1);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return executionTimes;
    }
}
