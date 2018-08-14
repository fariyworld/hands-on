package com.mace.handson.redisApp.util;

import java.math.BigDecimal;

/**
 * description: Java 数学运算工具类
 * <br />
 * Created by mace on 15:31 2018/7/23.
 */
public class NumberArithmeticUtils {

    private NumberArithmeticUtils(){ }

    /**
     * description: BigDecimal的加法运算封装
     * <br /><br />
     * create by mace on 2018/7/23 15:34.
     * @param v1
     * @param v2
     * @return: double    两个参数的和
     */
    public static double add(double v1, double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }


    /**
     * description: 减法
     * <br /><br />
     * create by mace on 2018/7/23 15:36.
     * @param v1          被减数
     * @param v2          减数
     * @return: double    两个参数的差
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }


    /**
     * description: 乘法
     * <br /><br />
     * create by mace on 2018/7/23 15:36.
     * @param v1
     * @param v2
     * @return: double  两个参数的积
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return: double 两个参数的商
     */
    public static double div(double v1,double v2,int scale) {
        if(scale<0){
            //如果精确范围小于0，抛出异常信息。
            throw new IllegalArgumentException("精确度不能小于0");
        }else if(v2 == 0){
            //如果除数为0，抛出异常信息。
            throw new IllegalArgumentException("除数不能为0");
        }
        BigDecimal b1 = new BigDecimal(Double.valueOf(v1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v         需要四舍五入的数字
     * @param scale     小数点后保留几位
     * @return: double  四舍五入后的结果
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("精确度不能小于0");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供精确加法计算的add方法，确认精确度
     * @param value1    被加数
     * @param value2    加数
     * @param scale     小数点后保留几位
     * @return: double  两个参数求和之后，按精度四舍五入的结果
     */
    public static double add(double value1, double value2, int scale){
        return round(add(value1, value2), scale);
    }

    /**
     * 提供精确减法运算的sub方法，确认精确度
     * @param value1    被减数
     * @param value2    减数
     * @param scale     小数点后保留几位
     * @return: double  两个参数的求差之后，按精度四舍五入的结果
     */
    public static double sub(double value1, double value2, int scale){
        return round(sub(value1, value2), scale);
    }

    /**
     * 提供精确乘法运算的mul方法，确认精确度
     * @param value1    被乘数
     * @param value2    乘数
     * @param scale     小数点后保留几位
     * @return: double  两个参数的乘积之后，按精度四舍五入的结果
     */
    public static double mul(double value1, double value2, int scale){
        return round(mul(value1, value2), scale);
    }
}
