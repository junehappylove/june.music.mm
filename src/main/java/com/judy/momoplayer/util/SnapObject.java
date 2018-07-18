/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import java.awt.Component;

/**
 *一个私有的类，它封装了一个可以吸附的对象
 * 它里面有吸附的对象，以及要吸附的方位，是左边，还是右边
 * 是上面还是下面
 * @author judy
 */
public class SnapObject {

    //private static Logger log = Logger.getLogger(SnapObject.class.getName());
    private int location;//表示方位
    private Component com;//表示吸附的对象,也就是要吸到谁的身上去
    public SnapObject(int location, Component com) {
        this.location = location;
        this.com = com;
    }

    public Component getCom() {
        return com;
    }

    public int getLocation() {
        return location;
    }
    public String toString(){
        return "方位是:"+location+",自己是:"+com;
    }
}
