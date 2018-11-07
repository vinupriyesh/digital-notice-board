package com.icl.digiboard.util;

import com.icl.digiboard.compatibility.GwtIncompatible;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@GwtIncompatible
public class DateUtil {

    public static Date parse(String respJson) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.parse(respJson);
    }
}
