package com.globalpaysolutions.tigorocket.customs;

public class Parsers
{
    public static float doubleToFloat(double val)
    {
        return Float.valueOf(String.valueOf(val));
    }

    public static double floatToDouble(float val)
    {
        return Double.parseDouble(Float.toString(val));
    }
}
