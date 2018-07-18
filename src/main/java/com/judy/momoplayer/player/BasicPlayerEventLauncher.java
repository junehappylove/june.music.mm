/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 本类是一个线程,一个永远在分派事件的线程
 * 它里面维护着一个链表
 * @author judy
 */
public class BasicPlayerEventLauncher extends Thread {

    private Set<BasicPlayerListener> listeners;//保存所有监听器的集合
    private LinkedList<BasicPlayerEvent> queue;//一个保存所有事件的队列
    /**
     * 默认的构造函数,只是包内友好,
     * 别的包不能初始化本类,因为本类只是给BasicPlayer所使用的
     */
     BasicPlayerEventLauncher() {
        super("BasicPlayerEvent Dispacther Thread");
        listeners = new HashSet<BasicPlayerListener>();
        queue = new LinkedList<BasicPlayerEvent>();
    }

    /**
     * 把事件加进去
     * @param event 事件
     */
    public synchronized void put(BasicPlayerEvent event) {
        queue.offer(event);
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * 添加事件监听器
     * @param listener 监听器
     */
    public synchronized void addBasicPlayerListener(BasicPlayerListener listener) {
        listeners.add(listener);
    }

    /**
     * 得到所有监听器的集合
     * @return 所有监听器
     */
    public synchronized Set<BasicPlayerListener> getBasicPlayerListeners() {
        return listeners;
    }

    /**
     * 去除指定的监听器
     * @param listener 要去除的临听器
     */
    public synchronized void removeBasicPlayerListener(BasicPlayerListener listener) {
        listeners.remove(listener);
    }

    public void run() {
        while (true) {
            BasicPlayerEvent event = queue.poll();
            if (event == null) {//如果事件为空,则等待
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BasicPlayerEventLauncher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {//否则就指派到指定的监听器去调用
                for (BasicPlayerListener bpl : listeners) {
                    bpl.stateUpdated(event);
                }
            }
        }

    }
}
