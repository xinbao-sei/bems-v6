package com.changhong.bems;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-04-23 00:41
 */
public class TestTime {

    @Test
    public void testDays() {
        System.out.println(TimeUnit.DAYS.toMillis(1));
    }

    @Test
    public void test1() {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    @Test
    public void test() {
        int year = 2021;
        LocalDate localDate = LocalDate.of(year, 1, 1);
        System.out.println(localDate);
        LocalDate localDate1 = LocalDate.now();
        System.out.println(localDate1.lengthOfMonth());
        System.out.println(localDate1.getDayOfMonth());
        System.out.println(localDate1.withDayOfMonth(localDate1.lengthOfMonth()));
    }

    @Test
    public void testSplit() {
        String temp = "/四川长虹电子控股集团有限公司/消费者BG/长虹多媒体公司/智慧业务BG/四川虹信软件股份有限公司";
//        String temp = "四川虹信软件股份有限公司";
        System.out.println(temp.substring(temp.lastIndexOf("/") + 1));
    }

    @Test
    public void isNumeric() {
        System.out.println(StringUtils.isNumeric("4500"));
        System.out.println(StringUtils.isNumeric("4500."));
        System.out.println(StringUtils.isNumeric("4500.00"));
        System.out.println(StringUtils.isNumeric("4500.12"));
        System.out.println(StringUtils.isNumeric("-4500.12"));
        System.out.println("  \"-?[0-9]+.?[0-9]*\"   ");
        System.out.println(isNumeric("4500"));
        System.out.println(isNumeric("4500."));
        System.out.println(isNumeric("4500.00"));
        System.out.println(isNumeric("4500.12"));
        System.out.println(isNumeric("-4500.12"));
        System.out.println("Double.parseDouble()");
        System.out.println(Double.parseDouble("4500"));
        System.out.println(Double.parseDouble("4500."));
        System.out.println(Double.parseDouble("4500.00"));
        System.out.println(Double.parseDouble("4500.12"));
        System.out.println(Double.parseDouble("-4500.12"));
    }

    private static boolean isNumeric(String str) {
        Boolean strResult = str.matches("-?[0-9]+.?[0-9]*");
        if (strResult) {
            System.out.println("Is Number!");
        } else {
            System.out.println("Is not Number!");
        }
        return strResult;
    }

    @Test
    public void testDuration() {
        LocalDateTime startTime = LocalDateTime.of(2021, 5, 8, 8, 50, 0);
        LocalDateTime endTime = LocalDateTime.now();
        System.out.println(startTime);
        System.out.println(endTime);
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("耗时(ms): " + duration.toMillis());
        System.out.println("耗时(s)" + duration.toMillis() / 1000);
        System.out.println("耗时(s)" + duration.toMinutes());
    }

    @Test
    public void pinYin() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        StringBuilder firstPinyin = new StringBuilder();
        char[] hanyuArr = "虹信软件-基础平台部".trim().toCharArray();
        try {
            for (char c : hanyuArr) {
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] pys = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    firstPinyin.append(pys[0].charAt(0));
                } else {
                    firstPinyin.append(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(firstPinyin.toString());
    }
}
