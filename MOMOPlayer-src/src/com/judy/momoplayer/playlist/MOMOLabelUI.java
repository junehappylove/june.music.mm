/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.playlist;

import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 *
 * @author hadeslee
 */
public class MOMOLabelUI extends BasicLabelUI {

    private MOMOLabel label;
//    private static final int TIME_WIDTH = 35;//用来显示时间的长度
//    private final Color highLight = new Color(0, 244, 245);
//    private final Color fromColor = new Color(18, 18, 18);
//    private final Color toColor = new Color(100, 100, 100);
//    private final Color timeColor = new Color(128, 128, 0);
    public MOMOLabelUI(MOMOLabel label) {
        this.label = label;
    }

    @Override
    protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
        PlayListItem item = label.getPlayListItem();
        if (item == null) {
            return;
        }
        Config config = Config.getConfig();
        g.setFont(config.getPlaylistFont());
        int timeWidth = Util.getStringWidth("00:00", g);
        int width = l.getWidth();//标签的总宽度
        int height = l.getHeight();//标签的总高度
        int rest = width - timeWidth;//除了时间剩下的宽度
        int indexWidth = Util.getStringWidth("" + label.getItemCount() + ".", g);//序号的宽度
        int dotWidth = Util.getStringHeight("...", g);//三个点的宽度
        String display = item.getFormattedName();
        //如果被选中,则画出渐变的底色
        if (label.getIsSelected()) {
            Graphics2D gd = (Graphics2D) g;
            gd.setPaint(new GradientPaint(0, 0, config.getPlaylistSelectedBG(), 0, height, l.getBackground()));
            gd.fillRect(0, 0, width, height);
            String time = item.getFormattedLength();
            g.setColor(config.getPlaylistSelectedColor());
            Util.drawString(g, time, width - Util.getStringWidth(time, g), 0);
            Util.drawStringRight(g, "" + (label.getIndex() + 1) + ".", indexWidth, 0);
            if (Util.getStringWidth(display, g) < rest - indexWidth) {
                Util.drawString(g, display, indexWidth, 0);
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < display.length(); i++) {
                    sb.append(display.charAt(i));
                    if (Util.getStringWidth(sb.toString(), g) > rest - indexWidth - dotWidth) {
                        break;
                    }
                }
                sb.append("...");
                Util.drawString(g, sb.toString(), indexWidth, 0);
            }

        } else {
            String time = item.getFormattedLength();
            g.setColor(config.getPlaylistLengthColor());
            Util.drawString(g, time, width - Util.getStringWidth(time, g), 0);
            g.setColor(config.getPlaylistIndexColor());
            Util.drawStringRight(g, "" + (label.getIndex() + 1) + ".", indexWidth, 0);
            if (item.isSelected()) {
                g.setColor(config.getPlaylistHiLightColor());
            } else {
                g.setColor(config.getPlaylistTitleColor());
            }
            if (Util.getStringWidth(display, g) < rest - indexWidth) {
                Util.drawString(g, display, indexWidth, 0);
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < display.length(); i++) {
                    sb.append(display.charAt(i));
                    if (Util.getStringWidth(sb.toString(), g) > rest - indexWidth - dotWidth) {
                        break;
                    }
                }
                sb.append("...");
                Util.drawString(g, sb.toString(), indexWidth, 0);
            }
        }
        //如果有了焦点,则画一个白框出来
//        if (label.isHasFocus()&&(!item.isSelected()||(item.isSelected()&&label.getIsSelected()))) {
        if (label.isHasFocus()) {
            g.setColor(config.getPlaylistSelectedColor());
            g.drawRect(0, 0, l.getWidth() - 1, l.getHeight() - 1);
        }
    }
}
