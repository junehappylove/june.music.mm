/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.player.ui;

import com.judy.momoplayer.playlist.PlayListItem;
import com.judy.momoplayer.util.SongInfo;
import com.judy.momoplayer.util.Util;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * 一个用来显示歌曲信息的面板
 *
 * @author Admin
 */
public class SongInfoPanel extends JPanel implements MouseListener, Runnable {

    private static final long serialVersionUID = 20071214L;
    @SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SongInfoPanel.class.getName());
    private SongInfo info;
    private long lastShow;//上一次显示一个项目的时间
    private String show = "MOMOPlayer";//要显示的字符串
    private String preShow = "MOMOPlayer";//以前显示的字符串，用来做动画用的
    @SuppressWarnings("unused")
	private boolean isMoved;//是否已经移动了
    @SuppressWarnings("unused")
	private int length;//正在显示的字符串的长度
    private static final long TIME = 5000L;//等待的时间
    private int x1, y1, x2, y2;//两个座标给两个字符串用的
    private long sleepTime = TIME;//要睡的时间
    private volatile boolean isClicked;//是否点击了
    private volatile boolean isChanging;//是否正在改变信息

    public SongInfoPanel() {
        this.setPreferredSize(new Dimension(117, 14));
        this.setOpaque(false);
        this.addMouseListener(this);
        new Thread(this).start();
    }

    public void setInfo(SongInfo info) {
        this.info = info;
    }

    /**
     * 重置当前的字符串显示信息，以便更新
     *
     * @param item
     */
    public void reset(PlayListItem item) {
        lastShow = System.currentTimeMillis();
        info.reset(item);
        preShow = show;
        show = info.getNext();
        x1 = x2 = 0;
        y1 = -14;
        y2 = 0;
        repaint();
    }

    /**
     * 显示下一个要显示的项目
     */
    private void showNext() {
        lastShow = System.currentTimeMillis();
        preShow = show;
        show = info.getNext();
        x1 = 0;
        y1 = -14;
        x2 = 0;
        y2 = 0;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(sleepTime > 0 ? sleepTime : 1000);
                long now = System.currentTimeMillis();
                if (now - lastShow >= TIME) {
                    new Thread() {

                        public void run() {
                            changeRun();
                        }
                    }.start();
                    sleepTime = TIME;
                } else {
                    sleepTime = now - lastShow;
                }
            } catch (InterruptedException exe) {
                exe.printStackTrace();
            }
        }
    }

    private void changeRun() {
        isChanging = true;
        isClicked = false;
        showNext();
        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = 14;
        for (int i = 0; i < 14; i++) {
            try {
                Thread.sleep(80);
                y1--;
                y2--;
                if (isClicked) {
                    break;
                }
                repaint();
            } catch (InterruptedException exe) {
                exe.printStackTrace();
            }
        }
        x1 = 0;
        y1 = -14;
        x2 = 0;
        y2 = 0;
        repaint();
        isChanging = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        Util.drawString(g, preShow, x1, y1);
        Util.drawString(g, show, x2, y2);
        length = Util.getStringWidth(show, g);
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        isClicked = true;
        if (!isChanging) {
            showNext();
            repaint();
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
