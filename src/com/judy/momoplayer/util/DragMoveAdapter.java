/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.logging.Logger;

/**
 * 一个鼠标拖动的适配器
 * @author hadeslee
 */
public class DragMoveAdapter implements MouseListener, MouseMotionListener {

    private static Logger log = Logger.getLogger(DragMoveAdapter.class.getName());
    //以下四项表示吸附的时候，对方所处的方位
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;
    private Component me;
    private int startX,  startY;
    private Rectangle myBound,  otherBound;
    private static final int SNAP = 30;//吸附的象素
    private Config config;//全局的配置对象
    public DragMoveAdapter(Component com, Config config) {
        this.me = com;
        this.config = config;
        myBound = com.getBounds();
        otherBound = new Rectangle();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        if (me == config.getTopParent()) {
            config.updateComponentSnap();
        }
        config.updateDistance();
        startX = e.getX();
        startY = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        //如果是主窗口就免了
        if (me == config.getTopParent()) {
            return;
        }
        getSnapObject();
        config.updateComponentSnap();

    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        int endX = e.getX();
        int endY = e.getY();
        int moveX = endX - startX;
        int moveY = endY - startY;
        Point p = me.getLocation();
        me.setLocation(p.x + moveX, p.y + moveY);
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
            if (dis > 0 && dis < SNAP) {
                int dir = getDirection(dis);
                if (dir != -1) {
                    SnapObject obj = new SnapObject(dir, c1);
                    changeLocation(obj);
                }
            }
        }
        me.getBounds(myBound);
        c1 = config.getLrcWindow();
        if (c1 != null && c1 != me && c1.isShowing()) {
            c1.getBounds(otherBound);
            int dis = Util.getDistance(myBound, otherBound);
            if (dis > 0 && dis < SNAP) {
                int dir = getDirection(dis);
                if (dir != -1) {
                    SnapObject obj = new SnapObject(dir, c1);
                    changeLocation(obj);
                }
            }
        }
        me.getBounds(myBound);
        c1 = config.getPlWindow();
        if (c1 != null && c1 != me && c1.isShowing()) {
            c1.getBounds(otherBound);
            int dis = Util.getDistance(myBound, otherBound);
            if (dis > 0 && dis < SNAP) {
                int dir = getDirection(dis);
                if (dir != -1) {
                    SnapObject obj = new SnapObject(dir, c1);
                    changeLocation(obj);
                }
            }
        }
        me.getBounds(myBound);
        c1 = config.getTopParent();
        if (c1 != null && c1 != me && c1.isShowing()) {
            c1.getBounds(otherBound);
            int dis = Util.getDistance(myBound, otherBound);
            if (dis > 0 && dis < SNAP) {
                int dir = getDirection(dis);
                if (dir != -1) {
                    SnapObject obj = new SnapObject(dir, c1);
                    changeLocation(obj);
                }
            }
        }
//        for (Component com : other.keySet()) {
//            //只有当不和自己当前组件相等才去比较
//            if (me != com&&com.isShowing()) {
//                com.getBounds(otherBound);
//                int dis=Util.getDistance(myBound, otherBound);
//                if(dis>0&&dis<SNAP){
//                    return com;
//                }
//            }
//        }
    }

    public void mouseMoved(MouseEvent e) {
    }
}
