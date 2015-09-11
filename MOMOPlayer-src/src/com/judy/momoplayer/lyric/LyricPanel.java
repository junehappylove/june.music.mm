/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.lyric;

import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Playerable;
import com.judy.momoplayer.util.Util;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 *
 * @author hadeslee
 */
public class LyricPanel extends JPanel implements Runnable, DropTargetListener,
        MouseListener, MouseWheelListener, MouseMotionListener {

    private static final long serialVersionUID = 20071214L;
    private static Logger log = Logger.getLogger(LyricPanel.class.getName());
    private DropTarget dt;//一个拖放的目标
    private Playerable player;//播放器
    private Lyric ly;//表示此歌词面板对应的歌词对象
    public static final int V = 0;//表示纵向显示
    public static final int H = 1;//表示横向显示
    private int state = H;//表示现在是横向还是纵向的
    private volatile boolean isPress;//是已经按下,按下就就不滚动歌词了
    private volatile boolean isDrag;//是否已经动过了
    private int start;//开始的时候座标,在释放的时候,好计算拖了多少
    private int end;//现在的座标
    private volatile boolean isResized;//是否已经重设大小了
    private volatile boolean pause = true;//一个循环的标量
    private final Object lock = new Object();
    private volatile boolean isOver;//是否手在上面
    private Rectangle area = new Rectangle();
    private final String logo = Config.getResource("LyricPanel.logo");
    private boolean isShowLogo = false;
    private Config config;//一个全局配置对象
//    private Component parent;//它是被加到谁的身上去了
    public LyricPanel(Playerable pl) {
        this();
        this.player = pl;
        this.setDoubleBuffered(true);
    }

    public LyricPanel() {
        config = Config.getConfig();
        dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
        state = config.getLpState();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        Thread th = new Thread(this);
        th.setDaemon(true);
        th.start();
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setShowLogo(boolean b) {
        isShowLogo = b;
    }

    /**
     * 设置播放列表
     * @param pl 播放列表
     */
    public void setPlayList(Playerable pl) {
        this.player = pl;
    }

    /**
     * 设置一个新的歌词对象,此方法可能会被
     * PlayList调用
     * @param ly 歌词
     */
    public void setLyric(Lyric ly) {
        this.ly = ly;
        isResized = false;
    }

    public void pause() {
        log.log(Level.INFO, "歌词暂停显示了");
        pause = true;
    }

    public void start() {
        log.log(Level.INFO, "歌词开始显示了...");
        pause = false;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    protected void paintComponent(Graphics g) {
        Graphics2D gd = (Graphics2D) g;
        if (config.isTransparency()) {

        } else {
            super.paintComponent(g);
            gd.setColor(config.getLyricBackground());
            gd.fillRect(0, 0, getWidth(), getHeight());
        }
        if (config.isAntiAliasing()) {
            gd.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//            gd.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }
        g.setFont(config.getLyricFont());
        state = config.getLpState();
        if (ly != null) {
            //只有要重设大小,并且没有重设大小的时候,才去设,否则就不用理它了
            //并且还要不是水平显示，因为水平显示的话，宽度就没有意义了，想多宽就可以多宽
            if (config.isAutoResize() && !isResized && ly.isInitDone() && state == V) {
                int maxWidth = ly.getMaxWidth(g);
                int inset = player.getLyricUI().getInsets().left + player.getLyricUI().getInsets().right;
                JDialog win = config.getLrcWindow();
                if (win != null) {
                    win.setSize(maxWidth + inset, win.getHeight());
                    isResized = true;
                }

            }
            if (isPress && isDrag) {
                if (state == H) {
                    ly.moveH(start - end, g);
                } else {
                    ly.moveV(start - end, g);
                }
            }
            if (state == H) {
                ly.drawH(g);
            } else {
                ly.drawV(g);
            }
            if (isPress && isDrag) {
                if (state == H) {
                    drawTimeH((int) (ly.getTime() / 1000), g);
                } else {
                    drawTimeV((int) (ly.getTime() / 1000), g);
                }

            }
        } else {
            g.setColor(config.getLyricHilight());
            int width = Util.getStringWidth(Config.NAME, g);
            int height = Util.getStringHeight(Config.NAME, g);
            Util.drawString(g, Config.NAME, (getWidth() - width) / 2, (getHeight() - height) / 2);
        }
        if (isShowLogo) {
            drawLogo(g);
        }
    }

    /**
     * 画出自己的LOGO
     * @param g 画笔
     */
    private void drawLogo(Graphics g) {
        g.setFont(new Font("Dialog", Font.BOLD, 14));
        int width = Util.getStringWidth(logo, g);
        int height = Util.getStringHeight(logo, g);
        area.x = 5;
        area.y = 5;
        area.width = width;
        area.height = height;
        if (isOver) {
            g.setColor(Color.RED);
        } else {
            Color bg = config.getLyricBackground();
            int rgb = bg.getRGB();
            int xor = ~rgb;
            rgb = xor & 0x00ffffff;
            Color c = new Color(rgb);
            g.setColor(c);
        }
        //System.out.println("===============================logo what  ? you know  what: "+logo);
        Util.drawString(g, logo, 5, 5);
    }

    /**
     * 得到播放器对象,此方法一般是给
     * 在线搜索歌词框用的
     * @return 播放器
     */
    public Playerable getPlayer() {
        return this.player;
    }

    /**
     * 画出正在拖动的时候的时间,以便更好的掌握进度
     * 这是画出垂直方向的拖动时间
     * @param sec 当前的秒数
     * @param g 画笔
     */
    private void drawTimeV(int sec, Graphics g) {
        String s = Util.secondToString(sec);
        int width = getWidth();
        int height = getHeight();
        int centerY = height / 2;

        g.drawLine(3, centerY - 5, 3, centerY + 5);
        g.drawLine(width - 3, centerY - 5, width - 3, centerY + 5);
        g.drawLine(3, centerY, width - 3, centerY);
        g.setFont(new Font(Config.getResource("LyricPanel.font"), Font.PLAIN, 14));
        g.setColor(Util.getColor(config.getLyricForeground(), config.getLyricHilight()));
        Util.drawString(g, s, width - Util.getStringWidth(s, g), (height / 2 - Util.getStringHeight(s, g)));
    }

    /**
     * 画出正在拖动的时候的时间,以便更好的掌握进度
     * 这是画出水平方向的拖动时间
     * @param sec 当前的秒数
     * @param g 画笔
     */
    private void drawTimeH(int sec, Graphics g) {
        String s = Util.secondToString(sec);
        int centerX = getWidth() / 2;
        int height = getHeight();

        g.drawLine(centerX - 5, 3, centerX + 5, 3);
        g.drawLine(centerX - 5, height - 3, centerX + 5, height - 3);
        g.drawLine(centerX, 3, centerX, height - 3);
        g.setFont(new Font(Config.getResource("LyricPanel.font"), Font.PLAIN, 14));
        g.setColor(Util.getColor(config.getLyricForeground(), config.getLyricHilight()));
        Util.drawString(g, s, centerX, (height - Util.getStringHeight(s, g)));
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(config.getRefreshInterval());
                if (pause) {
                    synchronized (lock) {
                        lock.wait();
                    }
                } else {
                    if (ly != null) {
                        ly.setHeight(this.getHeight());
                        ly.setWidth(this.getWidth());
                        ly.setTime(player.getTime());
                        repaint();
                    }
                }
            } catch (Exception exe) {
                exe.printStackTrace();
            }
        }
    }

    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
    }

    public void dragOver(DropTargetDragEvent dtde) {
    }

    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    public void dragExit(DropTargetEvent dte) {
    }

    public void drop(DropTargetDropEvent e) {
        try {
            //得到操作系统的名字，如果是windows，则接受的是DataFlavor.javaFileListFlavor
            //如果是linux则接受的是DataFlavor.stringFlavor
            String os = System.getProperty("os.name");
            if (os.startsWith("Windows")) {
                if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                    Transferable tr = e.getTransferable();
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    @SuppressWarnings("unchecked")
                    java.util.List<File> s = (java.util.List<File>) tr.getTransferData(
                            DataFlavor.javaFileListFlavor);
                    if (s.size() == 1) {
                        File f = s.get(0);
                        if (f.isFile() && player.getCurrentItem() != null) {
                            ly = new Lyric(f, player.getCurrentItem());
                            ly.setWidth(this.getWidth());
                            ly.setHeight(this.getHeight());
                            player.setLyric(ly);
                        }
                    }
                    e.dropComplete(true);
                }
            } else if (os.startsWith("Linux")) {
                if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Transferable tr = e.getTransferable();
                    String[] ss = tr.getTransferData(DataFlavor.stringFlavor).toString().split("\r\n");
                    if (ss.length == 1) {
                        File f = new File(new URI(ss[0]));
                        if (f.isFile() && player.getCurrentItem() != null) {
                            ly = new Lyric(f, player.getCurrentItem());
                            ly.setWidth(this.getWidth());
                            ly.setHeight(this.getHeight());
                            player.setLyric(ly);
                        }
                    }
                    e.dropComplete(true);
                }
            } else {
                e.rejectDrop();
            }
        } catch ( Exception io) {
            io.printStackTrace();
            e.rejectDrop();
        }
    }

    public void setState(int state) {
        if (state == H || state == V) {
            this.state = state;
        }
    }

    public void setResized(boolean b) {
        isResized = b;
    }

    public void mouseClicked(MouseEvent e) {
//        //双击的时候,改变显示风格
//        if (e.getClickCount() == 2) {
//            if (state == H) {
//                state = V;
//            } else {
//                state = H;
//            }
//        }
    }

    public void mousePressed(MouseEvent e) {
        if (ly == null) {
            return;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (area != null && area.contains(e.getPoint())) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.blogjava.net/hadeslee"));
                } catch (URISyntaxException ex) {
                    Logger.getLogger(LyricPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(LyricPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (ly != null && ly.canMove()) {
                isPress = true;
                isDrag = false;
                if (state == V) {
                    start = e.getY();
                } else {
                    start = e.getX();
                }
                ly.startMove();
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (ly == null) {
            return;
        }
        //如果是鼠标左键
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (ly.canMove() && isDrag) {
                if (state == H) {
                    end = e.getX();
                } else {
                    end = e.getY();
                }
                long time = ly.getTime();
                player.setTime(time);
                start = end = 0;
            }
            ly.stopMove();
            isPress = false;
            isDrag = false;
        //如果是鼠标右键
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            if (player.getCurrentItem() == null) {
                return;
            }
            JPopupMenu pop = new JPopupMenu();
            Util.generateLyricMenu(pop, this);
            pop.show(this, e.getX(), e.getY());
        }
    }

    /**
     * 隐藏自己
     */
    public void hideMe() {
        player.setShowLyric(false);
    }

    public Lyric getLyric() {
        return ly;
    }

    public void mouseEntered(MouseEvent e) {
        if (ly != null && ly.canMove()) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void mouseExited(MouseEvent e) {
        this.setCursor(Cursor.getDefaultCursor());
        isOver = false;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (ly == null) {
            return;
        }
        //只有当配置允许鼠标滚动调整时间才可以
        if (config.isMouseScrollAjustTime()) {
            int adjust = e.getUnitsToScroll() * 100;//每转动一下,移动300毫秒
            ly.adjustTime(adjust);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (ly == null) {
            return;
        }
        if (ly.canMove() && isPress) {
            isDrag = true;
            if (state == H) {
                end = e.getX();
            } else {
                end = e.getY();
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
        if (area != null && area.contains(e.getPoint())) {
            isOver = true;
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            isOver = false;
            mouseEntered(e);
        }
    }
}
