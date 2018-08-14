package com.mace.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description:
 * <br />
 * Created by mace on 17:14 2018/8/6.
 */
public class NumericalConversion {

    public static Long getLong(String str){

        try {
            Pattern p = Pattern.compile("^\\d+");
            Matcher m = p.matcher(str);
            if (m.find())
                return Long.valueOf(str);
            else
                return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
