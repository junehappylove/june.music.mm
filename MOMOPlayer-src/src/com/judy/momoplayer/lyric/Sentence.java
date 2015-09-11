/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.lyric;

import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

/**
 * 一个用来表示每一句歌词的类
 * 它封装了歌词的内容以及这句歌词的起始时间
 * 和结束时间，还有一些实用的方法
 * @author hadeslee
 */
public class Sentence implements Serializable {

    private static final long serialVersionUID = 20071125L;
    private long fromTime;//这句的起始时间,时间是以毫秒为单位
    private long toTime;//这一句的结束时间
    private String content;//这一句的内容
    private final static long DISAPPEAR_TIME = 1000L;//歌词从显示完到消失的时间
    public Sentence(String content, long fromTime, long toTime) {
        this.content = content;
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public Sentence(String content, long fromTime) {
        this(content, fromTime, 0);
    }

    public Sentence(String content) {
        this(content, 0, 0);
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    /**
     * 检查某个时间是否包含在某句中间
     * @param time 时间
     * @return 是否包含了
     */
    public boolean isInTime(long time) {
        return time >= fromTime && time <= toTime;
    }

    /**
     * 得到这一句的内容
     * @return 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 得到V方向的增量
     * @param time 时间
     * @return 增量
     */
    public int getVIncrease(Graphics g, long time) {
        int height = getContentHeight(g);
        return (int) ((height + Config.getConfig().getV_SPACE()) * ((time - fromTime) * 1.0 / (toTime - fromTime)));
    }

    /**
     * 得到H向方的增量
     * @param time 时间
     * @return 增时
     */
    public int getHIncrease(Graphics g, long time) {
        int width = getContentWidth(g);
        return (int) ((width + Config.getConfig().getH_SPACE()) * ((time - fromTime) * 1.0 / (toTime - fromTime)));
    }

    /**
     * 得到内容的宽度
     * @param g 画笔
     * @return 宽度
     */
    public int getContentWidth(Graphics g) {
        return (int) g.getFontMetrics().getStringBounds(content, g).getWidth();
    }

    /**
     * 得到这个句子的时间长度,毫秒为单位
     * @return 长度
     */
    public long getDuring() {
        return toTime - fromTime;
    }

    /**
     * 移动这些距离来说,对于这个句子
     * 花了多少的时间
     * @param length 要移动的距离
     * @param g 画笔
     * @return 时间长度
     */
    public long getTimeH(int length, Graphics g) {
        return getDuring() * length / getContentWidth(g);
    }

    /**
     * 对于竖直方向的移动这些象素所代表的时间
     * @param length　距离的长度
     * @param g　画笔
     * @return　时间长度
     */
    public long getTimeV(int length, Graphics g) {
        return getDuring() * length / getContentHeight(g);
    }

    /**
     * 得到内容的高度
     * @param g 画笔
     * @return 高度
     */
    public int getContentHeight(Graphics g) {
        return (int) g.getFontMetrics().getStringBounds(content, g).getHeight() + Config.getConfig().getV_SPACE();
    }

    /**
     * 根据当前指定的时候,得到这个时候应该
     * 取渐变色的哪个阶段了,目前的算法是从
     * 快到结束的五分之一处开始渐变,这样平缓一些
     * @param c1 高亮色
     * @param c2 普通色
     * @param time 时间
     * @return 新的颜色
     */
    public Color getBestInColor(Color c1, Color c2, long time) {
        float f = (time - fromTime) * 1.0f / getDuring();
        if (f > 0.1f) {//如果已经过了十分之一的地方,就直接返高亮色
            return c1;
        } else {
            long dur = getDuring();
            f = (time - fromTime) * 1.0f / (dur * 0.1f);
            if (f > 1 || f < 0) {
                return c1;
            }
            return Util.getGradientColor(c2, c1, f);
        }
    }

    /**
     * 得到最佳的渐出颜色
     * @param c1
     * @param c2
     * @param time
     * @return
     */
    public Color getBestOutColor(Color c1, Color c2, long time) {
        if (isInTime(time)) {
            return c1;
        }
        float f = (time - toTime) * 1.0f / DISAPPEAR_TIME;
        if (f > 1f || f <= 0) {//如果时间已经超过了最大的时间了，则直接返回原来的颜色
            return c2;
        } else {
            return Util.getGradientColor(c1, c2, f);
        }
    }

    public String toString() {
        return "{" + fromTime + "(" + content + ")" + toTime + "}";
    }
    }
