/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import static com.judy.momoplayer.util.Config.BOTTOM;
import static com.judy.momoplayer.util.Config.LEFT;
import static com.judy.momoplayer.util.Config.LEFT_BOTTOM;
import static com.judy.momoplayer.util.Config.LEFT_TOP;
import static com.judy.momoplayer.util.Config.MOVE;
import static com.judy.momoplayer.util.Config.RIGHT;
import static com.judy.momoplayer.util.Config.RIGHT_BOTTOM;
import static com.judy.momoplayer.util.Config.RIGHT_TOP;
import static com.judy.momoplayer.util.Config.TOP;

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
public class MultiImageBorder extends AbstractBorder implements MouseListener, MouseMotionListener {

    //private Logger log = Logger.getLogger(MultiImageBorder.class.getName());
    private static final long serialVersionUID = 20071214L;
    private Image corner1,  corner2,  corner3,  corner4;//四个角的图片
    private Image top,  bottom,  left,  right;//四条边的图片
    private Component me;
    private int startX,  startY;
    private int state;//表示当前的鼠标移动状态,是移动还是缩放,缩放也分八种
    private Rectangle myBound,  otherBound;
    private Rectangle[] rects;//八个方向的矩形,这个矩形之外就是移动的区域了
    private Cursor[] cursors;//八种鼠标状态
    private int[] states;//八种移动状态
    private Insets insets = new Insets(17, 7, 9, 7);
    private boolean isMoving;//是否正在移动,如果正在移动,而move事件不触发
    private Config config;
    //以上八种之外都不是的话,就是MOVE状态了
    public MultiImageBorder(Component com, Config config) {
        this.me = com;
        this.config = config;
        initOther();
    }

    public void setParent(Component com) {
        this.me = com;
    }

    public void setInsets(Insets insets) {
        this.insets = insets;
    }

    private void initOther() {
        myBound = new Rectangle();
        otherBound = new Rectangle();
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
        Dimension size = me.getSize();
        rects[0].setBounds(0, 0, insets.left, insets.top);
        rects[1].setBounds(insets.left, 0, size.width - insets.left - insets.right, insets.top / 3);
        rects[2].setBounds(size.width - insets.right, 0, insets.right, insets.top);
        rects[3].setBounds(size.width - insets.right / 3, insets.top, insets.right / 3, size.height - insets.top - insets.bottom);
        rects[4].setBounds(size.width - insets.right, size.height - insets.bottom, insets.right, insets.bottom);
        rects[5].setBounds(insets.left, size.height - insets.bottom / 3, size.width - insets.left - insets.right, insets.bottom / 3);
        rects[6].setBounds(0, size.height - insets.bottom, insets.left, insets.bottom);
        rects[7].setBounds(0, insets.top, insets.left / 3, size.height - insets.top - insets.bottom);
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
                me.setCursor(cursors[i]);
                find = true;
                break;
            }
        }
        if (!find) {
            state = MOVE;
            me.setCursor(Cursor.getDefaultCursor());
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

    private int getDirection(int dis) {
        int x1 = (int) myBound.getCenterX();
        int y1 = (int) myBound.getCenterY();
        int x2 = (int) otherBound.getCenterX();
        int y2 = (int) otherBound.getCenterY();
        int abs = Math.abs(x1 - x2 - myBound.width / 2 - otherBound.width / 2 - dis);
        if (abs < 3) {
            return RIGHT;
        }
        abs = Math.abs(x2 - x1 - myBound.width / 2 - otherBound.width / 2 - dis);
        if (abs < 3) {
            return LEFT;
        }
        abs = Math.abs(y1 - y2 - myBound.height / 2 - otherBound.height / 2 - dis);
        if (abs < 3) {
            return BOTTOM;
        }
        abs = Math.abs(y2 - y1 - myBound.height / 2 - otherBound.height / 2 - dis);
        if (abs < 3) {
            return TOP;
        }
        return -1;
    }

    private void changeLocation(SnapObject obj) {
        Component com = obj.getCom();
        int location = obj.getLocation();
        int x, y;
        switch (location) {
            case LEFT:
                x = com.getX() - me.getWidth();
                y = me.getY();
                break;
            case RIGHT:
                x = com.getX() + com.getWidth();
                y = me.getY();
                break;
            case TOP:
                x = me.getX();
                y = com.getY() - me.getHeight();
                break;
            case BOTTOM:
                x = me.getX();
                y = com.getY() + com.getHeight();
                break;
            default:
                x = me.getX();
                y = me.getY();
                break;
            }
        me.setLocation(x, y);
    }

    /**
     * 得到应该吸附到的窗口
     * @return
     */
    public void getSnapObject() {
        me.getBounds(myBound);
        //先查询EQ的窗口
        Component c1 = config.getEqWindow();
        if (c1 != null && c1 != me && c1.isShowing()) {
            c1.getBounds(otherBound);
            int dis = Util.getDistance(myBound, otherBound);
            if (dis > 0 && dis < Config.SNAP) {
                int dir = getDirection(dis);
                if (dir != -1) {
                    SnapObject obj = new SnapObject(dir, c1);
                    changeLocation(obj);
                } else {
                    config.getComponentMap().remove(config.getComponentName(me));
                }
            } else {
                config.getComponentMap().remove(config.getComponentName(me));
            }
        }
        me.getBounds(myBound);
        c1 = config.getLrcWindow();
        if (c1 != null && c1 != me && c1.isShowing()) {
            c1.getBounds(otherBound);
            int dis = Util.getDistance(myBound, otherBound);
            if (dis > 0 && dis < Config.SNAP) {
                int dir = getDirection(dis);
                if (dir != -1) {
                    SnapObject obj = new SnapObject(dir, c1);
                    changeLocation(obj);
                } else {
                    config.getComponentMap().remove(config.getComponentName(me));
                }
            } else {
                config.getComponentMap().remove(config.getComponentName(me));
            }
        }
        me.getBounds(myBound);
        c1 = config.getPlWindow();
        if (c1 != null && c1 != me && c1.isShowing()) {
            c1.getBounds(otherBound);
            int dis = Util.getDistance(myBound, otherBound);
            if (dis > 0 && dis < Config.SNAP) {
                int dir = getDirection(dis);
                if (dir != -1) {
                    SnapObject obj = new SnapObject(dir, c1);
                    changeLocation(obj);
                } else {
                    config.getComponentMap().remove(config.getComponentName(me));
                }
            } else {
                config.getComponentMap().remove(config.getComponentName(me));
            }
        }
        me.getBounds(myBound);
        c1 = config.getTopParent();
        if (c1 != null && c1 != me && c1.isShowing()) {
            c1.getBounds(otherBound);
            int dis = Util.getDistance(myBound, otherBound);
            if (dis > 0 && dis < Config.SNAP) {
                int dir = getDirection(dis);
                if (dir != -1) {
                    SnapObject obj = new SnapObject(dir, c1);
                    changeLocation(obj);
                } else {
                    config.getComponentMap().remove(config.getComponentName(me));
                }
            } else {
                config.getComponentMap().remove(config.getComponentName(me));
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        isMoving = false;
        if (state == MOVE) {
            //如果是主窗口就免了
            if (me == config.getTopParent()) {
                return;
            }
            getSnapObject();
            config.updateComponentSnap();
        }
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
        Point old = me.getLocation();
        me.setLocation(old.x + addX, old.y + addY);
    }

    private void doLEFT(Point p) {
        int addX = startX - p.x;
        Point old = me.getLocation();
        Dimension oldSize = me.getSize();
        if (me.getWidth() <= me.getMinimumSize().width && addX < 0) {
            addX = 0;
        }
        me.setSize(oldSize.width + addX, oldSize.height);
        me.setLocation(old.x - addX, old.y);
    }

    private void doRIGHT(Point p) {
        int addX = p.x - startX;
        Dimension oldSize = me.getSize();
        if (me.getWidth() <= me.getMinimumSize().width && addX < 0) {
            addX = 0;
        } else {
            startX = p.x;
        }
        me.setSize(oldSize.width + addX, oldSize.height);
        startY = p.y;
    }

    private void doTOP(Point p) {
        int addY = startY - p.y;
        Dimension oldSize = me.getSize();
        Point old = me.getLocation();
        if (me.getHeight() <= me.getMinimumSize().height && addY < 0) {
            addY = 0;
        }
        me.setSize(oldSize.width, oldSize.height + addY);
        me.setLocation(old.x, old.y - addY);
    }

    private void doBOTTOM(Point p) {
        int addY = p.y - startY;
        Dimension oldSize = me.getSize();
        if (me.getHeight() <= me.getMinimumSize().height && addY < 0) {
            addY = 0;
        } else {
            startY = p.y;
        }
        me.setSize(oldSize.width, oldSize.height + addY);
        startX = p.x;
    }

    private void doLEFT_TOP(Point p) {
        int addX = startX - p.x;
        int addY = startY - p.y;
        Dimension oldSize = me.getSize();
        Point old = me.getLocation();
        if (me.getWidth() <= me.getMinimumSize().width && addX < 0) {
            addX = 0;
        }
        if (me.getHeight() <= me.getMinimumSize().height && addY < 0) {
            addY = 0;
        }
        me.setSize(oldSize.width + addX, oldSize.height + addY);
        me.setLocation(old.x - addX, old.y - addY);
    }

    private void doLEFT_BOTTOM(Point p) {
        int addX = startX - p.x;
        int addY = p.y - startY;
        Dimension oldSize = me.getSize();
        Point old = me.getLocation();
        if (me.getWidth() <= me.getMinimumSize().width && addX < 0) {
            addX = 0;
        }
        if (me.getHeight() <= me.getMinimumSize().height && addY < 0) {
            addY = 0;
        } else {
            startY = p.y;
        }
        me.setSize(oldSize.width + addX, oldSize.height + addY);
        me.setLocation(old.x - addX, old.y);
    }

    private void doRIGHT_TOP(Point p) {
        int addX = p.x - startX;
        int addY = startY - p.y;
        Dimension oldSize = me.getSize();
        Point old = me.getLocation();
        if (me.getWidth() <= me.getMinimumSize().width && addX < 0) {
            addX = 0;
        } else {
            startX = p.x;
        }
        if (me.getHeight() <= me.getMinimumSize().height && addY < 0) {
            addY = 0;
        }
        me.setSize(oldSize.width + addX, oldSize.height + addY);
        me.setLocation(old.x, old.y - addY);
    }

    private void doRIGHT_BOTTOM(Point p) {
        int addX = p.x - startX;
        int addY = p.y - startY;
        Dimension oldSize = me.getSize();
        if (me.getWidth() <= me.getMinimumSize().width && addX < 0) {
            addX = 0;
        } else {
            startX = p.x;
        }
        if (me.getHeight() <= me.getMinimumSize().height && addY < 0) {
            addY = 0;
        } else {
            startY = p.y;
        }
        me.setSize(oldSize.width + addX, oldSize.height + addY);
    }
}
