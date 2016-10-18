/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.player.ui;

import com.judy.momoplayer.player.BasicPlayer;
import com.judy.momoplayer.setting.AboutPanel;
import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.DragMoveAdapter;
import com.judy.momoplayer.util.Loader;
import com.judy.momoplayer.util.Util;
import com.sun.jna.examples.WindowUtils;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * 程序运行的主类
 *
 * @author June
 */
public class Main extends JFrame implements Loader {

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException exe) {
            exe.printStackTrace();
        } catch (IllegalAccessException exe) {
            exe.printStackTrace();
        } catch (InstantiationException exe) {
            exe.printStackTrace();
        } catch (UnsupportedLookAndFeelException exe) {
            exe.printStackTrace();
        }
    }
    private static final Logger log = Logger.getLogger(Main.class.getName());
    private static final long serialVersionUID = 20071214L;
    private PlayerUI mp = null;//播放器
    private JDialog eqWin = null;//均衡器窗口
    private JDialog plWin = null;//播放列表窗口
    private JDialog lrcWin = null;//歌词列表窗口
    private static final Config config = Config.getConfig();//配置信息
//    private RoundRectangle2D.Float rectPl,  rectLrc;

    public Main() {
        //必须使用下面这一行代码，实现窗口透明效果
        System.setProperty("sun.java2d.noddraw", "true");
        Logger main = Logger.getLogger("com");
        main.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        try {
            LogManager lm = LogManager.getLogManager();
            lm.readConfiguration(Main.class.getResourceAsStream("/com/hadeslee/momoplayer/util/Log.properties"));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        CheckThread th = new CheckThread();//检查软件更新类
        th.setDaemon(true);
        th.start();
        final Main player = new Main();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                player.loadUI();
                player.loadJS();
                player.loadPlaylist();
                player.addSystemTray();
                player.boot();
            }
        });
    }

    public void boot() {
        if (config.isAutoPlayWhenStart()) {
            if (config.isMaintainLastPlay()) {
                mp.setLastRate(config.getLastRate());
            }
            mp.pressStart();//我的播放器开启
        }
    }

    public void loadJS() {
        BasicPlayer bplayer = new BasicPlayer();
        List mixers = bplayer.getMixers();
        config.setMixers(mixers);
        for (Object obj : mixers) {
            log.log(Level.INFO, "\u53ef\u7528\u7684MIXER\uff1a{0}", obj);
        }
        if (mixers != null) {
            Iterator it = mixers.iterator();
            String mixer = config.getAudioDevice();
            log.log(Level.INFO, "Config.Mixer={0}", mixer);
            boolean mixerFound = false;
            if ((mixer != null) && (mixer.length() > 0)) {
                // Check if mixer is valid. 
                while (it.hasNext()) {
                    if (((String) it.next()).equals(mixer)) {
                        bplayer.setMixerName(mixer);
                        mixerFound = true;
                        break;
                    }
                }
            }
            if (mixerFound == false) {
                // Use first mixer available.
                it = mixers.iterator();
                if (it.hasNext()) {
                    mixer = (String) it.next();
                    bplayer.setMixerName(mixer);
                    config.setAudioDevice(mixer);
                }
            }
        }
        // Register the front-end to low-level player events.
        bplayer.addBasicPlayerListener(mp);
        // Adds controls for front-end to low-level player.
        mp.setController(bplayer);
    }

    public void loadPlaylist() {
        mp.loadPlaylist();
    }

    public void loadUI() {
        this.setResizable(false);
//        doTest();
        config.setTopParent(this);
        setTitle(Config.TITLETEXT);
        ClassLoader cl = this.getClass().getClassLoader();
        URL iconURL = cl.getResource("com/hadeslee/momoplayer/pic/player/icon.png");
        if (iconURL != null) {
            ImageIcon jlguiIcon = new ImageIcon(iconURL);
            setIconImage(jlguiIcon.getImage());
            config.setIconParent(jlguiIcon);
        }
        setUndecorated(true);
        mp = new PlayerUI();
        mp.loadUI(this, config);
        setContentPane(mp);
        pack();
        config.setPlayer(mp);

        eqWin = new JDialog(this);
        eqWin.setContentPane(mp.getEqualizerUI());
        eqWin.setUndecorated(true);
        eqWin.pack();
        eqWin.setVisible(false);
        config.setEqWindow(eqWin);
        plWin = new JDialog(this);
        mp.getPlaylistUI().loadUI(plWin, config);
        plWin.setContentPane(mp.getPlaylistUI());
        plWin.setMinimumSize(mp.getPlaylistUI().getMinimumSize());
        plWin.setUndecorated(true);
        plWin.pack();
        plWin.setVisible(false);
        config.setPlWindow(plWin);
        lrcWin = new JDialog(this);
        mp.getLyricUI().loadUI(lrcWin, config);
        lrcWin.setContentPane(mp.getLyricUI());
        lrcWin.setMinimumSize(mp.getLyricUI().getMinimumSize());
        lrcWin.setUndecorated(true);
        lrcWin.pack();
        lrcWin.setVisible(false);
        config.setLrcWindow(lrcWin);
        if (config.isTransparency()) {
            WindowUtils.setWindowTransparent(lrcWin, true);
            mp.getLyricUI().setBorderEnabled(config.isShowLrcBorder());
        }
        // Window listener
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Closing window (Alt+F4 under Win32)
                mp.closePlayer();
            }
        });
        addMouseMoveListener();
        // Keyboard shortcut
        setKeyBoardShortcut();
        // Display front-end
        setLocation(config.getXLocation(), config.getYLocation());
        setVisible(true);
        showOtherWindow();
        if (config.isStartAutoMinimize()) {
            minimize();
        }

    }

    private void addMouseMoveListener() {

        DragMoveAdapter dm1 = new DragMoveAdapter(eqWin, config);
        mp.getEqualizerUI().addMouseListener(dm1);
        mp.getEqualizerUI().addMouseMotionListener(dm1);

        DragMoveAdapter dm = new DragMoveAdapter(this, config);
        mp.addMouseListener(dm);
        mp.addMouseMotionListener(dm);
    }

    private void showOtherWindow() {
        if (config.getLrcSize() != null) {
            lrcWin.setAlwaysOnTop(config.isLyricTopShow());
            lrcWin.setSize(config.getLrcSize());
        }
        if (config.getPlSize() != null) {
            plWin.setSize(config.getPlSize());
        }
        if (config.isShowEq()) {
            Point p = config.getEqLocation();
            if (p == null) {
                p = new Point(config.getXLocation(), config.getYLocation() + this.getHeight());
            }
            eqWin.setLocation(p);
            eqWin.setVisible(true);
        }
        if (config.isShowPlayList()) {
            Point p = config.getPlLocation();
            if (p == null) {
                p = new Point(config.getXLocation(), config.getYLocation() + this.getHeight() + eqWin.getHeight());
            }
            plWin.setLocation(p);
            plWin.setVisible(true);
        }
        if (config.isShowLrc()) {
            Point p = config.getLrcLocation();
            if (p == null) {
                p = new Point(config.getXLocation() + this.getWidth(), config.getYLocation());
            }
            lrcWin.setLocation(p);
            lrcWin.setVisible(true);
            mp.getLyricUI().start();
        }
    }

    private void addWindowStateListener() {

        this.addWindowStateListener(new WindowStateListener() {

            public void windowStateChanged(WindowEvent e) {
                int state = e.getNewState();
                if (state == JFrame.ICONIFIED) {
                    eqWin.setVisible(false);
                    plWin.setVisible(false);
                    if (config.isShowLrc() && config.isLyricTopShow()) {
                        lrcWin.setVisible(true);
                    } else {
                        lrcWin.setVisible(false);
                    }
                } else if (state == JFrame.NORMAL) {
                    if (config.isShowEq()) {
                        eqWin.setVisible(true);
                    }
                    if (config.isShowPlayList()) {
                        plWin.setVisible(true);
                    }
                    if (config.isShowLrc()) {
                        lrcWin.setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * 设置键盘快捷键
     */
    public void setKeyBoardShortcut() {
        //B表示下一首歌曲
        KeyStroke nextStroke = KeyStroke.getKeyStroke(KeyEvent.VK_B, 0, true);
        //空格表示播放和暂停
        KeyStroke pauseStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true);
        //C表示播放
        KeyStroke playStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, 0, true);
        //V表示停止
        KeyStroke stopStroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, 0, true);
        //三个快捷键，显示三个窗体
        KeyStroke eqStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, true);
        KeyStroke plStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, true);
        KeyStroke lrcStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true);
        String nextID = "NEXT";
        String pauseID = "PAUSE";
        String playID = "PLAY";
        String stopID = "STOP";
        String eqID = "EQ";
        String plID = "PL";
        String lrcID = "LRC";
        Action nextAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (mp != null) {
                    mp.processNext(e.getModifiers());
                }
            }
        };
        Action pauseAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (mp != null) {
                    mp.processPause(e.getModifiers());
                }
            }
        };
        Action playAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (mp != null) {
                    mp.processPlay(e.getModifiers());
                }
            }
        };
        Action stopAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                if (mp != null) {
                    mp.processStop(e.getModifiers());
                }
            }
        };
        Action lrcAction = new AbstractAction() {

            public void actionPerformed(ActionEvent ae) {
//                toggleLyricWindow(!lrcWin.isShowing());
                mp.lrc.doClick();
            }
        };
        Action eqAction = new AbstractAction() {

            public void actionPerformed(ActionEvent ae) {
//                toggleEqualizer(!eqWin.isShowing());
                mp.eq.doClick();
            }
        };
        Action plAction = new AbstractAction() {

            public void actionPerformed(ActionEvent ae) {
//                togglePlaylist(!plWin.isShowing());
                mp.pl.doClick();
            }
        };
        setKeyboardAction(nextID, nextStroke, nextAction);
        setKeyboardAction(pauseID, pauseStroke, pauseAction);
        setKeyboardAction(playID, playStroke, playAction);
        setKeyboardAction(stopID, stopStroke, stopAction);
        setKeyboardAction(lrcID, lrcStroke, lrcAction);
        setKeyboardAction(eqID, eqStroke, eqAction);
        setKeyboardAction(plID, plStroke, plAction);

    }

    public void setKeyboardAction(String id, KeyStroke key, Action action) {
        mp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, id);
        mp.getActionMap().put(id, action);
        mp.getPlaylistUI().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, id);
        mp.getPlaylistUI().getActionMap().put(id, action);
        mp.getEqualizerUI().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, id);
        mp.getEqualizerUI().getActionMap().put(id, action);
        mp.getLyricUI().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, id);
        mp.getLyricUI().getActionMap().put(id, action);
    }

    public void addSystemTray() {
        if (SystemTray.isSupported()) {
            try {
                final PopupMenu pm = new PopupMenu(Config.NAME);
                MenuItem about = new MenuItem(Config.getResource("menuitem.about"));
                MenuItem showMain = new MenuItem(Config.getResource("menuitem.showMain"));
                MenuItem set = new MenuItem(Config.getResource("menuitem.set"));
                MenuItem exit = new MenuItem(Config.getResource("menuitem.exit"));
                set.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ae) {
                        JDialog jd = config.getOptionDialog();
                        jd.setVisible(true);
                    }
                });
                exit.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ae) {
                        mp.closePlayer();
                    }
                });
                showMain.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ae) {
                        Main.this.setVisible(true);
                        Main.this.setExtendedState(JFrame.NORMAL);
                        if (config.isShowEq()) {
                            eqWin.setVisible(true);
                        }
                        if (config.isShowPlayList()) {
                            plWin.setVisible(true);
                        }
                        if (config.isShowLrc()) {
                            lrcWin.setVisible(true);
                        }
                    }
                });
                about.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ae) {
                        JOptionPane.showMessageDialog(null,
                                new AboutPanel());
                    }
                });
                pm.add(about);
                pm.add(set);
                pm.add(showMain);
                pm.add(exit);
                Image img = Util.getImage("player/icon.png");
                final TrayIcon ti = new TrayIcon(img, Config.NAME, pm);
                ti.setImageAutoSize(true);
                ti.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent ae) {
                        Main.this.setVisible(true);
                        Main.this.setExtendedState(JFrame.NORMAL);
                        if (config.isShowEq()) {
                            eqWin.setVisible(true);
                        }
                        if (config.isShowPlayList()) {
                            plWin.setVisible(true);
                        }
                        if (config.isShowLrc()) {
                            lrcWin.setVisible(true);
                        }
                    }
                });
                SystemTray.getSystemTray().add(ti);
            } catch (AWTException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void loaded() {
    }

    public void close() {
        config.setLocation(getLocation().x, getLocation().y);
        config.setLrcLocation(lrcWin.getLocation());
        config.setEqLocation(eqWin.getLocation());
        config.setPlLocation(plWin.getLocation());
        config.setLrcSize(lrcWin.getSize());
        config.setPlSize(plWin.getSize());
        Config.save();
        dispose();
        System.exit(0);
    }

    public void minimize() {
        if (config.isMiniHide()) {
            setVisible(false);
            eqWin.setVisible(false);
            plWin.setVisible(false);
            if (config.isShowLrc() && config.isLyricTopShow()) {
                lrcWin.setVisible(true);
            } else {
                lrcWin.setVisible(false);
            }
        } else {
            this.setState(JFrame.ICONIFIED);
        }
    }

    public void togglePlaylist(boolean enabled) {
        config.setShowPlayList(enabled);
        if (plWin != null) {
            showOrHide(plWin, enabled, config.isShadow());
        }
    }

    public void toggleEqualizer(boolean enabled) {
        config.setShowEq(enabled);
        if (eqWin != null) {
            showOrHide(eqWin, enabled, config.isShadow());
        }
    }

    public void toggleLyricWindow(boolean enabled) {
        config.setShowLrc(enabled);
        if (lrcWin != null) {
            if (enabled) {
                mp.getLyricUI().start();
            } else {
                mp.getLyricUI().pause();
            }
            showOrHide(lrcWin, enabled, config.isShadow());
        }
    }

    private void showOrHide(final Window win, final boolean enabled, final boolean isAlpha) {
        new Thread() {

            @Override
            public void run() {
                showOrHide0(win, enabled, isAlpha);
            }
        }.start();
    }

    /**
     * 设置窗口出现或者隐起来 可以设置有特效或者无特效
     *
     * @param win 窗体
     * @param enabled 是否可见
     * @param isAlpha 是否要渐变
     */
    private void showOrHide0(Window win, boolean enabled, boolean isAlpha) {
        if (enabled) {
            win.setVisible(true);
            if (!config.isLinux() && isAlpha && WindowUtils.isWindowAlphaSupported()) {
//                WindowUtils.setWindowTransparent(win, true);
                for (float f = 0.f; f <= 1.0f; f += 0.1f) {
                    try {
                        Thread.sleep(20);
                        WindowUtils.setWindowAlpha(win, f);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                WindowUtils.setWindowAlpha(win, 1.0f);
            }
        } else {
            if (!config.isLinux() && isAlpha && WindowUtils.isWindowAlphaSupported()) {
//                WindowUtils.setWindowTransparent(win, true);
                for (float f = 1.0f; f >= 0; f -= 0.1f) {
                    try {
                        Thread.sleep(20);
                        WindowUtils.setWindowAlpha(win, f);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                WindowUtils.setWindowAlpha(win, 0.0f);
            }
            win.setVisible(false);
        }
    }

    @Override
    public synchronized void setLocation(int x, int y) {
        Point now = new Point(x, y);
        super.setLocation(now.x, now.y);
        Config config1 = Config.getConfig();
        if (plWin != null && (config1.isSnapPlWindow() || !plWin.isShowing())) {
            Point dis = config1.getDisPl();
            if (dis != null) {
                plWin.setLocation(now.x + dis.x, now.y + dis.y);
            }
        }
        if (eqWin != null && (config1.isSnapEqWindow()) || !eqWin.isShowing()) {
            Point dis = config1.getDisEq();
            if (dis != null) {
                eqWin.setLocation(now.x + dis.x, now.y + dis.y);
            }
        }
        if (lrcWin != null && (config1.isSnapLrcWindow()) || !lrcWin.isShowing()) {
            Point dis = config1.getDisLrc();
            if (dis != null) {
                lrcWin.setLocation(now.x + dis.x, now.y + dis.y);
            }
        }
    }

    public void reRange() {
        this.setLocation(300, 100);
        eqWin.setSize(285, 155);
        plWin.setSize(285, 155);
        lrcWin.setSize(285, 465);
        eqWin.setLocation(300, 255);
        plWin.setLocation(300, 410);
        lrcWin.setLocation(585, 100);
        config.updateDistance();
    }

    public JDialog changeLrcDialog() {
        Point p = lrcWin.getLocation();
        Dimension size = lrcWin.getSize();
        mp.getLyricUI().pause();
        lrcWin.removeAll();
        lrcWin.dispose();
        lrcWin = new JDialog(this);
        lrcWin.setContentPane(mp.getLyricUI());
        lrcWin.setMinimumSize(mp.getLyricUI().getMinimumSize());
        lrcWin.setUndecorated(true);
        config.setLrcWindow(lrcWin);
        lrcWin.setSize(size);
        lrcWin.setLocation(p);
        lrcWin.setAlwaysOnTop(config.isLyricTopShow());
        mp.getLyricUI().setParent(lrcWin);
        if (config.isShowLrc()) {
            lrcWin.setVisible(true);
        }
        return lrcWin;
    }
}
