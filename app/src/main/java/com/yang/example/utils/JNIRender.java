package com.yang.example.utils;

public class JNIRender {

    public static native void render(int[] b1, int[] b2, int width, int height,
                                     double r, double len, double weight, double x0, double y0);
}
