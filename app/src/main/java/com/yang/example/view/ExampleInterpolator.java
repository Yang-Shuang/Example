package com.yang.example.view;


public class ExampleInterpolator {

    /**
     * 上开口抛物线
     */
    private int upParabolaCalculate(int x) {
        return x * x;
    }

    /**
     * 右开口抛物线
     */
    private int rightParabolaCalculate(double x) {
        return (int) Math.sqrt(x);
    }

    /**
     * @param y 当前需要滚动的距离
     */
    public void filling(int y) {

    }

    public void getFillingY() {

    }
}
