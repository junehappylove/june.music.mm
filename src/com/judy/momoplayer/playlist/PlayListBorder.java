/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.playlist;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author judy
 */
public class PlayListBorder extends AbstractBorder implements MouseListener, MouseMotionListener {
    private static final long serialVersionUID=20071214L;
    private Image corner1,  corner2,  corner3,  corner4;//四个角的图片
    private Image top,  bottom,  left,  right;//四条边的图片
    private Component com;
    private int startX,  startY;
    private int state;//表示当前的鼠标移动状态,是移动还是缩放,缩放也分八种
    private static final int MOVE = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int TOP = 4;
    private static final int BOTTOM = 5;
    private static final int LEFT_TOP = 6;
    private static final int LEFT_BOTTOM = 7;
    private static final int RIGHT_TOP = 8;
    private static final int RIGHT_BOTTOM = 9;
    private Rectangle[] rects;//八个方向的矩形,这个矩形之外就是移动的区域了
    private Cursor[] cursors;//八种鼠标状态
    private int[] states;//八种移动状态
    private final Insets insets = new Insets(21, 12, 15, 12);
    private boolean isMoving;//是否正在移动,如果正在移动,而move事件不触发
    //以上八种之外都不是的话,就是MOVE状态了
    public PlayListBorder(Component com) {
        this.com = com;
        initOther();
    }

    private void initOther() {
        rects = new Rectangle[8];
        for (int i = 0; i < rects.length; i++) {
            rects[i] = new Rectangle();
        }
        calculateRectangles();
        cursors = new Cursor[]{
            Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR),
            Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR),
            Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR),
            Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR),
            Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR),
            Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR),
            Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR),
            Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)
        };
        states = new int[]{
            LEFT_TOP, TOP, RIGHT_TOP, RIGHT, RIGHT_BOTTOM, BOTTOM, LEFT_BOTTOM, LEFT
        };
    }

    /**
     * 计算八个矩形的位置
     */
    private void calculateRectangles() {
        Dimension size = com.getSize();
        rects[0].setBounds(0, 0, insets.left / 3, insets.top / 3);
        rects[1].setBounds(insets.left / 3, 0, size.width - insets.left / 3 - insets.right / 3, insets.top / 3);
        rects[2].setBounds(size.width - insets.right / 3, 0, insets.right / 3, insets.top / 3);
        rects[3].setBounds(size.width - insets.right / 3, insets.top / 3, insets.right / 3, size.height - insets.top / 3 - insets.bottom / 3);
        rects[4].setBounds(size.width - insets.right / 3, size.height - insets.bottom / 3, insets.right / 3, insets.bottom / 3);
        rects[5].setBounds(insets.left / 3, size.height - insets.bottom / 3, size.width - insets.left / 3 - insets.right / 3, insets.bottom / 3);
        rects[6].setBounds(0, size.height - insets.bottom / 3, insets.left / 3, insets.bottom / 3);
        rects[7].setBounds(0, insets.top / 3, insets.left / 3, size.height - insets.top / 3 - insets.bottom / 3);
    }

    /**
     * 计算当前的状态
     */
    private void calculateState(Point p) {
        //先计算一个八个坐标
        calculateRectangles();
        boolean find = false;//是否找到了
        for (int i = 0; i < rects.length; i++) {
            if (rects[i].contains(p)) {
                state = states[i];
                com.setCursor(cursors[i]);
                find = true;
                break;
            }
        }
        if (!find) {
            state = MOVE;
            com.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void setBottom(Image bottom) {
        this.bottom = bottom;
    }

    public void setCorner1(Image corner1) {
        this.corner1 = corner1;
    }

    public void setCorner2(Image corner2) {
        this.corner2 = corner2;
    }

    public void setCorner3(Image corner3) {
        this.corner3 = corner3;
    }

    public void setCorner4(Image corner4) {
        this.corner4 = corner4;
    }

    public void setLeft(Image left) {
        this.left = left;
    }

    public void setRight(Image right) {
        this.right = right;
    }

    public void setTop(Image top) {
        this.top = top;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawImage(corner1, x, y, c);
        g.drawImage(corner2, width - corner2.getWidth(c), y, c);
        g.drawImage(corner3, x, height - corner3.getHeight(c), c);
        g.drawImage(corner4, width - corner4.getWidth(c), height - corner4.getHeight(c), c);
        //得到最上面的重绘部分
        int wid = width - corner2.getWidth(c);
        if (wid > 0) {
            int sx = corner1.getWidth(c);
            while (sx + top.getWidth(c) <= wid) {
                g.drawImage(top, sx, y, c);
                sx += top.getWidth(c);
            }
            g.drawImage(top, sx, y, wid, top.getHeight(c),
                    0, 0, wid - sx, top.getHeight(c), c);
        }
        //得到下面的重绘部份
        wid = width - corner4.getWidth(c);
        if (wid > 0) {
            int sx = corner1.getWidth(c);
            while (sx + bottom.getWidth(c) <= wid) {
                g.drawImage(bottom, sx, height - bottom.getHeight(c), c);
                sx += bottom.getWidth(c);
            }
            g.drawImage(bottom, sx, height - bottom.getHeight(c),
                    wid, height,
                    0, 0, wid - sx, bottom.getHeight(c), c);
        }
        //得到最左边的重绘部份
        int he = height - corner1.getHeight(c);
        if (he > 0) {
            int sy = corner1.getHeight(c);
            while (sy + left.getHeight(c) <= he) {
                g.drawImage(left, x, sy, c);
                sy += left.getHeight(c);
            }
            g.drawImage(left, x, sy, left.getWidth(c), he + 1,
                    0, 0, left.getWidth(c), he - sy, c);
        }
        //得到最右边的重绘部份
        he = height - corner2.getHeight(c);
        if (he > 0) {
            int sy = corner2.getHeight(c);
            while (sy + right.getHeight(c) <= he) {
                g.drawImage(right, width - right.getWidth(c), sy, c);
                sy += right.getHeight(c);
            }
            g.drawImage(right, width - right.getWidth(c), sy, width, he + 1,
                    0, 0, right.getWidth(c), he - sy, c);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        isMoving = true;
        calculateState(e.getPoint());
        startX = e.getX();
        startY = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        isMoving = false;
    }

    public void mouseEntered(MouseEvent e) {
        if (!isMoving) {
            calculateState(e.getPoint());
        }
    }

    public void mouseExited(MouseEvent e) {
        if (!isMoving) {
            state = 0;
        }
    }

    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        switch (state) {
            case MOVE:
                doMOVE(p);
                break;
            case LEFT:
                doLEFT(p);
                break;
            case RIGHT:
                doRIGHT(p);
                break;
            case TOP:
                doTOP(p);
                break;
            case BOTTOM:
                doBOTTOM(p);
                break;
            case LEFT_TOP:
                doLEFT_TOP(p);
                break;
            case LEFT_BOTTOM:
                doLEFT_BOTTOM(p);
                break;
            case RIGHT_TOP:
                doRIGHT_TOP(p);
                break;
            case RIGHT_BOTTOM:
                doRIGHT_BOTTOM(p);
                break;
        }
    }

    public void mouseMoved(MouseEvent e) {
        calculateState(e.getPoint());

    }

    private void doMOVE(Point p) {
        int addX = p.x - startX;
        int addY = p.y - startY;
        Point old = com.getLocation();
        com.setLocation(old.x + addX, old.y + addY);
    }

    private void doLEFT(Point p) {
        int addX = startX - p.x;
        Point old = com.getLocation();
        Dimension oldSize = com.getSize();
        if (com.getWidth() <= com.getMinimumSize().width && addX < 0) {
            addX = 0;
        }
        com.setSize(oldSize.width + addX, oldSize.height);
        com.setLocation(old.x - addX, old.y);
    }

    private void doRIGHT(Point p) {
        int addX = p.x - startX;
        Dimension oldSize = com.getSize();
        if (com.getWidth() <= com.getMinimumSize().width && addX < 0) {
            addX = 0;
        } else {
            startX = p.x;
        }
        com.setSize(oldSize.width + addX, oldSize.height);
        startY = p.y;
    }

    private void doTOP(Point p) {
        int addY = startY - p.y;
        Dimension oldSize = com.getSize();
        Point old = com.getLocation();
        if (com.getHeight() <= com.getMinimumSize().height && addY < 0) {
            addY = 0;
        }
        com.setSize(oldSize.width, oldSize.height + addY);
        com.setLocation(old.x, old.y - addY);
    }

    private void doBOTTOM(Point p) {
        int addY = p.y - startY;
        Dimension oldSize = com.getSize();
        if (com.getHeight() <= com.getMinimumSize().height && addY < 0) {
            addY = 0;
        } else {
            startY = p.y;
        }
        com.setSize(oldSize.width, oldSize.height + addY);
        startX = p.x;
    }

    private void doLEFT_TOP(Point p) {
        int addX = startX - p.x;
        int addY = startY - p.y;
        Dimension oldSize = com.getSize();
        Point old = com.getLocation();
        if (com.getWidth() <= com.getMinimumSize().width && addX < 0) {
            addX = 0;
        }
        if (com.getHeight() <= com.getMinimumSize().height && addY < 0) {
            addY = 0;
        }
        com.setSize(oldSize.width + addX, oldSize.height + addY);
        com.setLocation(old.x - addX, old.y - addY);
    }

    private void doLEFT_BOTTOM(Point p) {
        int addX = startX - p.x;
        int addY = p.y - startY;
        Dimension oldSize = com.getSize();
        Point old = com.getLocation();
        if (com.getWidth() <= com.getMinimumSize().width && addX < 0) {
            addX = 0;
        }
        if (com.getHeight() <= com.getMinimumSize().height && addY < 0) {
            addY = 0;
        } else {
            startY = p.y;
        }
        com.setSize(oldSize.width + addX, oldSize.height + addY);
        com.setLocation(old.x - addX, old.y);
    }

    private void doRIGHT_TOP(Point p) {
        int addX = p.x - startX;
        int addY = startY - p.y;
        Dimension oldSize = com.getSize();
        Point old = com.getLocation();
        if (com.getWidth() <= com.getMinimumSize().width && addX < 0) {
            addX = 0;
        } else {
            startX = p.x;
        }
        if (com.getHeight() <= com.getMinimumSize().height && addY < 0) {
            addY = 0;
        }
        com.setSize(oldSize.width + addX, oldSize.height + addY);
        com.setLocation(old.x, old.y - addY);
    }

    private void doRIGHT_BOTTOM(Point p) {
        int addX = p.x - startX;
        int addY = p.y - startY;
        Dimension oldSize = com.getSize();
        if (com.getWidth() <= com.getMinimumSize().width && addX < 0) {
            addX = 0;
        } else {
            startX = p.x;
        }
        if (com.getHeight() <= com.getMinimumSize().height && addY < 0) {
            addY = 0;
        } else {
            startY = p.y;
        }
        com.setSize(oldSize.width + addX, oldSize.height + addY);
    }
}
