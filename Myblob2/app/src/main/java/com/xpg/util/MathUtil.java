package com.xpg.util;

public class MathUtil {
    public static float to255(float currentValue, int currentMax) {
        return (currentValue / ((float) currentMax)) * 255.0f;
    }

    public static float toValue(float currentValue, int currentMax, float decimal) {
        return (currentValue / ((float) currentMax)) * decimal;
    }

    public static void main(String[] args) {
    }
}
