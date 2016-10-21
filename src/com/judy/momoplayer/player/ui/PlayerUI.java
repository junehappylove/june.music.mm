/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.player.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.SourceDataLine;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.judy.momoplayer.equalizer.EqualizerUI;
import com.judy.momoplayer.lyric.Lyric;
import com.judy.momoplayer.lyric.LyricUI;
import com.judy.momoplayer.player.BasicController;
import com.judy.momoplayer.player.BasicPlayerEvent;
import com.judy.momoplayer.player.BasicPlayerException;
import com.judy.momoplayer.player.BasicPlayerListener;
import com.judy.momoplayer.playlist.PlayList;
import com.judy.momoplayer.playlist.PlayListItem;
import com.judy.momoplayer.playlist.PlayListUI;
import com.judy.momoplayer.setting.OptionDialog;
import com.judy.momoplayer.tag.SongInfoDialog;
import com.judy.momoplayer.util.AudioChart;
import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.FileNameFilter;
import com.judy.momoplayer.util.Loader;
import com.judy.momoplayer.util.MOMOSlider;
import com.judy.momoplayer.util.Playerable;
import com.judy.momoplayer.util.SongInfo;
import com.judy.momoplayer.util.Util;

/**
 *
 * @author judy
 */
public class PlayerUI extends JPanel implements Playerable, ActionListener, ChangeListener, BasicPlayerListener {

    private static final long serialVersionUID = 20071214L;
    //表示播放器的各种状态
    public static final int INIT = 0;
    public static final int OPEN = 1;
    public static final int PLAY = 2;
    public static final int PAUSE = 3;
    public static final int STOP = 4;
    private static final Logger log = Logger.getLogger(PlayerUI.class.getName());
    private JButton pre, next, play, stop;//四个播放相关按钮
    private JButton close, min;//右上角的三个图标
    private JLabel about;//关于信息的一个标签，添加了鼠标事件
    private JLabel state, channel;//表示标题和信息的一个标签
    protected JToggleButton pl, lrc, eq, speaker;//三个状态按钮,和一个音量按钮
    private MOMOSlider pos, pan, volume;//三个进度条
    private AudioChart audioChart;//示波器
    private Lyric lyric;//一个歌词对象
    private BasicController player;//基本的播放器对象
    private Map<?, ?> audioInfo;//里面存的是所有有关的音频信息
    private int playerState;//播放器的当前状态
    private Config config;//一个配置对象
    @SuppressWarnings("unused")
	private long lastScrollTime;//上一次的滚动歌曲信息的时候
    private boolean posValueJump;//表示现在位置条是否在拖动中
    private PlayListItem currentItem;//当前正在播放的列表项
    private long secondsAmount;//总共用去的秒数
    private Loader loader;//一个负责总装载的接口,一般由主窗口实现
    private PlayList playlist;//一个播放列表的实现,
    private String currentSongName;//当前正在播放的歌曲的名字
    private String currentFileOrURL;//当前正在放的歌曲的文件名或者URL路径
    private boolean currentIsFile;//当前正在播放的是否是文件,因为可能是网络上的URL
    private String titleText;//标题应该显示的内容
    private PlayListUI playlistUI;//摠放列表的UI的引用
    private EqualizerUI equalizerUI;//调音器的UI引用
    private LyricUI lyricUI;//歌词显示面板UI的引用
    private SongInfo songInfo;//当前正在播放的歌曲的一些信息,用于滚动显示在时间下面
    private boolean posDragging;//指示当前的进度条是否在拖动中
    private double posValue;//指示当前的进度条所在的位置的比例
    private TimePanel timePanel;//显时间的面板
    private SongInfoPanel infoPanel;//显示歌曲信息的面板
    private Image[] playImgs, pauseImgs;//用于显示播放和暂停的图标数组
    private String currentState;//当前的状态，比如正在播放，停止，暂停
    private boolean isSeeked;//是否已经seek了
    private double lastRate;//最后的比率
    private final Object lock = new Object();
    private volatile boolean scrollTitle;//是否滚动标题栏
    private String title = Config.NAME;
    private Thread thread;//显示任务栏的标题滚动线程
    private long seekedTime;//拖过的时间
//    private //一个监听鼠标滚轮滚动的监听器，用于调节音量

    public PlayerUI() {
        super(null);
        setPreferredSize(new Dimension(285, 155));

    }

    /**
     * 得到媒体当前的时间，以毫秒为单位
     *
     * @return 时间
     */
    public long getTime() {
        if (player == null) {
            return -1;
        } else {
            return player.getMicrosecondPosition() / 1000 + seekedTime;
        }
    }

    void setLastRate(double rate) {
        this.lastRate = rate;
    }

    public void setPlayList(PlayList playlist) {
        this.playlist = playlist;
    }

    public AudioChart getAudioChart() {
        return audioChart;
    }

    public boolean loadPlaylist() {
        boolean loaded = false;
        String lastPlay = config.getCurrentFileOrUrl();
        log.log(Level.INFO, "lastPlay={0}", lastPlay);
        if (lastPlay != null) {
            for (PlayListItem item : playlist.getAllItems()) {
                if (item.getLocation().equals(lastPlay)) {
                    log.log(Level.INFO, "找到了最后要播的匹配!!");
                    this.setCurrentSong(item);
                    break;
                }
            }
        }
        return loaded;
    }

    public void processJumpToFile(int modifiers) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void processPreferences(int modifiers) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void pressStart() {
        play.doClick();
    }

    public void pressEq() {
        eq.doClick();
    }

    public void pressLrc() {
        lrc.doClick();
    }

    public void pressPl() {
        pl.doClick();
    }

    /**
     * 给此面板本身以及所有的子组件都添加上这个 监听器
     */
    private void addMouseVolumeListener() {
        MouseVolumeListener mv = new MouseVolumeListener();
        this.addMouseWheelListener(mv);
        int count = this.getComponentCount();
        for (int i = 0; i < count; i++) {
            this.getComponent(i).addMouseWheelListener(mv);
        }
    }

    private void initUI() {
        //初始化按钮
        eq = Util.createJToggleButton("player/eq", config.isShowEq() ? Config.EQ_OFF : Config.EQ_ON, this, config.isShowEq());
        pl = Util.createJToggleButton("player/pl", config.isShowPlayList() ? Config.PL_OFF : Config.PL_ON, this, config.isShowPlayList());
        lrc = Util.createJToggleButton("player/lrc", config.isShowLrc() ? Config.LRC_OFF : Config.LRC_ON, this, config.isShowLrc());
        speaker = Util.createJToggleButton("player/speaker", config.isMute() ? Config.VOL_ON : Config.VOL_OFF, this, config.isMute());
        pre = Util.createJButton("player/pre", Config.PREVIOUS, this);
        next = Util.createJButton("player/next", Config.NEXT, this);
        play = Util.createJButton("player/play", Config.PLAY, this);
        stop = Util.createJButton("player/stop", Config.STOP, this);
        close = Util.createJButton("player/close", Config.CLOSE, this);
        min = Util.createJButton("player/min", Config.MINIMIZE, this);
        //初始化播放和暂停的各三个图标
        playImgs = Util.getImages("player/play", 3);
        pauseImgs = Util.getImages("player/pause", 3);

        setButtonLocation(eq, 160, 94);
        setButtonLocation(pl, 201, 94);
        setButtonLocation(lrc, 241, 94);
        setButtonLocation(speaker, 28, 138);
        setButtonLocation(pre, 155, 134);
        setButtonLocation(play, 188, 134);
        setButtonLocation(stop, 218, 134);
        setButtonLocation(next, 249, 134);
        setButtonLocation(close, 275, 7);
        setButtonLocation(min, 260, 7);

        //初始化进度条
        Image ball1 = Util.getImage("player/ball1.png");
        Image ball2 = Util.getImage("player/ball2.png");
        Image ball3 = Util.getImage("player/ball3.png");
        pos = Util.createSlider(0, Config.POSBARMAX, 0,
                ball1, ball2, ball3,
                Util.getImage("player/pos1.png"), Util.getImage("player/pos2.png"),
                this, SwingConstants.HORIZONTAL);
        pan = Util.createSlider(-Config.BALANCEMAX, Config.BALANCEMAX, 0,
                Util.getImage("player/panBall1.png"), Util.getImage("player/panBall2.png"), null,
                null, null,
                this, SwingConstants.HORIZONTAL);
        volume = Util.createSlider(0, Config.VOLUMEMAX, Config.VOLUMEMAX,
                ball1, ball2, ball3,
                Util.getImage("player/volume1.png"), Util.getImage("player/volume2.png"),
                this, SwingConstants.HORIZONTAL);
        pos.setBounds(10, 108, 270, 15);
        volume.setBounds(43, 130, 82, 15);
        pan.setBounds(160, 72, 90, 13);
        this.add(pos);
        this.add(volume);
        this.add(pan);
        pan.setValue(config.getPanValue());
        volume.setValue(config.getGainValue());
        //初始化示波器
        audioChart = new AudioChart();
        audioChart.setDisplayMode(config.getAudioChartDisplayMode());
        audioChart.setSpectrumAnalyserBandCount(config.getAudioChartBarCount());
        audioChart.setPeakColor(config.getAudioChartPeakColor());
        audioChart.setScopeColor(config.getAudioChartlineColor());
        Color c3 = config.getAudioChartTopColor();
        Color c2 = config.getAudioChartCenterColor();
        Color c1 = config.getAudioChartbottomColor();
        audioChart.setSpectrumAnalyserColors(Util.getColors(c1, c2, c3, 256));
//        audioChart.setVisColor(AudioChart.getDefaultSpectrumAnalyserColors());
//        audioChart.setVisColor("0,0,0\n" +
//                "0,0,0\n" +
//                "255,255,255\n" +
//                "1,251,254\n" +
//                "255,255,255\n" +
//                "1,251,254\n" +
//                "255,255,255\n" +
//                "1,251,254\n" +
//                "255,255,255\n" +
//                "1,251,254\n" +
//                "255,255,255\n" +
//                "1,251,254\n" +
//                "255,255,255\n" +
//                "202,255,255\n" +
//                "255,255,255\n" +
//                "226,255,255\n" +
//                "255,255,255\n" +
//                "255,255,255\n" +
//                "255,255,255\n" +
//                "255,255,255\n" +
//                "255,255,255\n" +
//                "255,255,255\n" +
//                "255,255,255\n" +
//                "1,251,254");
        audioChart.setSpectrumAnalyserDecay(0.05f);
        audioChart.setBounds(158, 30, 92, 37);
        this.add(audioChart);
        //初始化时间面板
        timePanel = new TimePanel();
        Dimension di = timePanel.getPreferredSize();
        timePanel.setBounds(15, 45, di.width, di.height);
        this.add(timePanel);
        //初始化titile和info标签
        state = new JLabel(Config.getResource("state.stop"));
        channel = new JLabel(Config.getResource("songinfo.channel.stereo"));
        channel.setForeground(Color.WHITE);
        state.setForeground(Color.WHITE);
        state.setBounds(94, 52, 50, 14);
        channel.setBounds(94, 37, 50, 14);
        this.add(state);
        this.add(channel);
        //初始化歌曲信息面板
        infoPanel = new SongInfoPanel();
        songInfo = new SongInfo();
        infoPanel.setInfo(songInfo);
        infoPanel.setBounds(17, 88, 117, 14);
        //初始化LOGO做为ABOUT的图标
        Image img = Util.getImage("logo.png");
        img = img.getScaledInstance(19, 19, Image.SCALE_SMOOTH);
        about = new JLabel(new ImageIcon(img));
        about.setBounds(256, 30, 19, 19);
        about.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent me) {
                showOptionDialog(me);
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                about.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent me) {
                about.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        this.add(about);
        this.add(infoPanel);
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent me) {
                if (me.getButton() == MouseEvent.BUTTON3) {
                    showOptionDialog(me);
                }
            }
        });

    }

    public Loader getLoader() {
        return loader;
    }

    /**
     * 用于显示关于的一系列菜单的方法 供自己还有Main调用,因为Main里面 注册了系统栏图标
     *
     * @param me
     */
    public void showOptionDialog(MouseEvent me) {
        JPopupMenu pop = new JPopupMenu("Set");
        pop.add(new AbstractAction(Config.getResource("PlayerUI.option")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 228308452845020080L;

            public void actionPerformed(ActionEvent e) {
                JDialog jd = config.getOptionDialog();
                jd.setVisible(true);
            }
        });
        pop.addSeparator();
        pop.add(createPlayMenu());
        pop.add(createVolumeMenu());
        pop.add(createPlayModeMenu());
        pop.add(createPlayWhichMenu());
        pop.addSeparator();
        pop.add(createAudioChartMenu());
        pop.add(createLyricMenu());
        pop.add(createEqMenu());
        pop.addSeparator();
        pop.add(createViewMenu());
        pop.add(new AbstractAction(Config.getResource("PlayerUI.minimize")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 6019891550248081318L;

            public void actionPerformed(ActionEvent e) {
                loader.minimize();
            }
        });
        pop.add(new AbstractAction(Config.getResource("PlayerUI.exit")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 6472559900409297392L;

            public void actionPerformed(ActionEvent e) {
                closePlayer();
            }
        });
        pop.show(me.getComponent(), me.getX(), me.getY());
    }

    private JMenu createPlayMenu() {
        JMenu menu = new JMenu(Config.getResource("PlayerUI.playControl"));
        menu.add(new AbstractAction(playerState == PLAY ? Config.getResource("PlayerUI.pause") : Config.getResource("PlayerUI.play")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = -6079145256199154037L;

            public void actionPerformed(ActionEvent e) {
                if (playerState == PLAY) {
                    processPause(0);
                } else {
                    processPlay(0);
                }
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.stop")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 5874796005559506529L;

            public void actionPerformed(ActionEvent e) {
                processStop(0);
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.ff5")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 8762573139131497865L;

            public void actionPerformed(ActionEvent e) {
                if (currentItem == null) {
                    return;
                }
                int sec = timePanel.getSeconds();
                if (sec >= 5) {
                    setTime((sec - 5) * 1000);
                } else {
                    setTime(0);
                }
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.ss5")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = -4858165000349764496L;

            public void actionPerformed(ActionEvent e) {
                if (currentItem == null) {
                    return;
                }
                int sec = timePanel.getSeconds();
                if (sec + 5 < currentItem.getLength()) {
                    setTime((sec + 5) * 1000);
                }
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.pre")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 2895707073837592432L;

            public void actionPerformed(ActionEvent e) {
                previousSong();
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.next")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 992961818897082654L;

            public void actionPerformed(ActionEvent e) {
                nextSong();
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.playFile")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = -4875808544917645300L;

            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = Util.getFileChooser(new FileNameFilter(Config.EXTS,
                        Config.getResource("playlist.filechooser.name"), true), JFileChooser.FILES_ONLY);
                int i = jf.showOpenDialog(config.getPlWindow());
                if (i == JFileChooser.APPROVE_OPTION) {
                    File f = jf.getSelectedFile();
                    PlayListItem item = new PlayListItem(Util.getSongName(f), f.getPath(), -1, true);
                    playlist.removeAllItems();
                    playlist.appendItem(item);
                    playlistUI.repaint();
                    playerState = PLAY;
                    setCurrentSong(item);
                }
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.playURL")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 7012248393694962314L;

            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(config.getTopParent(),
                        Config.getResource("playlist.add.inputurl"));
                if (s != null) {
                    if (Config.startWithProtocol(s)) {
                        PlayListItem item = new PlayListItem(s, s, -1, false);
                        playlist.removeAllItems();
                        playlist.appendItem(item);
                        playlistUI.repaint();
                        playerState = PLAY;
                        setCurrentSong(item);
                    } else {
                        JOptionPane.showMessageDialog(config.getPlWindow(),
                                Config.getResource("playlist.add.invalidUrl"));
                    }
                }
            }
        });
        return menu;
    }

    private JMenu createVolumeMenu() {
        JMenu menu = new JMenu(Config.getResource("PlayerUI.volumeControl"));
        menu.add(new AbstractAction(Config.getResource("PlayerUI.increase")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = -6479449161176201748L;

            public void actionPerformed(ActionEvent e) {
                int value = volume.getValue();
                volume.setValue(value + 10);
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.decrease")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = -3919727683499256477L;

            public void actionPerformed(ActionEvent e) {
                int value = volume.getValue();
                volume.setValue(value - 10);
            }
        });
        menu.addSeparator();
        JCheckBoxMenuItem mute = new JCheckBoxMenuItem(Config.getResource("PlayerUI.mute"), config.isMute());
        mute.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                JCheckBoxMenuItem check = (JCheckBoxMenuItem) ae.getSource();
                config.setMute(check.isSelected());
                changeSpeakerState();
            }
        });
        menu.add(mute);
        return menu;
    }

    private void changeSpeakerState() {

        if (config.isMute()) {
            speaker.setActionCommand(Config.VOL_ON);
            speaker.setSelected(true);
            try {
                player.setGain(0);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(PlayerUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            speaker.setActionCommand(Config.VOL_OFF);
            speaker.setSelected(false);
            int gainValue = volume.getValue();
            int maxGain = volume.getMaximum();
            if (gainValue == 0) {
                try {
                    player.setGain(0);
                } catch (BasicPlayerException ex) {
                }
            } else {
                try {
                    player.setGain((double) gainValue / (double) maxGain);
                } catch (BasicPlayerException ex) {
                }
            }
        }
    }

    private JMenu createPlayModeMenu() {
        JMenu menu = new JMenu(Config.getResource("playlist.mode"));
        ButtonGroup bg1 = new ButtonGroup();
        ButtonGroup bg2 = new ButtonGroup();
        //不循环
        JRadioButtonMenuItem noCircle = new JRadioButtonMenuItem(Config.getResource("playlist.mode.noCircle"));
        noCircle.setSelected(!config.isRepeatEnabled());
        menu.add(noCircle).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                config.setRepeatEnabled(false);
            }
        });
        //单曲循环
        JRadioButtonMenuItem singleCircle = new JRadioButtonMenuItem(Config.getResource("playlist.mode.singleCircle"));
        singleCircle.setSelected(config.isRepeatEnabled() && config.getRepeatStrategy() == Config.REPEAT_ONE);
        menu.add(singleCircle).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                config.setRepeatEnabled(true);
                config.setRepeatStrategy(Config.REPEAT_ONE);
            }
        });
        //整体循环
        JRadioButtonMenuItem allCircle = new JRadioButtonMenuItem(Config.getResource("playlist.mode.allCircle"));
        allCircle.setSelected(config.isRepeatEnabled() && config.getRepeatStrategy() == Config.REPEAT_ALL);
        menu.add(allCircle).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                config.setRepeatEnabled(true);
                config.setRepeatStrategy(Config.REPEAT_ALL);
            }
        });
        menu.addSeparator();
        //顺序播放
        JRadioButtonMenuItem orderPlay = new JRadioButtonMenuItem(Config.getResource("playlist.mode.orderPlay"));
        orderPlay.setSelected(config.getPlayStrategy() == Config.ORDER_PLAY);
        menu.add(orderPlay).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                config.setPlayStrategy(Config.ORDER_PLAY);
            }
        });
        //随机播放
        JRadioButtonMenuItem randomPlay = new JRadioButtonMenuItem(Config.getResource("playlist.mode.randomPlay"));
        randomPlay.setSelected(config.getPlayStrategy() == Config.RANDOM_PLAY);
        menu.add(randomPlay).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                config.setPlayStrategy(Config.RANDOM_PLAY);
            }
        });
        bg1.add(noCircle);
        bg1.add(singleCircle);
        bg1.add(allCircle);
        bg2.add(orderPlay);
        bg2.add(randomPlay);
        return menu;
    }

    private JMenu createPlayWhichMenu() {
        JMenu menu = new JMenu(Config.getResource("PlayerUI.playSong"));
        for (final PlayListItem item : playlist.getAllItems()) {
            menu.add(new AbstractAction(item.getFormattedName()) {

                /**
                 * fields:<br>
                 * serialVersionUID
                 */
                private static final long serialVersionUID = -2534811488786680582L;

                public void actionPerformed(ActionEvent e) {
                    playerState = PLAY;
                    setCurrentSong(item);
                }
            });
        }
        return menu;
    }

    private JMenu createAudioChartMenu() {
        JMenu menu = new JMenu(Config.getResource("PlayerUI.audioChart"));
        menu.add(new AbstractAction(Config.getResource("PlayerUI.analyzing")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 8445245812733330802L;

            public void actionPerformed(ActionEvent e) {
                audioChart.setDisplayMode(AudioChart.DISPLAY_MODE_SPECTRUM_ANALYSER);
                config.setAudioChartDisplayMode(AudioChart.DISPLAY_MODE_SPECTRUM_ANALYSER);
                audioChart.repaint();
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.line")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 4104747428427594614L;

            public void actionPerformed(ActionEvent e) {
                audioChart.setDisplayMode(AudioChart.DISPLAY_MODE_SCOPE);
                config.setAudioChartDisplayMode(AudioChart.DISPLAY_MODE_SCOPE);
                audioChart.repaint();
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.noshow")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = -3333411235606841579L;

            public void actionPerformed(ActionEvent e) {
                audioChart.setDisplayMode(AudioChart.DISPLAY_MODE_OFF);
                config.setAudioChartDisplayMode(AudioChart.DISPLAY_MODE_OFF);
                audioChart.repaint();
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.optional")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = -7946854989500268559L;

            public void actionPerformed(ActionEvent e) {
                OptionDialog jd = config.getOptionDialog();
                jd.setSelected(Config.getResource("PlayerUI.view"));
                jd.setVisible(true);
            }
        });
        return menu;
    }

    private JMenu createLyricMenu() {
        JMenu menu = new JMenu(Config.getResource("PlayerUI.lrc"));
        Util.generateLyricMenu(menu, lyricUI.getLyricPanel());
        return menu;
    }

    private JMenu createEqMenu() {
        JMenu menu = new JMenu(Config.getResource("PlayerUI.eq"));
        menu.add(new AbstractAction(Config.getResource("PlayerUI.autoConfig")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = -7017178850020435665L;

            public void actionPerformed(ActionEvent e) {
                equalizerUI.getAutoButton().doClick();
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.onoff")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 7071631271932008787L;

            public void actionPerformed(ActionEvent e) {
                equalizerUI.getOnOffButton().doClick();
            }
        });
        menu.addSeparator();
        JMenu selects = new JMenu(Config.getResource("PlayerUI.selectableTypes"));
        for (String s : EqualizerUI.presets) {
            JMenuItem item = new JMenuItem(Config.getResource(s));
            item.addActionListener(equalizerUI);
            item.setActionCommand(s);
            selects.add(item);
        }
        menu.add(selects);
        return menu;
    }

    private JMenu createViewMenu() {
        JMenu menu = new JMenu(Config.getResource("PlayerUI.viewType"));
        menu.add(new AbstractAction(Config.getResource("PlayerUI.lrcWindow")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 8935760968071943254L;

            public void actionPerformed(ActionEvent e) {
                loader.toggleLyricWindow(!config.getLrcWindow().isVisible());
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.eqWindow")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 4510625309583976007L;

            public void actionPerformed(ActionEvent e) {
                loader.toggleEqualizer(!config.getEqWindow().isVisible());
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.plWindow")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 8489631967072190849L;

            public void actionPerformed(ActionEvent e) {
                loader.togglePlaylist(!config.getPlWindow().isVisible());
            }
        });
        menu.addSeparator();
        menu.add(new AbstractAction(Config.getResource("PlayerUI.fileProperty")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 1782636756517917447L;

            public void actionPerformed(ActionEvent e) {
                if (currentItem != null) {
                    SongInfoDialog info = new SongInfoDialog(config.getTopParent(), true, currentItem);
                    info.setVisible(true);
                }
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.reOrder")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 8195914901755982639L;

            public void actionPerformed(ActionEvent e) {
                loader.reRange();
            }
        });
        menu.add(new AbstractAction(Config.getResource("PlayerUI.alwaysOnTop")) {

            /**
             * fields:<br>
             * serialVersionUID
             */
            private static final long serialVersionUID = 1337383606150443122L;

            public void actionPerformed(ActionEvent e) {
            }
        });
        return menu;
    }

    public PopupMenu createPopupMenu() {
        //PopupMenu pm;

        return null;
    }

    /**
     * 设置按钮的位置,不管这个按钮多大
     *
     * @param ab 按钮
     * @param x 中心点的X
     * @param y 中心点的Y
     */
    private void setButtonLocation(AbstractButton ab, int x, int y) {
        int width = ab.getIcon().getIconWidth();
        int height = ab.getIcon().getIconHeight();
        ab.setBounds(x - width / 2, y - height / 2, width, height);
        this.add(ab);
    }

    /**
     * 加载界面
     *
     * @param l
     * @param c
     */
    public void loadUI(Loader l, Config c) {
        this.loader = l;
        this.config = c;
        ImageBorder border = new ImageBorder();
        Image bg = Util.getImage("player/main.png");
        border.setImage(bg);
        this.setBorder(border);
        playlistUI = new PlayListUI();
        playlistUI.setPlayerUI(this);
        lyricUI = new LyricUI();
        lyricUI.setPlayer(this);
        equalizerUI = new EqualizerUI();
        equalizerUI.setPlayer(this);
        equalizerUI.loadUI();
        initUI();
        addMouseVolumeListener();
        loader.loaded();
        thread = new Thread() {

            @Override
            public void run() {
                int index = 0;
                while (true) {
                    if (scrollTitle && config.isShowTitleInTaskBar()) {
                        if (index > title.length() - 1) {
                            index = 0;
                        }
                        String temp = title.substring(index) + title.substring(0, index);
                        loader.setTitle(temp);
                        index++;
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException ex) {
                        }
                    } else {
                        loader.setTitle(title);
                        synchronized (lock) {
                            try {
                                lock.wait();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(PlayerUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                }
            }
        };
        thread.start();
    }

    public void setShowTile(boolean b) {
        if (b) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    public LyricUI getLyricUI() {
        return lyricUI;
    }

    public EqualizerUI getEqualizerUI() {
        return equalizerUI;
    }

    public PlayListUI getPlaylistUI() {
        return playlistUI;
    }

    public synchronized void processStateUpdated(BasicPlayerEvent event) {
        log.log(Level.FINE, "{0}\t,Time:{1}\t,Thread:{2}", new Object[]{event.toString(), System.nanoTime(), Thread.currentThread()});
        /*-- End Of Media reached --*/
        int eventState = event.getCode();
        Object obj = event.getDescription();
        if (eventState == BasicPlayerEvent.EOM) {
            title = Config.NAME;
            if ((playerState == PAUSE) || (playerState == PLAY)) {
                //如果启用了重复的策略，则分两种，一是单曲重复，一是整体重复
                if (config.isRepeatEnabled()) {
                    if (config.getRepeatStrategy() == Config.REPEAT_ALL) {
                        //如果设了连续播放的时候间隔，则要睡这么久才能播下一首
                        try {
                            Thread.sleep(config.getSequencePlayInterval() * 1000);
                            this.nextSong();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(PlayerUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (config.getRepeatStrategy() == Config.REPEAT_ONE) {
                        this.play();
                    }
                }

            }
        } else if (eventState == BasicPlayerEvent.PLAYING) {
            int sec = (int) currentItem.getLength();
            if (sec <= 0) {
                Long duration = (Long) audioInfo.get("duration");
                if (duration != null) {//此处有必要修改ITEM的时间吗?初始化的时候是不是得到的不准确?
                    sec = (int) (duration / 1000000);
                    currentItem.setDuration(sec);
                }
            }
            timePanel.reset(sec);
            infoPanel.reset(currentItem);
            lastScrollTime = System.currentTimeMillis();
            posValueJump = false;
            if (audioInfo.containsKey("basicplayer.sourcedataline")) {
                if (audioChart != null) {
                    audioChart.setupDSP((SourceDataLine) audioInfo.get("basicplayer.sourcedataline"));
                    audioChart.startDSP((SourceDataLine) audioInfo.get("basicplayer.sourcedataline"));
                }
            }
        } else if (eventState == BasicPlayerEvent.SEEKING) {
            posValueJump = true;
        } else if (eventState == BasicPlayerEvent.SEEKED) {
            applyPanAndGain();
        } else if (eventState == BasicPlayerEvent.OPENING) {
            if ((obj instanceof URL) || (obj instanceof InputStream)) {
                showTitle(Config.getResource("title.buffering"));
            }
        } else if (eventState == BasicPlayerEvent.STOPPED) {
            if (audioChart != null) {
                audioChart.stopDSP();
                audioChart.repaint();
            }
        }
        changeStateTitle(eventState);
    }

    /**
     * 根据当前的状态改变标题的状态改变
     *
     * @param state
     */
    private void changeStateTitle(int eventState) {
        switch (eventState) {
            case BasicPlayerEvent.EOM:
            case BasicPlayerEvent.STOPPED:
                showTitle(Config.getResource("state.stop"));
                currentState = Config.getResource("state.stop");
                scrollTitle = false;
                if (currentItem != null) {
                    title = currentItem.getFormattedName() + " - " + Config.NAME + "  ";
                }
                //log.log(Level.FINE, "停止:" + System.nanoTime() + "Thread:" + Thread.currentThread());
                log.log(Level.FINE, "\u505c\u6b62:{0}Thread:{1}", new Object[]{System.nanoTime(), Thread.currentThread()});
                break;
            case BasicPlayerEvent.PLAYING:
            case BasicPlayerEvent.RESUMED:
                showTitle(Config.getResource("state.play"));
                currentState = Config.getResource("state.play");
                title = currentItem.getFormattedName() + " - " + Config.NAME + "  ";
                scrollTitle = true;
                //log.log(Level.FINE, "播放:" + System.nanoTime() + "Thread:" + Thread.currentThread());
                log.log(Level.FINE, "\u64ad\u653e:{0}Thread:{1}", new Object[]{System.nanoTime(), Thread.currentThread()});
                synchronized (lock) {
                    lock.notifyAll();
                }
                break;
            case BasicPlayerEvent.PAUSED:
                showTitle(Config.getResource("state.pause"));
                currentState = Config.getResource("state.pause");
                title = currentItem.getFormattedName() + " - " + Config.NAME + "  ";
                scrollTitle = false;
                //log.log(Level.FINE, "暂停:" + System.nanoTime() + "Thread:" + Thread.currentThread());
                log.log(Level.FINE, "\u6682\u505c:{0}Thread:{1}", new Object[]{System.nanoTime(), Thread.currentThread()});
                break;
        }
    }

    private void showTitle(String title) {
        //可能是设置提示的标题 ，比如正在缓冲，正在连接什么的
        if (state != null) {
            state.setText(title);
        }
    }

    public long getTimeLengthEstimation(Map<?, ?> properties) {
        long milliseconds = -1;
        int byteslength = -1;
        if (properties != null) {
            if (properties.containsKey("audio.length.bytes")) {
                //byteslength = ((Integer) properties.get("audio.length.bytes")).intValue();//不必要的拆箱
                byteslength = ((Integer) properties.get("audio.length.bytes"));
            }
            if (properties.containsKey("duration")) {
                //milliseconds = (int) (((Long) properties.get("duration")).longValue()) / 1000;
                milliseconds = (((Long) properties.get("duration"))) / 1000;
            } else {
                // Try to compute duration
                int bitspersample = -1;
                int channels = -1;
                float samplerate = -1.0f;
                int framesize = -1;
                if (properties.containsKey("audio.samplesize.bits")) {
                    //bitspersample = ((Integer) properties.get("audio.samplesize.bits")).intValue();
                    bitspersample = ((Integer) properties.get("audio.samplesize.bits"));
                }
                if (properties.containsKey("audio.channels")) {
                    //channels = ((Integer) properties.get("audio.channels")).intValue();
                    channels = ((Integer) properties.get("audio.channels"));
                }
                if (properties.containsKey("audio.samplerate.hz")) {
                    //samplerate = ((Float) properties.get("audio.samplerate.hz")).floatValue();
                    samplerate = ((Float) properties.get("audio.samplerate.hz"));
                }
                if (properties.containsKey("audio.framesize.bytes")) {
                    //framesize = ((Integer) properties.get("audio.framesize.bytes")).intValue();
                    framesize = ((Integer) properties.get("audio.framesize.bytes"));
                }
                if (bitspersample > 0) {
                    milliseconds = (int) (1000.0f * byteslength / (samplerate * channels * (bitspersample / 8)));
                } else {
                    milliseconds = (int) (1000.0f * byteslength / (samplerate * framesize));
                }
            }
        }
        return milliseconds;
    }

    public void processProgress(int bytesread, long microseconds, byte[] pcmdata, Map<?, ?> properties) {
        int byteslength = -1;
        long total = -1;
        // Try to get time from playlist item.
        if (currentItem != null) {
            total = currentItem.getLength();
        }
        // If it fails then try again with JavaSound SPI.
        if (total <= 0) {
            total = (long) Math.round(getTimeLengthEstimation(audioInfo) / 1000);
            currentItem.setDuration(total);
            playlistUI.repaint();
        }
        // If it fails again then it might be stream => Total = -1
        if (total <= 0) {
            total = -1;
        }
        if (audioInfo.containsKey("basicplayer.sourcedataline")) {
            // Spectrum/time analyzer
            if (audioChart != null) {
                audioChart.writeDSP(pcmdata);
            }
        }
        if (audioInfo.containsKey("audio.length.bytes")) {
            //byteslength = ((Integer) audioInfo.get("audio.length.bytes")).intValue();
            byteslength = ((Integer) audioInfo.get("audio.length.bytes"));
        }
        float progress = -1.0f;
        if ((bytesread > 0) && ((byteslength > 0))) {
            progress = bytesread * 1.0f / byteslength * 1.0f;
            config.setLastRate(progress);
        }

        if (audioInfo.containsKey("audio.type")) {
            String audioformat = (String) audioInfo.get("audio.type");
            if (audioformat.equalsIgnoreCase("mp3")) {
                //if (properties.containsKey("mp3.position.microseconds")) secondsAmount = (long) Math.round(((Long) properties.get("mp3.position.microseconds")).longValue()/1000000);
                // Shoutcast stream title.
                if (properties.containsKey("mp3.shoutcast.metadata.StreamTitle")) {
                    String shoutTitle = ((String) properties.get("mp3.shoutcast.metadata.StreamTitle")).trim();
                    if (shoutTitle.length() > 0) {
                        if (currentItem != null) {
                            String sTitle = " (" + currentItem.getFormattedDisplayName() + ")";
                            if (!currentItem.getFormattedName().equals(shoutTitle + sTitle)) {
                                currentItem.setFormattedDisplayName(shoutTitle + sTitle);
//                                showTitle((shoutTitle + sTitle).toUpperCase());
//                                playlistUI.paintList();
                            }
                        }
                    }
                }
                // EqualizerUI
                if (properties.containsKey("mp3.equalizer")) {
                    equalizerUI.setBands((float[]) properties.get("mp3.equalizer"));
                }
                if (total > 0) {
                    secondsAmount = (long) (total * progress);
                } else {
                    secondsAmount = -1;
                }
            } else if (audioformat.equalsIgnoreCase("wave")) {
                secondsAmount = (long) (total * progress);
            } else {
                secondsAmount = (long) Math.round(microseconds / 1000000);
                equalizerUI.setBands(null);
            }
        } else {
            secondsAmount = (long) Math.round(microseconds / 1000000);
            equalizerUI.setBands(null);
        }
        if (secondsAmount < 0) {
            secondsAmount = (long) Math.round(microseconds / 1000000);
        }
//        //如果歌曲里面有位置的信息,则设置歌词
//        if (properties.containsKey("mp3.position.microseconds")) {
//            int mill = (int) ((Long) properties.get("mp3.position.microseconds") / 1000);
//            if (lyric != null) {
//                lyric.setTime(mill);
//            }
//        } else {//如果不是MP3的格式，则歌词的显示就没有那么精确了，但是还需要滚动
//            if (lyric != null) {
////                lyric.setTime((secondsAmount * 1000));
//                lyric.setTime(microseconds / 1000);
//            }
//        }
        /*-- Display elapsed time --*/
        int seconds = (int) secondsAmount;
        timePanel.setTime(seconds);
        // Update PosBar location.
        if (total != 0) {
            if (posValueJump == false) {
                int pValue = (Math.round(secondsAmount * Config.POSBARMAX / total));
                pos.setValue(pValue);
            }
        } else {
            pos.setValue(0);
        }
        //long ctime = System.currentTimeMillis();
        //long lctime = lastScrollTime;
        // Scroll title ?//这里可能是显示歌曲信息如果显示不下时的换行做法
//        if ((titleScrollLabel != null) && (titleScrollLabel.length > 0)) {
//            if (ctime - lctime > SCROLL_PERIOD) {
//                lastScrollTime = ctime;
//                if (scrollRight == true) {
//                    scrollIndex++;
//                    if (scrollIndex >= titleScrollLabel.length) {
//                        scrollIndex--;
//                        scrollRight = false;
//                    }
//                } else {
//                    scrollIndex--;
//                    if (scrollIndex <= 0) {
//                        scrollRight = true;
//                    }
//                }
//                // TODO : Improve
//                skin.getAcTitleLabel().setAcText(titleScrollLabel[scrollIndex]);
//            }
//        }
    }

    public void setLyric(Lyric ly) {
        this.lyric = ly;
        if (currentItem != null) {
            currentItem.setLyricFile(ly.getLyricFile());
        }
    }

    public void setTime(long time) {
        if (time < 0) {
            return;
        }
        if (currentItem != null) {
            long length = currentItem.getLength() * 1000;
            if (length < 0) {
                return;
            }
            double rate = time * 1.0 / length;
            processSeek(rate);
        }
    }

    public void saveConfig() {
    }

    public void readConfig() {
    }

    public void setShowLyric(boolean b) {
        lrc.setSelected(b);
        lrc.setActionCommand(b ? Config.LRC_OFF : Config.LRC_ON);
        loader.toggleLyricWindow(b);
    }

    public JFrame getTopParent() {
        return config.getTopParent();
    }

    public void play() {
        processPlay(0);
    }

    public void pause() {
        processPause(0);
    }

    public void stop() {
        processStop(0);
    }

    public void nextSong() {
        processNext(0);
    }

    public void previousSong() {
        processPrevious(0);
    }

    public void actionPerformed(ActionEvent e) {

        final ActionEvent evt = e;
        if (e.getActionCommand().equals(Config.PAUSE)) {
            processActionEvent(e);
        } else if ((e.getActionCommand().equals(Config.PLAY)) && (playerState == PAUSE)) {
            processActionEvent(e);
        } else if (e.getActionCommand().equals(Config.CLOSE)) {
            closePlayer();
        } else if (e.getActionCommand().equals(Config.MINIMIZE)) {
            loader.minimize();
        } else if (e.getActionCommand().equals(Config.SETTING)) {
            OptionDialog jd = new OptionDialog(config.getTopParent(), true);
            jd.setTitle("关于");
            jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            jd.setVisible(true);
        } else { //其它的都是比较耗时的操作,所以新起一个线程去做
            new Thread("PlayerUIActionEvent") {

                @Override
                public void run() {
                    processActionEvent(evt);
                }
            }.start();
        }
    }

    public void processStop(int modifiers) {
        if ((playerState == PAUSE) || (playerState == PLAY)) {
            try {
                player.stop();
            } catch (BasicPlayerException e) {
            }
            playerState = STOP;
            secondsAmount = 0;
            pos.setValue(0);
            timePanel.reset();
            //这里清空一些东西,比如时间归0,播放按钮变成一个可播放的标志,而不是暂停的标志
//            skin.getAcPlayIcon().setIcon(2);
//            skin.getAcTimeIcon().setIcon(1);
            pos.setEnabled(false);
            changePlayPauseState(playerState);
        }
    }

    /**
     * 这里处理关闭播放器的方法, 关闭的时候要保存目录的所有配置,然后再释放资源
     */
    protected void closePlayer() {
        log.log(Level.INFO, "调用了closePlayer");
        if ((playerState == PAUSE) || (playerState == PLAY)) {
            try {
                if (player != null) {
                    player.stop();
                }
            } catch (BasicPlayerException e) {
            }
        }
        config.setGainValue(volume.getValue());
        config.setPanValue(pan.getValue());
        loader.close();
    }

    public PlayListItem getCurrentItem() {
        return currentItem;
    }

    public void setPlayerState(int state) {
        this.playerState = state;
    }

    public void setCurrentSong(PlayListItem pli) {
        seekedTime = 0L;
        //log.log(Level.INFO, "调用了setCurrentSong()" + Thread.currentThread());
        log.log(Level.INFO, "\u8c03\u7528\u4e86setCurrentSong(){0}", Thread.currentThread());
        if (config.getReadTagInfoStrategy().equals(Config.READ_WHEN_PLAY)) {
            pli.getTagInfo();
        }
        currentItem = pli;
        playlistUI.setCurrentItem(pli);
        int playerStateMem = playerState;
        if ((playerState == PAUSE) || (playerState == PLAY)) {
            try {
                player.stop();
            } catch (BasicPlayerException e) {
            }
            playerState = STOP;
            secondsAmount = 0;
            timePanel.reset();
            // Display play/time icons.
            //把播放和时间的图标变成最初的状态
//            skin.getAcPlayIcon().setIcon(2);
//            skin.getAcTimeIcon().setIcon(0);
        }
        playerState = OPEN;
        if (pli != null) {
            // Read tag info.
            pli.getTagInfo();
            infoPanel.repaint();
            currentSongName = pli.getFormattedName();
            currentFileOrURL = pli.getLocation();
            currentIsFile = pli.isFile();
        } // Playlist ended.
        else {
            // Try to repeat ?
            if (config.isRepeatEnabled()) {
                if (playlist != null) {
                    // PlaylistItems available ?
                    if (playlist.getPlaylistSize() > 0) {
                        playlist.begin();
                        PlayListItem rpli = playlist.getCursor();
                        if (rpli != null) {
                            // OK, Repeat the playlist.
                            rpli.getTagInfo();
                            currentSongName = rpli.getFormattedName();
                            currentFileOrURL = rpli.getLocation();
                            currentIsFile = rpli.isFile();
                            currentItem = rpli;
                        }
                    } // No, so display Title.
                    else {
                        currentSongName = Config.TITLETEXT;
                        currentFileOrURL = null;
                        currentIsFile = false;
                        currentItem = null;
                    }
                }
            } // No, so display Title.
            else {
                currentSongName = Config.TITLETEXT;
                currentFileOrURL = null;
                currentIsFile = false;
                currentItem = null;
            }
        }
        if (currentIsFile == true) {
            pos.setEnabled(true);
            pos.setHideThumb(false);
        } else {
            pos.setValue(0);
            pos.setEnabled(false);
            pos.setHideThumb(true);
        }
        config.setCurrentFileOrUrl(currentFileOrURL);
        //这里可能是要显示歌名以及此歌曲的一些信息
        titleText = currentSongName.toUpperCase();
        showMessage(titleText);
        playlist.setItemSelected(pli, playlist.getSelectedIndex());
        playlistUI.repaint();
        // Start playing if needed.
        if ((playerStateMem == PLAY) || (playerStateMem == PAUSE)) {
            processPlay(MouseEvent.BUTTON1_MASK);

        }

    }

    /**
     * 在这里显示一些常用的东西 比如改变标题,以及时间清0,还有,歌曲信息的改变 比如比特率,单双声道等全部重置,因为一首新的歌曲要开始了
     * 即使不开始,在不播的时候清除这些也是应该的
     *
     * @param titleText
     */
    public void showMessage(String titleText) {
    }

    /**
     * 处理播放的请求,这下就要分很多种情况了,哈哈
     *
     * @param modifiers
     */
    protected void processPlay(int modifiers) {
        //先检查播放列表有没有改变,改变了就要检查一下了
        //因为下一个下标或者当前的下标并不一定有效了,有可能
        //删除了一些列表项
        log.log(Level.INFO, "processPlay....... ");
        // playlist has been modified since we were last there, must update our cursor pos etc.
        if (playlist.isModified()) {
            PlayListItem pli = playlist.getCursor();
            //log.log(Level.INFO, "播放列表改了..." + pli);
            log.log(Level.INFO, "\u64ad\u653e\u5217\u8868\u6539\u4e86...{0}", pli);
            //如果当前的列表项已经为空了,则从头开始播了
            if (pli == null) {
                playlist.begin();
                pli = playlist.getCursor();
            }
            setCurrentSong(pli);
            playlist.setModified(false);
            playlistUI.repaint();
        }
        //如果当前的状态是暂停,则马上恢复
        if (playerState == PAUSE) {
            try {
                player.resume();
            } catch (BasicPlayerException e) {
            }
            playerState = PLAY;
            //更改播放的图标,让暂停的图标变成正在播放的图标
            //时间的图标也要变一下
//            skin.getAcPlayIcon().setIcon(0);
//            skin.getAcTimeIcon().setIcon(0);
        } //如果正在播放的话,则先停止它,再从头播放,这样更合普通使用习惯
        else if (playerState == PLAY) {
            try {
                player.stop();
            } catch (BasicPlayerException e) {
            }
            playerState = PLAY;
            secondsAmount = 0;
            timePanel.reset();
            //重置当前显示时间
//            skin.getAcMinuteH().setAcText("0");
//            skin.getAcMinuteL().setAcText("0");
//            skin.getAcSecondH().setAcText("0");
//            skin.getAcSecondL().setAcText("0");
            if (currentFileOrURL != null) {
                try {

                    if (currentIsFile == true) {
                        player.open(new File(currentFileOrURL));
                    } else {
                        player.open(new URL(currentFileOrURL));
                    }
                    player.play();
                } catch (BasicPlayerException ex) {
                    showMessage(Config.getResource("title.invalidfile"));
                } catch (MalformedURLException ex) {
                    showMessage(Config.getResource("title.invalidfile"));
                }
            }//如果状态是停止或者状态是已经打开文件了,则先停止,再播放
            //在这种播放的状态下,需要重新读取歌曲文件的一些信息,比如比特率,是否单双声道等等
        } else if ((playerState == STOP) || (playerState == OPEN)) {
            try {
                player.stop();
            } catch (BasicPlayerException e) {
            }
            if (currentFileOrURL != null) {
                try {
                    if (currentIsFile == true) {
                        player.open(new File(currentFileOrURL));
                    } else {
                        player.open(new URL(currentFileOrURL));
                    }
                    player.play();
                    lyric = new Lyric(currentItem);
                    lyricUI.setLyric(lyric);
                    titleText = currentSongName.toUpperCase();
                    // Get bitrate, samplingrate, channels, time in the following order :
                    // PlaylistItem, BasicPlayer (JavaSound SPI), Manual computation.
                    int bitRate = -1;
                    if (currentItem != null) {
                        bitRate = currentItem.getBitrate();
                    }
                    if ((bitRate <= 0) && (audioInfo.containsKey("bitrate"))) {
                        //bitRate = ((Integer) audioInfo.get("bitrate")).intValue();
                        bitRate = ((Integer) audioInfo.get("bitrate"));
                    }
                    if ((bitRate <= 0) && (audioInfo.containsKey("audio.framerate.fps")) && (audioInfo.containsKey("audio.framesize.bytes"))) {
                        //float FR = ((Float) audioInfo.get("audio.framerate.fps")).floatValue();
                        //int FS = ((Integer) audioInfo.get("audio.framesize.bytes")).intValue();
                        float FR = ((Float) audioInfo.get("audio.framerate.fps"));
                        int FS = ((Integer) audioInfo.get("audio.framesize.bytes"));
                        bitRate = Math.round(FS * FR * 8);
                    }
                    int channels = -1;
                    if (currentItem != null) {
                        channels = currentItem.getChannels();
                    }
                    if ((channels <= 0) && (audioInfo.containsKey("audio.channels"))) {
                        channels = ((Integer) audioInfo.get("audio.channels"));
                    }
                    float sampleRate = -1.0f;
                    if (currentItem != null) {
                        sampleRate = currentItem.getSamplerate();
                    }
                    if ((sampleRate <= 0) && (audioInfo.containsKey("audio.samplerate.hz"))) {
                        sampleRate = ((Float) audioInfo.get("audio.samplerate.hz"));
                    }
                    long lenghtInSecond = -1L;
                    if (currentItem != null) {
                        lenghtInSecond = currentItem.getLength();
                    }
                    if ((lenghtInSecond <= 0) && (audioInfo.containsKey("duration"))) {
                        lenghtInSecond = ((Long) audioInfo.get("duration")) / 1000000;
                    }
                    if ((lenghtInSecond <= 0) && (audioInfo.containsKey("audio.length.bytes"))) {
                        // Try to compute time length.
                        lenghtInSecond = (long) Math.round(getTimeLengthEstimation(audioInfo) / 1000);
                        if (lenghtInSecond > 0) {
                            int minutes = (int) Math.floor(lenghtInSecond / 60);
                            int hours = (int) Math.floor(minutes / 60);
                            minutes = minutes - hours * 60;
                            int seconds = (int) (lenghtInSecond - minutes * 60 - hours * 3600);
                            if (seconds >= 10) {
                                titleText = "(" + minutes + ":" + seconds + ") " + titleText;
                            } else {
                                titleText = "(" + minutes + ":0" + seconds + ") " + titleText;
                            }
                        }
                    }
                    bitRate = Math.round((bitRate / 1000));
                    currentItem.setSampled(String.valueOf(Math.round((sampleRate / 1000))) + "kHz");
                    if (bitRate > 999) {
                        bitRate = (bitRate / 100);
                        currentItem.setBitRate(bitRate + "Hkbps");
                    } else {
                        currentItem.setBitRate(String.valueOf(bitRate) + "kbps");
                    }
                    if (channels == 2) {
                        currentItem.setChannels(Config.getResource("songinfo.channel.stereo"));
                    } else if (channels == 1) {
                        currentItem.setChannels(Config.getResource("songinfo.channel.mono"));
                    }
//                    showTitle(titleText);
//                    skin.getAcMinuteH().setAcText("0");
//                    skin.getAcMinuteL().setAcText("0");
//                    skin.getAcSecondH().setAcText("0");
//                    skin.getAcSecondL().setAcText("0");
                    //把播放按钮的图片变成可以按暂停
//                    skin.getAcPlayIcon().setIcon(0);
//                    skin.getAcTimeIcon().setIcon(0);
                } catch (BasicPlayerException bpe) {
                    showMessage(Config.getResource("title.invalidfile"));
                } catch (MalformedURLException mue) {
                    showMessage(Config.getResource("title.invalidfile"));
                }
                // Set pan/gain.
                //因为是重新读取的,所以要设置声音和声道
                applyPanAndGain();
                playerState = PLAY;
            }
        }
        changePlayPauseState(playerState);
        pos.setEnabled(true);
        if (!isSeeked && config.isAutoPlayWhenStart() && config.isMaintainLastPlay()) {
            isSeeked = true;
            processSeek(lastRate);
        }
    }

    /**
     * 应用当前的声道位置和声音设置 无论是拖动进度还是重新开始,都要应用一遍 2008.8.2更改
     */
    private void applyPanAndGain() {
        try {
            //要看是不是静音,静听就不用设声音了
            if (config.isMute()) {
                player.setGain(0);
            } else {
                player.setGain(((double) volume.getValue() / (double) volume.getMaximum()));
            }
            float balanceValue = pan.getValue() * 1.0f / Config.BALANCEMAX;
            player.setPan(balanceValue);
        } catch (BasicPlayerException e) {
        }
    }

    /**
     * 根据播放或者暂停来改变那个按钮的图标
     *
     * @param state 状态
     */
    private void changePlayPauseState(int state) {
        if (state == PLAY) {
            play.setActionCommand(Config.PAUSE);
            play.setIcon(new ImageIcon(pauseImgs[0]));
            play.setRolloverIcon(new ImageIcon(pauseImgs[1]));
            play.setPressedIcon(new ImageIcon(pauseImgs[2]));
        } else if (state == PAUSE || state == STOP) {
            play.setActionCommand(Config.PLAY);
            play.setIcon(new ImageIcon(playImgs[0]));
            play.setRolloverIcon(new ImageIcon(playImgs[1]));
            play.setPressedIcon(new ImageIcon(playImgs[2]));
        }
    }

    public void processPause(int modifiers) {
        if (playerState == PLAY) {
            try {
                player.pause();
            } catch (BasicPlayerException e) {
            }
            playerState = PAUSE;
            changePlayPauseState(playerState);
            //改变播放按钮的图片,让它显示可播放
//            skin.getAcPlayIcon().setIcon(1);
//            skin.getAcTimeIcon().setIcon(1);
        } else if (playerState == PAUSE) {
            try {
                player.resume();
            } catch (BasicPlayerException e) {
            }
            playerState = PLAY;
            changePlayPauseState(playerState);
            //改变播放按钮的图片,让它显示可播放
//            skin.getAcPlayIcon().setIcon(0);
//            skin.getAcTimeIcon().setIcon(0);
        }
    }

    public void processNext(int modifiers) {
        // Try to get next song from the playlist
        playlist.nextCursor();
        //这里是只要重绘还是还要显式地调用nextCursor()?
        playlistUI.repaint();
        PlayListItem pli = playlist.getCursor();
        setCurrentSong(pli);
    }

    public void processPrevious(int modifiers) {
        // Try to get previous song from the playlist
        playlist.previousCursor();
        playlistUI.repaint();
        PlayListItem pli = playlist.getCursor();
        setCurrentSong(pli);
    }

    public void processActionEvent(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equalsIgnoreCase(Config.EQ_ON)) {
            loader.toggleEqualizer(true);
            eq.setActionCommand(Config.EQ_OFF);
        } else if (cmd.equalsIgnoreCase(Config.EQ_OFF)) {
            loader.toggleEqualizer(false);
            eq.setActionCommand(Config.EQ_ON);
        } else if (cmd.equalsIgnoreCase(Config.PL_ON)) {
            loader.togglePlaylist(true);
            pl.setActionCommand(Config.PL_OFF);
        } else if (cmd.equalsIgnoreCase(Config.PL_OFF)) {
            loader.togglePlaylist(false);
            pl.setActionCommand(Config.PL_ON);
        } else if (cmd.equals(Config.LRC_ON)) {
            loader.toggleLyricWindow(true);
            lrc.setActionCommand(Config.LRC_OFF);
        } else if (cmd.equals(Config.LRC_OFF)) {
            loader.toggleLyricWindow(false);
            lrc.setActionCommand(Config.LRC_ON);
        } else if (cmd.equals(Config.VOL_ON)) {
            config.setMute(false);
            changeSpeakerState();
        } else if (cmd.equals(Config.VOL_OFF)) {
            config.setMute(true);
            changeSpeakerState();
        } //        if (cmd.equalsIgnoreCase(PlayerActionEvent.MIPREFERENCES)) {
        //            processPreferences(e.getModifiers());
        //        } // Skin browser
        //        else if (cmd.equals(PlayerActionEvent.MISKINBROWSER)) {
        //            processSkinBrowser(e.getModifiers());
        //        } // Jump to file
        //        else if (cmd.equals(PlayerActionEvent.MIJUMPFILE)) {
        //            processJumpToFile(e.getModifiers());
        //        } // Stop
        else if (cmd.equals(Config.STOP)) {
            processStop(e.getModifiers());
        } // Load skin
        //        else if (e.getActionCommand().equals(PlayerActionEvent.MILOADSKIN)) {
        //            File[] file = FileSelector.selectFile(loader, FileSelector.OPEN, false, skin.getResource("skin.extension"), skin.getResource("loadskin.dialog.filtername"), new File(config.getLastDir()));
        //            if (FileSelector.getInstance().getDirectory() != null) {
        //                config.setLastDir(FileSelector.getInstance().getDirectory().getPath());
        //            }
        //            if (file != null) {
        //                String fsFile = file[0].getName();
        //                skin.setPath(config.getLastDir() + fsFile);
        //                loadSkin();
        //                config.setDefaultSkin(skin.getPath());
        //            }
        //        } // Shuffle
        //        else if (cmd.equals(PlayerActionEvent.ACSHUFFLE)) {
        //            if (skin.getAcShuffle().isSelected()) {
        //                config.setShuffleEnabled(true);
        //                if (playlist != null) {
        //                    playlist.shuffle();
        //                    playlistUI.initPlayList();
        //                    // Play from the top
        //                    PlaylistItem pli = playlist.getCursor();
        //                    setCurrentSong(pli);
        //                }
        //            } else {
        //                config.setShuffleEnabled(false);
        //            }
        //        } // Repeat
        //        else if (cmd.equals(PlayerActionEvent.ACREPEAT)) {
        //            if (skin.getAcRepeat().isSelected()) {
        //                config.setRepeatEnabled(true);
        //            } else {
        //                config.setRepeatEnabled(false);
        //            }
        //        } // Play file
        //        else if (cmd.equals(PlayerActionEvent.MIPLAYFILE)) {
        //            processEject(MouseEvent.BUTTON1_MASK);
        //        } // Play URL
        //        else if (cmd.equals(PlayerActionEvent.MIPLAYLOCATION)) {
        //            processEject(MouseEvent.BUTTON3_MASK);
        //        } // Playlist menu item
        //        else if (cmd.equals(PlayerActionEvent.MIPLAYLIST)) {
        //            skin.getAcPlaylist().setSelected(miPlaylist.getState());
        //            togglePlaylist();
        //        } // Playlist toggle button
        //        else if (cmd.equals(PlayerActionEvent.ACPLAYLIST)) {
        //            togglePlaylist();
        //        } // EqualizerUI menu item
        //        else if (cmd.equals(PlayerActionEvent.MIEQUALIZER)) {
        //            skin.getAcEqualizer().setSelected(miEqualizer.getState());
        //            toggleEqualizer();
        //        } // EqualizerUI
        //        else if (cmd.equals(PlayerActionEvent.ACEQUALIZER)) {
        //            toggleEqualizer();
        //        } // Exit player
        //        else if (cmd.equals(PlayerActionEvent.ACEJECT)) {
        //            processEject(e.getModifiers());
        //        } // Play
        else if (cmd.equals(Config.PLAY)) {
            processPlay(e.getModifiers());
        } // Pause
        else if (cmd.equals(Config.PAUSE)) {
            processPause(e.getModifiers());
        } // Stop
        else if (cmd.equals(Config.STOP)) {
            processStop(e.getModifiers());
        } // Next
        else if (cmd.equals(Config.NEXT)) {
            processNext(e.getModifiers());
        } // Previous
        else if (cmd.equals(Config.PREVIOUS)) {
            processPrevious(e.getModifiers());
        } else {
        }
    }

    /**
     * state changed
     *
     * @param e
     */
    public void stateChanged(ChangeEvent e) {
        Object src = e.getSource();
        // Volume 音量进度条
        if (src == volume) {
            Object[] args = {String.valueOf(volume.getValue())};
            String volumeText = MessageFormat.format(Config.getResource("slider.volume.text"), args);
            //在某个标题的地方显示一些提示的信息,
            //完全可以显示在歌曲信息的地方
            if (volume.getValueIsAdjusting()) {
                showTitle(volumeText);
            } else {
                showTitle(currentState);
            }
            try {
                int gainValue = volume.getValue();
                int maxGain = volume.getMaximum();
                //System.out.println("gainValue:"+gainValue+",maxGain:"+maxGain);
                config.setVolume(gainValue);
                //如果设置是静音的话,那就不用将更新到播放器去了
                if (config.isMute()) {
                    return;
                }
                if (gainValue == 0) {
                    player.setGain(0);
                } else {
                    double value = ((double) gainValue / (double) maxGain);
                    //System.out.println("gain:"+value);
                    player.setGain(value);
                }
            } catch (BasicPlayerException ex) {
                //System.out.println("====================");
                ex.printStackTrace();
                //System.out.println("====================");
            }
        } // Balance
        else if (src == pan) {
            //这里要搞清楚PAN的最大值最小值,应该是从负数到正数
            Object[] args = {String.valueOf(Math.abs(pan.getValue() * 10 / Config.BALANCEMAX))};
            String balanceText = null;
            if (pan.getValue() > 0) {
                balanceText = MessageFormat.format(Config.getResource("slider.balance.text.right"), args);
            } else if (pan.getValue() < 0) {
                balanceText = MessageFormat.format(Config.getResource("slider.balance.text.left"), args);
            } else {
                balanceText = MessageFormat.format(Config.getResource("slider.balance.text.center"), args);
            }
            //再把它显示出来
            if (pan.getValueIsAdjusting()) {
                showTitle(balanceText);
            } else {
                showTitle(currentState);
            }
            try {
                float balanceValue = pan.getValue() * 1.0f / Config.BALANCEMAX;
                player.setPan(balanceValue);
            } catch (BasicPlayerException ex) {
                ex.printStackTrace();
            }
        } else if (src == pos) {
            if (pos.getValueIsAdjusting() == false) {
                if (posDragging == true) {
                    posDragging = false;
                    posValue = pos.getValue() * 1.0 / Config.POSBARMAX;
                    processSeek(posValue);
                }
            } else {
                posDragging = true;
                posValueJump = true;
            }
        }
    }

    /**
     *
     * @param rate
     */
    protected void processSeek(double rate) {
        try {
            if ((audioInfo != null) && (audioInfo.containsKey("audio.type"))) {
                String type = (String) audioInfo.get("audio.type");
                // Seek support for MP3.
                if ((type.equalsIgnoreCase("mp3")) && (audioInfo.containsKey("audio.length.bytes"))) {
                    long skipBytes = Math.round(((Integer) audioInfo.get("audio.length.bytes")) * rate);
                    player.seek(skipBytes);
                    long length = currentItem.getLength();
                    if (length > 0) {
                        seekedTime = (long) (length * 1000 * rate);
                    }
                } // Seek support for WAV.
                else if ((type.equalsIgnoreCase("wave")) && (audioInfo.containsKey("audio.length.bytes"))) {
                    long skipBytes = Math.round(((Integer) audioInfo.get("audio.length.bytes")) * rate);
                    player.seek(skipBytes);
                    long length = currentItem.getLength();
                    if (length > 0) {
                        seekedTime = (long) (length * 1000 * rate);
                    }
                } else {
                    posValueJump = false;
                }
            } else {
                posValueJump = false;
            }
            applyPanAndGain();
        } catch (BasicPlayerException ioe) {
            posValueJump = false;
        }
    }

    public void opened(Object stream, Map<?, ?> properties) {
        audioInfo = properties;
        //在这里既然有了playListItem，那么歌曲的信息就
        //没有必要又从文件里面再读一遍了，并且不一定播放的就是
        //文件，也有可能是网络上的一个地址，所以最好的办法是从
        //当前正在播放的playListItem里面去读出想要的TagInfo,
        //正好TagInfo是一个接口，可以有很多种实现。可以是ID3v1也可能是APEv2...
//        String s = playlist.getCursor().getLocation();
//        File f = new File(s);
//        lyric = new Lyric(Util.getSongName(f), new SongInfo(f));
//        lp.setLyric(lyric);
//        log.log(Level.SEVERE, properties.toString());
    }

    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map<?, ?> properties) {
        processProgress(bytesread, microseconds, pcmdata, properties);
    }

    public void stateUpdated(BasicPlayerEvent event) {
        processStateUpdated(event);
    }

    public void setController(BasicController controller) {
        this.player = controller;
    }

    private class MouseVolumeListener implements MouseWheelListener {

        public void mouseWheelMoved(MouseWheelEvent e) {
            int index = e.getUnitsToScroll();
            int value = volume.getValue();
            volume.setValue(value - index);
        }
    }
}
