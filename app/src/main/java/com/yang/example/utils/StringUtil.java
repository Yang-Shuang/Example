package com.yang.example.utils;

public class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null ? true : ("".equals(str.trim()) || "null".equals(str.trim()));
    }
}
