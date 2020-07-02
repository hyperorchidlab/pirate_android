package com.hop.pirate.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeUtil {


    private static final SimpleDateFormat Y_M_R_H_M = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static SimpleDateFormat UTCFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static String forTime(long time) {
        return Y_M_R_H_M.format(time);
    }

    public static String forMinePoolTime(String time) {
        Date parse = new Date();
        try {
            parse = UTCFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Y_M_R_H_M.format(parse);
    }


}
