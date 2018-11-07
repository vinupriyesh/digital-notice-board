package com.icl.digiboard.util;

import java.text.ParseException;
import java.util.Date;
import com.google.gwt.i18n.client.DateTimeFormat;

public class DateUtil {
    public static Date parse(String respJson) throws ParseException {
        DateTimeFormat format = DateTimeFormat.getFormat("yyyyMMddHHmmss");
        return format.parse(respJson);
    }
}
