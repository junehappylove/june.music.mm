/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.judy.momoplayer.setting;

/**
 * 一个接口，主要是在缓存的
 * 状态下给各个子面板用的，以便
 * 它们能够重新读取在别的地方改变了
 * 的设置 
 * @author judy
 */
public interface Initable {
    public void init();
}
