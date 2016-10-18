package com.judy.momoplayer.playlist;

import com.judy.momoplayer.util.Util;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * 此类包内友好。只用于滚动条的UI实现
 * 可能的话，可能把它升级为公共类。以供别
 * 的包所使用
 * @author hadeslee
 */
class MOMOScrollBarUI extends BasicScrollBarUI {


    MOMOScrollBarUI() {
        super();
        init();
    }

    private void init() {
        this.thumbColor = new Color(100, 100, 100);
        this.thumbDarkShadowColor = new Color(50, 50, 50);
        this.thumbLightShadowColor = new Color(150, 150, 150);
        this.thumbHighlightColor = new Color(0, 244, 245);
        this.trackColor = new Color(10, 10, 10);
        this.trackHighlightColor = new Color(10, 10, 10);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return new Dimension(8, 8);
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(8, 8);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton jb = new JButton();
        jb.setPreferredSize(new Dimension(8, 8));
        jb.setOpaque(false);
        jb.setContentAreaFilled(false);
        jb.setFocusPainted(false);
        jb.setBorderPainted(false);
        jb.setIcon(new ImageIcon(Util.getImage("playlist/up1.png")));
        jb.setPressedIcon(new ImageIcon(Util.getImage("playlist/up2.png")));
        return jb;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton jb = new JButton();
        jb.setPreferredSize(new Dimension(8, 8));
        jb.setOpaque(false);
        jb.setContentAreaFilled(false);
        jb.setFocusPainted(false);
        jb.setBorderPainted(false);
        jb.setIcon(new ImageIcon(Util.getImage("playlist/down1.png")));
        jb.setPressedIcon(new ImageIcon(Util.getImage("playlist/down2.png")));
        return jb;
    }

    @Override
    protected Dimension getMaximumThumbSize() {
        Dimension di = super.getMaximumThumbSize();
        return new Dimension(di.width / 2, di.height);
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        Dimension di = super.getMinimumThumbSize();
        return new Dimension(di.width / 2, di.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        init();
        super.paintThumb(g, c, thumbBounds);
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        init();
        super.paintTrack(g, c, trackBounds);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        init();
        super.paint(g, c);
    }

    @Override
    protected void paintDecreaseHighlight(Graphics g) {
        init();
        super.paintDecreaseHighlight(g);
    }

    @Override
    protected void paintIncreaseHighlight(Graphics g) {
        init();
        super.paintIncreaseHighlight(g);
    }
}
