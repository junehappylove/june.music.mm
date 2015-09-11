/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.player.ui;

import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author hadeslee
 */
public class TimePanel extends JPanel implements MouseListener {

    private static final long serialVersionUID = 20071214L;
    private static Logger log = Logger.getLogger(TimePanel.class.getName());
    private boolean isNormal;//是否是正常状态
    private int seconds;//当前要显示的秒数
    private int length;//总共的秒数
    private ImageBorder border1,  border2;
    private JLabel sign,  minLeft,  minRight,  colon,  secLeft,  secRight;//六个标签
    private ImageIcon[] numbers;//0到9的图像数组
    private Image signImage,  colonImage;//符号图像,冒号图像
    private Dimension numberSize = new Dimension(13, 17);

    public TimePanel() {
        super(null);
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(numberSize.width * 6, 17));
        this.addMouseListener(this);
        initOther();
        isNormal = Config.getConfig().getShowTimeStyle() == Config.SHOWTIME_POSITIVE;
        log.log(Level.INFO, "初始化的isNormal:" + isNormal);
    }

    public int getSeconds() {
        return seconds;
    }

    /**
     * 重置,以新的起点重新显示时间
     * @param allSec 当前这首歌的时间
     */
    public void reset(int length) {
        log.log(Level.INFO, "重置了TimePanel:" + length);
        this.length = length;
        seconds = 0;
        repaint();
    }

    /**
     * 重置当前的状态,使总长度为0以及当前的时间也为0
     */
    public void reset() {
        length = 0;
        seconds = 0;
        repaint();
    }

    private void initOther() {
//        border1 = new ImageBorder();
//        border1.setImage(getImage("pic/timeBorder1.png"));
//        border2 = new ImageBorder();
//        border2.setImage(getImage("pic/timeBorder2.png"));
        signImage = Util.getImage("numbers/signImage.png");
        colonImage = Util.getImage("numbers/colon.png");
        sign = new JLabel();
        minLeft = new JLabel();
        minRight = new JLabel();
        colon = new JLabel();
        secLeft = new JLabel();
        secRight = new JLabel();
        numbers = new ImageIcon[10];

        sign.setPreferredSize(numberSize);
        minLeft.setPreferredSize(numberSize);
        minRight.setPreferredSize(numberSize);
        colon.setPreferredSize(numberSize);
        secLeft.setPreferredSize(numberSize);
        secRight.setPreferredSize(numberSize);


        sign.setIcon(new ImageIcon(signImage));
        colon.setIcon(new ImageIcon(colonImage));
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = new ImageIcon(Util.getImage("numbers/" + i + ".png"));
        }
        minLeft.setIcon(numbers[0]);
        minRight.setIcon(numbers[0]);
        secLeft.setIcon(numbers[0]);
        secRight.setIcon(numbers[0]);
//        setBorder(border1);
        int x = 0, y = 0;
        sign.setBounds(x, y, 13, 17);
        minLeft.setBounds(x += 13, y, 12, 17);
        minRight.setBounds(x += 13, y, 12, 17);
        colon.setBounds(x += 13, y, 12, 17);
        secLeft.setBounds(x += 13, y, 12, 17);
        secRight.setBounds(x += 13, y, 12, 17);
        sign.setVisible(false);
        this.add(sign);
        this.add(minLeft);
        this.add(minRight);
        this.add(colon);
        this.add(secLeft);
        this.add(secRight);
    }

    public void setTime(int seconds) {
        this.seconds = seconds;
        if (!isNormal && length > 0) {
            showNumber(seconds - length);
        } else {
            showNumber(seconds);
        }
    }

    /**
     * 要显示的数字,可能是负数,如果是负数
     * 就是显示倒计时了,还有,当总时间不能
     * 确定的时候,是不能显示倒计时的
     * @param number
     */
    private void showNumber(int number) {
        int temp = Math.abs(number);
        DecimalFormat df = new DecimalFormat("00");
        String sec = df.format(temp % 60);
        secLeft.setIcon(numbers[sec.charAt(0) - 48]);
        secRight.setIcon(numbers[sec.charAt(1) - 48]);
        String min = df.format(temp / 60);
        minLeft.setIcon(numbers[min.charAt(0) - 48]);
        minRight.setIcon(numbers[min.charAt(1) - 48]);
        if (number >= 0) {
            sign.setVisible(false);
        } else {
            sign.setVisible(true);
        }
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        if (length <= 0) {
            return;
        }
        if (length - seconds > 0) {
            isNormal = !isNormal;
            if (isNormal) {
                this.setBorder(border1);
                showNumber(seconds);
            } else {
                this.setBorder(border2);
                showNumber(seconds - length);
            }
            Config.getConfig().setShowTimeStyle(isNormal ? Config.SHOWTIME_POSITIVE : Config.SHOWTIME_NEGATIVE);

        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public static void main(String[] args) {
        JFrame jf = new JFrame();
        final TimePanel tp = new TimePanel();
        tp.reset(10);
        new Thread() {

            public void run() {
                int time = 0;
                while (true) {
                    try {
                        Thread.sleep(1000);
                        tp.setTime(time++);
                    } catch (Exception exe) {
                    }
                }
            }
        }.start();
        jf.add(tp);
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
