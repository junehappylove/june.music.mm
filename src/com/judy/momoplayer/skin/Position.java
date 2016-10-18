/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.skin;

/**
 * 一个类，用来表示从皮肤里面解析出来的位置
 * 信息，分别是左中和右下的坐标信息
 * @author binfeng.li
 */
public class Position {

    private int leftX;
    private int leftY;
    private int rightX;
    private int rightY;

    public Position(int leftX, int leftY, int rightX, int rightY) {
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.rightY = rightY;
    }

    public int getLeftX() {
        return leftX;
    }

    public int getLeftY() {
        return leftY;
    }

    public int getRightX() {
        return rightX;
    }

    public int getRightY() {
        return rightY;
    }
}
