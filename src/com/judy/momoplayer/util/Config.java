/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import com.judy.momoplayer.lyric.LyricPanel;
import com.judy.momoplayer.player.ui.PlayerUI;
import com.judy.momoplayer.playlist.PlayList;
import com.judy.momoplayer.setting.OptionDialog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * 一个保存所有可配的信息的类，把所有可 保存的信息放到一个对象里面，这样便于保存和读取
 *
 * @author june
 */
public class Config implements Serializable {

    private static final long serialVersionUID = 20071127L;
    private static final Logger log = Logger.getLogger(Config.class.getName());
    public int voteOpenCount, voteOneHourCount;
    /**
     * ****************************************************************
     */
    /**
     * ***********常量定义区********************************************
     */
    /**
     * ****************************************************************
     */
    public static final String NAME = "MOMOPlayer";//MOMOPlayer
    public static final String EXTS = "snd,aifc,aif,wav,au,mp1,mp2,mp3,ogg,spx,flac,ape,mac";
    public static final File HOME = new File(System.getProperty("user.home") + File.separator + ".MOMOPlayer");//MOMOPlayer的目录文件
    public static final int POSBARMAX = 1000;//位置滚动条最大的数
    public static final int VOLUMEMAX = 100;//音量最大
    public static final int BALANCEMAX = 5;//左右平衡最大的值
    /**
     * *******************ActionCommand定义区*************************
     */
    public static final String PLAY = "play";//播放
    public static final String PAUSE = "pause";//暂停
    public static final String STOP = "stop";//停止
    public static final String PREVIOUS = "previous";//上一首
    public static final String NEXT = "next";//下一首
    public static final String CLOSE = "close";//关闭
    public static final String MINIMIZE = "minimize";//最小化
    public static final String SETTING = "setting";//设置
    public static final String EQ_ON = "eq_on";//均衡器打开
    public static final String EQ_OFF = "eq_off";//均衡器关闭
    public static final String LRC_ON = "lrc_on";//歌词打开
    public static final String LRC_OFF = "lrc_off";//歌词关闭
    public static final String VOL_ON = "vol_on";//音量开
    public static final String VOL_OFF = "vol_off";//音量闭
    public static final String PL_ON = "pl_on";//音乐列表开
    public static final String PL_OFF = "pl_off";//音乐列表关
    public static final String ABOUT = "about";//关于
    public static final String EQ_ENABLE = "eq_enable";
    public static final String EQ_DISABLE = "eq_disable";
    public static final String EQ_AUTO_ENABLE = "eq_auto_enable";
    public static final String EQ_AUTO_DISABLE = "eq_auto_disable";
    public static final String EQ_PRESET = "eq_preset";
    /**
     * *****************检查更新策略常量定义区***********************
     */
    public static final String CHECK_DAY = "check.day";//每天检查
    public static final String CHECK_WEEK = "check.week";//每周检查
    public static final String CHECK_MONTH = "check.month";//每月检查
    public static final String CHECK_NONE = "check.none";//不检查
    /**
     * *****************读取文件标签策略常量定义区*******************
     */
    public static final String READ_WHEN_ADD = "read.when.add";//当添加时读取标签
    public static final String READ_WHEN_DISPLAY = "read.when.display";//当显示时读取标签
    public static final String READ_WHEN_PLAY = "read.when.play";//当播放时读取标签
    /**
     * *****************歌词对齐常量定义区**************************
     */
    public static final int LYRIC_LEFT_ALIGN = 1;//歌词左对齐
    public static final int LYRIC_RIGHT_ALIGN = 2;//歌词右对齐
    public static final int LYRIC_CENTER_ALIGN = 3;//歌词中间对齐
    /**
     * *****************可视化区示波的消逝速度常量定义区*************
     */
    public static final int DISAPPEAR_QUICK = 1;//消逝的速度快
    public static final int DISAPPEAR_NORMAL = 2;//消逝的速度普通
    public static final int DISAPPEAR_SLOW = 3;//消逝的速度慢
    /**
     * *****************普通常量定义区******************************
     */
    public static final int REPEAT_ONE = 100;//单曲重复
    public static final int REPEAT_ALL = 101;//整体重复
    public static final String TITLETEXT = "MOMOPlayer";//MOMOPlayer
    public static final String[] protocols = {"http:", "file:", "ftp:", 
        "https:", "ftps:", "jar:"};//协议
    public static final int ORDER_PLAY = 1;//表示顺序播放
    public static final int RANDOM_PLAY = 0;//表示随机播放
//    public static final String TAGINFO_POLICY_FILE = "file";
//    public static final String TAGINFO_POLICY_ALL = "all";
    public static final int APEv2_ID3v2_ID3v1 = 1;//读取的顺序,有四种组合
    public static final int ID3v2_APEv2_ID3v1 = 2;
    public static final int ID3v1_APEv2_ID3v2 = 3;
    public static final int ID3v1_ID3v2_APEv2 = 4;
    public static final int WRITEMODE_ID3v1 = 1;//写入的顺序,有六种组合,用位来组合
    public static final int WRITEMODE_ID3v2 = 2;
    public static final int WRITEMODE_APEv2 = 4;
    public static final String LRC_WINDOW = "lrcWindow";//歌词
    public static final String PL_WINDOW = "plWindow";//列表
    public static final String EQ_WINDOW = "eqWindow";//均衡器
    public static final String MAIN_WINDOW = "mainWindow";//主窗口
    public static final String UNKNOWN_WINDOW = "unknown";//未知
    public static final int SNAP = 15;//吸附的象素
    public static final int MOVE = 1;
    public static final int LEFT = 2;
    public static final int RIGHT = 3;
    public static final int TOP = 4;
    public static final int BOTTOM = 5;
    public static final int LEFT_TOP = 6;
    public static final int LEFT_BOTTOM = 7;
    public static final int RIGHT_TOP = 8;
    public static final int RIGHT_BOTTOM = 9;
    public static final int SHOWTIME_POSITIVE = 1;//以正数显示
    public static final int SHOWTIME_NEGATIVE = -1;//以负数显示.
    /**
     * ****************************************************************
     */
    /**
     * ***********普通变量定义区****************************************
     */
    /**
     * ****************************************************************
     */
    /**
     * 载入资源配置文件
     */
    private static final ResourceBundle rb = ResourceBundle.getBundle("com/hadeslee/momoplayer/util/UIInfo");
    private boolean showLrc = true;//是否显示歌词秀窗口
    private boolean showPlayList = true;//是否显示播放列表
    private boolean showEq = true;//是否显示调音台
    private boolean repeatEnabled = true;//是否允许重复,允许了以后,有两种模式,1,单曲重复,2,全部重复
    private boolean equalizerOn = true;//表示调音器是否生效.面板显不显示和这个没关系
    private boolean equalizerAuto;//表示调音器是否自动设置
    private boolean shadow = false;//是否要窗口渐入渐出
    private boolean isSnapEqWindow = true, isSnapLrcWindow = true, isSnapPlWindow = true;//是否吸纳住了三个窗口
    private boolean isLinux;//是否是LINUX，它很多东西不支持
    private boolean useProxy;//是否使用代理服务器搜索歌词
    private boolean mute;//是否静音
    private String audioDevice;//混音器设备的名字
    private String playListFileName;//播放列表的文件名,放在程序同目录下
    private String currentFileOrUrl;//最后也就是当前正在播放的文件或者URL
    private String lastDir;//最后所使用的目录
//    private String tagInfoPolicy = TAGINFO_POLICY_ALL;//标签的读取策略，是只读文件还是都读。
    private String encoding = "UTF-8";//读取和写入标签的编码 update by june private String encoding = "GBK"
    private String proxyHost, proxyPort;//代理服务器的主机和端口号
    private String proxyUserName, proxyPwd;//代理服务器的用户名和密码
    private String currentPlayListName;//当前选中的播放列表的名字,下次打开也选中它
    private int repeatStrategy = REPEAT_ALL;//重复策略
    private int gainValue = VOLUMEMAX;//表示音量的大小保存量
    private int panValue = 0;//表示声道的保存量
    private int playStrategy = ORDER_PLAY;//表示播放策略,是顺序还是随机
    private int xLocation = 300, yLocation = 100;//主窗口在屏幕上面的XY座标
    private int bufferSize = 8;//缓冲大小
    private int readTagOrder = APEv2_ID3v2_ID3v1;//读取标签的顺序,默认是ape,v2,v1
    private int writeTagMode = WRITEMODE_ID3v1;//默认是使用ID3v1来写入
    private int showTimeStyle = SHOWTIME_POSITIVE;//显示时间的方式
    private int[] lastEqualizer;//最后调音器的配置,以便下次导入
    private Point eqLocation, lrcLocation, plLocation;//三个窗口的位置
    private Point disLrc, disEq, disPl;//三个面板和主面板的距离
//    private File lyricDir = new File(System.getProperty("user.home"));//歌词的搜索目录,并不是写入的目录,写入还是固定在user.home里面
    private Dimension lrcSize, plSize;//歌词秀和播放列表的大小
    private final Vector<PlayList> playlists;//所有的播放列表
    private final Map<String, Set<String>> componentMap;//一个窗口关系的变量
    private Date lastCheckUpdate = new Date();//最后一次检查更新的时候
    /**
     * ****************************************************************
     */
    /**
     * ***************不参与序列化的变量声明区***************************
     */
    /**
     * ***************这些变量在使用前必须检查是否为空*******************
     */
    /**
     * ***************或者在初始化程序的时候,必须赋值给它****************
     */
    /**
     * ****************************************************************
     */
    private transient JFrame topParent;//顶级窗口类是谁
    private transient JDialog lrcWindow;//歌词电灯显示窗口
    private transient JDialog plWindow;//播放列表的窗口
    private transient JDialog eqWindow;//均衡器的窗口
    private transient List<String> mixers;//里面存的是所有的混音器,不用序列化
    private transient OptionDialog optionDialog;//选项对话框
    private transient PlayerUI player;//一个不序列化的播放器对象
    /**
     * ****************************************************************
     */
    /**
     * ***************常规设置要用到的变量*******************************
     */
    /**
     * ****************************************************************
     */
    private boolean startAutoMinimize;//是否自动在启动的时候最小化
    private boolean showTrayIcon = true;//是否显示系统栏图标
    private boolean showPlayTip;//是否显示播放提示,在屏幕的右下角
    private boolean showTitleInTaskBar = true;//是否在任务栏显示正在播放的歌曲
    private transient boolean autoShutDown;//是否自动关机,不参与序列化
    private transient Date shutDownTime;//自动关机的时间,不参与序列化
    private String checkUpdateStrategy = CHECK_DAY;//检查更新频率的策略,默认每天
    private boolean autoCloseDialogWhenSave;//在点击保存设置的时候,是否自动关闭对话框
    private boolean miniHide;//最小化的时候是否隐藏主界面
    /**
     * ****************************************************************
     */
    /**
     * ****************歌词搜索面板要用到的变量**************************
     */
    /**
     * ****************************************************************
     */
    private Vector<File> searchLyricDirs = new Vector<File>();//搜索歌词的目录
    private boolean autoSearchLyricOnline = true;//播放音频文件时自动在线搜索歌词
    private boolean searchWhenInfoFull;//是否当信息完整时才搜索
    private boolean selectBestLyric = true;//自动选择最佳歌词
    private boolean autoRelatingWithMediaFile = true;//下载后自动和歌曲文件相关联
    private boolean autoOverWriteExistFile;//自动覆盖已存在的歌词文件
    private boolean saveTheSameNameAsMediaFile;//保存与音频文件相同文件名的歌词
    private File saveLyricDir = new File(HOME, "Lyrics");//保存歌词文件的目录，歌词存放的目是 /Lyrics
    /**
     * ****************************************************************
     */
    /**
     * ****************播放设置面板要用到的变量**************************
     */
    /**
     * ****************************************************************
     */
    private boolean autoPlayWhenStart;//是否在程序启动的时候自动播放
    private boolean maintainLastPlay;//是否继续最后一次的播放进度
    private double lastRate;//最后一次的播放进度
    private int sequencePlayInterval;//连续播放的时间间隔(单位:秒)
    private boolean stopWhenError;//当出现错误的时候,是否停止播放
    /**
     * ****************************************************************
     */
    /**
     * ****************播放列表设置面板要用到的变量**********************
     */
    /**
     * ****************************************************************
     */
    private boolean canDnD = true;//能否拖放
    private boolean disableDelete;//是否禁用文件删除功能
    private boolean savePlayListByAbsolutePath = true;//保存歌曲列表的时候,是否使用绝对路径名
    private boolean ignoreBadFile;//加载列表的时候,是否忽略错误的文件
    private boolean showTooltipOnPlayList = true;//在播放列表上面是否显示歌曲的提示信息
    private String readTagInfoStrategy = READ_WHEN_DISPLAY;//读取文件标签的策略,默认是显示的时候读取标签
    private Color playlistTitleColor = new Color(0, 128, 255);//标题的颜色
    private Color playlistHiLightColor = new Color(0, 244, 245);//高亮的颜色
    private Color playlistIndexColor = new Color(0, 128, 0);//序号的颜色
    private Color playlistLengthColor = new Color(192, 128, 32);//长度的颜色
    private Color playlistSelectedColor = Color.WHITE;//选中的颜色
    private Color playlistSelectedBG = new Color(46, 96, 184);
    private Color playlistBackground1 = new Color(32, 32, 32);//背景1的颜色
    private Color playlistBackground2 = new Color(0, 0, 0);//背景2的颜色
    private Font playlistFont = new Font("Dialog", Font.PLAIN, 12);//播放列表的字体
    /**
     * ****************************************************************
     */
    /**
     * ****************歌词秀设置面板要用到的变量************************
     */
    /**
     * ****************************************************************
     */
    private int lpState = LyricPanel.V;//表示歌词显示面板的状态,是横向还是纵向
    private int lyricAlignMode = LYRIC_CENTER_ALIGN;//歌词对齐模式,是左对齐还是右对齐,还是中间对齐
    private int H_SPACE = 10;//表示左右两句之间的距离
    private int V_SPACE = 0;//表示上下两句之间的距离
    private boolean lyricShadow = true;//是否歌词淡入淡出
    private boolean karaoke = true;//是否卡拉OK的方式来显示歌词
    private boolean transparency;//是否背景颜色透明
    private boolean showLrcBorder = true;//在透明的时候是否显示歌词秀的边框,只有在透明的时候有效,不透明就不理会了
    private Color lyricHilight = new Color(0, 244, 245);//歌词高亮颜色
    private Color lyricForeground = new Color(100, 100, 100);//歌词前景颜色
    private Color lyricBackground = new Color(6, 6, 6);//歌词背景颜色
    private Font lyricFont = new Font("Dialog", Font.PLAIN, 14);
    private boolean autoLoadLyric = true;//播放时是否自动加载歌词
    private boolean cutBlankChars;//是否截掉空白字符
    private boolean hideWhenNoLyric;//是否在没有歌词的时候自动隐藏
    private boolean lyricTopShow;//是否歌词最前端显示
    private boolean autoResize = true;//表示是否根据歌词自适歌词秀的宽度
    private boolean onlyResizeWhenVerticalMode = true;//仅在垂直模式下能自动调整宽度
    private boolean mouseDragToSeekEnabled = true;//是否起用鼠标拖动歌词定位歌曲
    private boolean antiAliasing;//是否抗据齿显示字体 
    private boolean mouseScrollAjustTime = true;//是否可以用鼠标滚动来调整歌词的整体时间
    private int refreshInterval = 80;//表示线程休息的时间间隔(单位:毫秒)
    /**
     * ****************************************************************
     */
    /**
     * ****************可视化效果设置面板要用到的变量********************
     */
    /**
     * ****************************************************************
     */
    private int audioChartDisplayMode = AudioChart.DISPLAY_MODE_SPECTRUM_ANALYSER;//显示模式,或者是否显示
    private int audioChartfps = 25;//视觉效果的FPS
    private Color audioChartTopColor = Color.RED;//上部分的颜色
    private Color audioChartCenterColor = Color.YELLOW;//中间部分的颜色
    private Color audioChartbottomColor = new Color(0, 255, 255);//下面部份的颜色
    private Color audioChartPeakColor = Color.WHITE;//顶端的小小的颜色
    private Color audioChartlineColor = new Color(0, 255, 255);//波形的时候,线条的颜色
    private int audioChartDisappearSpeed = DISAPPEAR_NORMAL;//消逝的速度
    private int audioChartBarCount = 20;//条柱个数
    
    private static Config config = new Config();//自己的一个单例的对象 

    private Config() {
        lastEqualizer = new int[10];
        playlists = new Vector<PlayList>();
        Arrays.fill(lastEqualizer, 50);
        componentMap = new HashMap<String, Set<String>>();
        searchLyricDirs.add(saveLyricDir);
    }

    /**
     * 得到全局的单例的config对象
     *
     * @return
     */
    public synchronized static Config getConfig() {
        return config;
    }
    
    static {
        load();
    }
    
    /**
     * 以默认配置加载
     * @return 
     */
    public static boolean load() {
        ObjectInputStream ois = null;
        try {
            if (!HOME.exists()) {
                HOME.mkdirs();
            }
            ois = new ObjectInputStream(new FileInputStream(new File(Config.HOME, NAME + ".dat")));
            config = (Config) ois.readObject();
            log.log(Level.INFO, Config.getResource("SongInfoDialog.loadConfigSuccess"));
            return true;
        } catch (IOException ex) {
            log.log(Level.SEVERE, Config.getResource("SongInfoDialog.loadConfigFailure"));
            return false;
        } catch (ClassNotFoundException ex) {
            log.log(Level.SEVERE, Config.getResource("SongInfoDialog.loadConfigFailure"));
            return false;
        } finally {
            try {
                config.isLinux = System.getProperty("os.name").startsWith("Linux");
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                log.log(Level.OFF, "\u914d\u7f6e\u5931\u8d25\u3002{0}", ex.getMessage());
            }
        }
    }

    /**
     * 根据配置信息，加载配置
     * @param initConfig
     * @return 
     */
    public static boolean load(String initConfig) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(initConfig));
            config = (Config) ois.readObject();
            ois.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * 保存配置
     * @param path 
     */
    public static void save(String path) {
        try {
            FileOutputStream fout = new FileOutputStream(new File(path));
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(config);
            oos.flush();
            fout.close();
            log.log(Level.INFO, Config.getResource("SongInfoDialog.saveConfigSuccess"));
        } catch (IOException exe) {
            log.log(Level.INFO, Config.getResource("SongInfoDialog.saveConfigFailure"));
            exe.printStackTrace();
        }
    }

    /**
     * 保存默认配置
     */
    public static void save() {
        try {
            if (!Config.HOME.exists()) {
                Config.HOME.mkdirs();
            }
            FileOutputStream fout = new FileOutputStream((new File(Config.HOME, NAME + ".dat")));
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(config);
            oos.flush();
            fout.close();
            log.log(Level.INFO, Config.getResource("SongInfoDialog.saveConfigSuccess"));
        } catch (IOException exe) {
            log.log(Level.INFO, Config.getResource("SongInfoDialog.saveConfigFailure"));
            exe.printStackTrace();
        }
    }
    
    
    @SuppressWarnings("unused")
    private int getDirection(int dis, Rectangle myBound, Rectangle otherBound) {
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

    /**
     * 根据位置修改部件表
     */
    private void udpateComponentMapWithLoation() {
        List<Component> list = new ArrayList<Component>();
        Rectangle otherBound = new Rectangle();
        Rectangle myBound = new Rectangle();
        list.add(eqWindow);
        list.add(lrcWindow);
        list.add(topParent);
        list.add(plWindow);
        componentMap.clear();
        //先查歌词秀窗口吸附到谁了
        Component me = lrcWindow;
        me.getBounds(myBound);
        Set<String> set = new HashSet<String>();
        for (Component c1 : list) {
            if (c1 != null && c1 != me && c1.isShowing() && me.isShowing()) {
                c1.getBounds(otherBound);
                int dis = Util.getDistance(myBound, otherBound);
                if (Math.abs(dis) <= 3) {
                    set.add(getComponentName(c1));
                    if (c1 == topParent) {
                        break;
                    }
                }
            }
        }
        componentMap.put(getComponentName(me), set);
        //再查播放列表窗口
        set = new HashSet<String>();
        me = plWindow;
        me.getBounds(myBound);
        for (Component c1 : list) {
            if (c1 != null && c1 != me && c1.isShowing() && me.isShowing()) {
                c1.getBounds(otherBound);
                int dis = Util.getDistance(myBound, otherBound);
                if (Math.abs(dis) <= 3) {
                    set.add(getComponentName(c1));
                    if (c1 == topParent) {
                        break;
                    }
                }
            }
        }
        componentMap.put(getComponentName(me), set);
        //再查EQ窗口
        set = new HashSet<String>();
        me = eqWindow;
        me.getBounds(myBound);
        for (Component c1 : list) {
            if (c1 != null && c1 != me && c1.isShowing() && me.isShowing()) {
                c1.getBounds(otherBound);
                int dis = Util.getDistance(myBound, otherBound);
                if (Math.abs(dis) <= 3) {
                    set.add(getComponentName(c1));
                    if (c1 == topParent) {
                        break;
                    }
                }
            }
        }
        componentMap.put(getComponentName(me), set);
    }

    /**
     * 更新组件的吸附状态
     */
    public void updateComponentSnap() {
        //先更新窗口互相之间的状态,比如有没有吸附
        udpateComponentMapWithLoation();
        log.log(Level.CONFIG, componentMap.toString());
        //更新歌词秀窗口状态
        String me = this.getComponentName(lrcWindow);
        Set<String> set = componentMap.get(me);//这里放的是可以遍历的字符串
        Set<String> list = new HashSet<String>();//这个列表放的是遍历过的字符串
        list.add(me);
        boolean find = false;
        out:
        while (true) {
            if (set.isEmpty()) {
                break out;
            } else {
                Set<String> temp = new HashSet<String>();
                for (String other : set) {
                    log.log(Level.FINEST, "me={0},other={1}", new Object[]{me, other});
                    if (other.equals(MAIN_WINDOW)) {
                        find = true;
                        break out;
                    } else if (!list.contains(other)) {
                        temp.addAll(componentMap.get(other));
                    }
                    list.add(other);
                }
                set.removeAll(list);
                set.addAll(temp);
            }
        }
        this.setSnapWindow(lrcWindow, find);
        //更新播放列表状态
        me = this.getComponentName(plWindow);
        list.clear();
        list.add(me);
        set = componentMap.get(me);
        find = false;
        out:
        while (true) {
            if (set.isEmpty()) {
                break out;
            } else {
                Set<String> temp = new HashSet<String>();
                for (String other : set) {
                    log.log(Level.FINEST, "me={0},other={1}", new Object[]{me, other});
                    if (other.equals(MAIN_WINDOW)) {
                        find = true;
                        break out;
                    } else if (!list.contains(other)) {
                        temp.addAll(componentMap.get(other));
                    }
                    list.add(other);
                }
                set.removeAll(list);
                set.addAll(temp);
            }
        }
        this.setSnapWindow(plWindow, find);
        //更新EQ状态
        me = this.getComponentName(eqWindow);
        list.clear();
        list.add(me);
        set = componentMap.get(me);
        find = false;
        out:
        while (true) {
            if (set.isEmpty()) {
                break out;
            } else {
                Set<String> temp = new HashSet<String>();
                for (String other : set) {
                    log.log(Level.FINEST, "me={0},other={1}", new Object[]{me, other});
                    if (other.equals(MAIN_WINDOW)) {
                        find = true;
                        break out;
                    } else if (!list.contains(other)) {
                        temp.addAll(componentMap.get(other));
                    }
                    list.add(other);
                }
                set.removeAll(list);
                set.addAll(temp);
            }
        }
        this.setSnapWindow(eqWindow, find);
    }

    private void setSnapWindow(Component com, boolean snap) {
        if (com == this.lrcWindow) {
            this.setIsSnapLrcWindow(snap);
        } else if (com == this.plWindow) {
            this.setIsSnapPlWindow(snap);
        } else if (com == this.eqWindow) {
            this.setIsSnapEqWindow(snap);
        }
    }

    /**
     * 获取部件名称
     * @param com
     * @return 
     */
    public String getComponentName(Component com) {
        if (com == this.lrcWindow) {
            return LRC_WINDOW;
        } else if (com == this.plWindow) {
            return PL_WINDOW;
        } else if (com == this.eqWindow) {
            return EQ_WINDOW;
        } else if (com == this.topParent) {
            return MAIN_WINDOW;
        } else {
            return UNKNOWN_WINDOW;
        }
    }

    /**
     * 以那种协议开始
     * @param input
     * @return 
     */
    public static boolean startWithProtocol(String input) {
        boolean ret = false;
        if (input != null) {
            input = input.toLowerCase();
            for (String protocol : protocols) {
                if (input.startsWith(protocol)) {
                    ret = true;
                    break;
                }
            }
        }
        return ret;
    }
    
    public boolean isMiniHide() {
        return miniHide;
    }

    public void setMiniHide(boolean miniHide) {
        this.miniHide = miniHide;
    }

    public Date getLastCheckUpdate() {
        return lastCheckUpdate;
    }

    public void setLastCheckUpdate(Date lastCheckUpdate) {
        this.lastCheckUpdate = lastCheckUpdate;
    }

    public boolean isAutoCloseDialogWhenSave() {
        return autoCloseDialogWhenSave;
    }

    public void setAutoCloseDialogWhenSave(boolean autoCloseDialogWhenSave) {
        this.autoCloseDialogWhenSave = autoCloseDialogWhenSave;
    }

    public int getPanValue() {
        return panValue;
    }

    public void setPanValue(int panValue) {
        this.panValue = panValue;
    }

    public int getAudioChartBarCount() {
        return audioChartBarCount;
    }

    public void setAudioChartBarCount(int audioChartBarCount) {
        this.audioChartBarCount = audioChartBarCount;
    }

    /**
     * 选项对话框，必须被赋值
     * @return 
     */
    public synchronized OptionDialog getOptionDialog() {
        if (optionDialog == null) {
            optionDialog = new OptionDialog(config.getTopParent(), true);
            optionDialog.setTitle(Config.getResource("Config.settingOption"));
            optionDialog.setLocationRelativeTo(null);
        }
        return optionDialog;
    }

    /**
     * 播放器
     * @return 
     */
    public PlayerUI getPlayer() {
        return player;
    }

    public void setPlayer(PlayerUI player) {
        this.player = player;
    }

    public Color getPlaylistSelectedBG() {
        return playlistSelectedBG;
    }

    public void setPlaylistSelectedBG(Color playlistSelectedBG) {
        this.playlistSelectedBG = playlistSelectedBG;
    }

    public Color getAudioChartCenterColor() {
        return audioChartCenterColor;
    }

    public void setAudioChartCenterColor(Color audioChartCenterColor) {
        this.audioChartCenterColor = audioChartCenterColor;
    }

    public int getAudioChartDisappearSpeed() {
        return audioChartDisappearSpeed;
    }

    public void setAudioChartDisappearSpeed(int audioChartDisappearSpeed) {
        this.audioChartDisappearSpeed = audioChartDisappearSpeed;
    }

    public Color getAudioChartTopColor() {
        return audioChartTopColor;
    }

    public void setAudioChartTopColor(Color audioChartTopColor) {
        this.audioChartTopColor = audioChartTopColor;
    }

    public Color getAudioChartbottomColor() {
        return audioChartbottomColor;
    }

    public void setAudioChartbottomColor(Color audioChartbottomColor) {
        this.audioChartbottomColor = audioChartbottomColor;
    }

    public Color getAudioChartPeakColor() {
        return audioChartPeakColor;
    }

    public void setAudioChartPeakColor(Color audioChartcolor) {
        this.audioChartPeakColor = audioChartcolor;
    }

    public Color getAudioChartlineColor() {
        return audioChartlineColor;
    }

    public void setAudioChartlineColor(Color audioChartlineColor) {
        this.audioChartlineColor = audioChartlineColor;
    }

    public boolean isAutoLoadLyric() {
        return autoLoadLyric;
    }

    public void setAutoLoadLyric(boolean autoLoadLyric) {
        this.autoLoadLyric = autoLoadLyric;
    }

    public boolean isCutBlankChars() {
        return cutBlankChars;
    }

    public void setCutBlankChars(boolean cutBlankChars) {
        this.cutBlankChars = cutBlankChars;
    }

    public boolean isHideWhenNoLyric() {
        return hideWhenNoLyric;
    }

    public void setHideWhenNoLyric(boolean hideWhenNoLyric) {
        this.hideWhenNoLyric = hideWhenNoLyric;
    }

    public int getLyricAlignMode() {
        return lyricAlignMode;
    }

    public void setLyricAlignMode(int lyricAlignMode) {
        this.lyricAlignMode = lyricAlignMode;
    }

    public boolean isLyricShadow() {
        return lyricShadow;
    }

    public void setLyricShadow(boolean lyricShadow) {
        this.lyricShadow = lyricShadow;
    }

    public boolean isMouseDragToSeekEnabled() {
        return mouseDragToSeekEnabled;
    }

    public void setMouseDragToSeekEnabled(boolean mouseDragToSeekEnabled) {
        this.mouseDragToSeekEnabled = mouseDragToSeekEnabled;
    }

    public boolean isOnlyResizeWhenVerticalMode() {
        return onlyResizeWhenVerticalMode;
    }

    public void setOnlyResizeWhenVerticalMode(boolean onlyResizeWhenVerticalMode) {
        this.onlyResizeWhenVerticalMode = onlyResizeWhenVerticalMode;
    }

    public boolean isCanDnD() {
        return canDnD;
    }

    public void setCanDnD(boolean canDnD) {
        this.canDnD = canDnD;
    }

    public boolean isDisableDelete() {
        return disableDelete;
    }

    public void setDisableDelete(boolean disableDelete) {
        this.disableDelete = disableDelete;
    }

    public boolean isIgnoreBadFile() {
        return ignoreBadFile;
    }

    public void setIgnoreBadFile(boolean ignoreBadFile) {
        this.ignoreBadFile = ignoreBadFile;
    }

    public Color getPlaylistBackground1() {
        return playlistBackground1;
    }

    public void setPlaylistBackground1(Color playlistBackground1) {
        this.playlistBackground1 = playlistBackground1;
    }

    public Color getPlaylistBackground2() {
        return playlistBackground2;
    }

    public void setPlaylistBackground2(Color playlistBackground2) {
        this.playlistBackground2 = playlistBackground2;
    }

    public Font getPlaylistFont() {
        return playlistFont;
    }

    public void setPlaylistFont(Font playlistFont) {
        this.playlistFont = playlistFont;
    }

    public Color getPlaylistHiLightColor() {
        return playlistHiLightColor;
    }

    public void setPlaylistHiLightColor(Color playlistHiLightColor) {
        this.playlistHiLightColor = playlistHiLightColor;
    }

    public Color getPlaylistIndexColor() {
        return playlistIndexColor;
    }

    public void setPlaylistIndexColor(Color playlistIndexColor) {
        this.playlistIndexColor = playlistIndexColor;
    }

    public Color getPlaylistLengthColor() {
        return playlistLengthColor;
    }

    public void setPlaylistLengthColor(Color playlistLengthColor) {
        this.playlistLengthColor = playlistLengthColor;
    }

    public Color getPlaylistSelectedColor() {
        return playlistSelectedColor;
    }

    public void setPlaylistSelectedColor(Color playlistSelectedColor) {
        this.playlistSelectedColor = playlistSelectedColor;
    }

    public Color getPlaylistTitleColor() {
        return playlistTitleColor;
    }

    public void setPlaylistTitleColor(Color playlistTitleColor) {
        this.playlistTitleColor = playlistTitleColor;
    }

    public String getReadTagInfoStrategy() {
        return readTagInfoStrategy;
    }

    public void setReadTagInfoStrategy(String readTagInfoStrategy) {
        this.readTagInfoStrategy = readTagInfoStrategy;
    }

    public boolean isSavePlayListByAbsolutePath() {
        return savePlayListByAbsolutePath;
    }

    public void setSavePlayListByAbsolutePath(boolean savePlayListByAbsolutePath) {
        this.savePlayListByAbsolutePath = savePlayListByAbsolutePath;
    }

    public boolean isShowTooltipOnPlayList() {
        return showTooltipOnPlayList;
    }

    public void setShowTooltipOnPlayList(boolean showTooltipOnPlayList) {
        this.showTooltipOnPlayList = showTooltipOnPlayList;
    }

    public boolean isAutoPlayWhenStart() {
        return autoPlayWhenStart;
    }

    public void setAutoPlayWhenStart(boolean autoPlayWhenStart) {
        this.autoPlayWhenStart = autoPlayWhenStart;
    }

    public double getLastRate() {
        return lastRate;
    }

    public void setLastRate(double lastRate) {
        this.lastRate = lastRate;
    }

    public boolean isMaintainLastPlay() {
        return maintainLastPlay;
    }

    public void setMaintainLastPlay(boolean maintainLastPlay) {
        this.maintainLastPlay = maintainLastPlay;
    }

    public int getSequencePlayInterval() {
        return sequencePlayInterval;
    }

    public void setSequencePlayInterval(int sequencePlayInterval) {
        this.sequencePlayInterval = sequencePlayInterval;
    }

    public boolean isStopWhenError() {
        return stopWhenError;
    }

    public void setStopWhenError(boolean stopWhenError) {
        this.stopWhenError = stopWhenError;
    }

    public boolean isAutoOverWriteExistFile() {
        return autoOverWriteExistFile;
    }

    public void setAutoOverWriteExistFile(boolean autoOverWriteExistFile) {
        this.autoOverWriteExistFile = autoOverWriteExistFile;
    }

    public boolean isAutoRelatingWithMediaFile() {
        return autoRelatingWithMediaFile;
    }

    public void setAutoRelatingWithMediaFile(boolean autoRelatingWithMediaFile) {
        this.autoRelatingWithMediaFile = autoRelatingWithMediaFile;
    }

    public boolean isAutoSearchLyricOnline() {
        return autoSearchLyricOnline;
    }

    public void setAutoSearchLyricOnline(boolean autoSearchLyricOnline) {
        this.autoSearchLyricOnline = autoSearchLyricOnline;
    }

    public File getSaveLyricDir() {
        return saveLyricDir;
    }

    public void setSaveLyricDir(File saveLyricDir) {
        this.saveLyricDir = saveLyricDir;
    }

    public boolean isSaveTheSameNameAsMediaFile() {
        return saveTheSameNameAsMediaFile;
    }

    public void setSaveTheSameNameAsMediaFile(boolean saveTheSameNameAsMediaFile) {
        this.saveTheSameNameAsMediaFile = saveTheSameNameAsMediaFile;
    }

    public Vector<File> getSearchLyricDirs() {
        return searchLyricDirs;
    }

    public void setSearchLyricDirs(Vector<File> v) {
        this.searchLyricDirs = v;
    }

    public boolean isSearchWhenInfoFull() {
        return searchWhenInfoFull;
    }

    public void setSearchWhenInfoFull(boolean searchWhenInfoFull) {
        this.searchWhenInfoFull = searchWhenInfoFull;
    }

    public boolean isSelectBestLyric() {
        return selectBestLyric;
    }

    public void setSelectBestLyric(boolean selectBestLyric) {
        this.selectBestLyric = selectBestLyric;
    }

    public boolean isAutoShutDown() {
        return autoShutDown;
    }

    public void setAutoShutDown(boolean autoShutDown) {
        this.autoShutDown = autoShutDown;
    }

    public boolean isStartAutoMinimize() {
        return startAutoMinimize;
    }

    public void setStartAutoMinimize(boolean autoStartMini) {
        this.startAutoMinimize = autoStartMini;
    }

    public String getCheckUpdateStrategy() {
        return checkUpdateStrategy;
    }

    public void setCheckUpdateStrategy(String checkUpdateStrategy) {
        this.checkUpdateStrategy = checkUpdateStrategy;
    }

    public boolean isShowPlayTip() {
        return showPlayTip;
    }

    public void setShowPlayTip(boolean showPlayTip) {
        this.showPlayTip = showPlayTip;
    }

    public boolean isShowTitleInTaskBar() {
        return showTitleInTaskBar;
    }

    public void setShowTitleInTaskBar(boolean showTitleInTaskBar) {
        this.showTitleInTaskBar = showTitleInTaskBar;
    }

    public boolean isShowTrayIcon() {
        return showTrayIcon;
    }

    public void setShowTrayIcon(boolean showTrayIcon) {
        this.showTrayIcon = showTrayIcon;
    }

    public Date getShutDownTime() {
        return shutDownTime;
    }

    public void setShutDownTime(Date shutDownTime) {
        this.shutDownTime = shutDownTime;
    }

    public boolean isShowLrcBorder() {
        return showLrcBorder;
    }

    public void setShowLrcBorder(boolean showBorder) {
        this.showLrcBorder = showBorder;
    }

    public boolean isTransparency() {
        return transparency;
    }

    public void setTransparency(boolean transparency) {
        this.transparency = transparency;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public Map<String, Set<String>> getComponentMap() {
        return componentMap;
    }

    public int getAudioChartfps() {
        return audioChartfps;
    }

    public void setAudioChartfps(int audioChartfps) {
        this.audioChartfps = audioChartfps;
    }

    public int getAudioChartDisplayMode() {
        return audioChartDisplayMode;
    }

    public void setAudioChartDisplayMode(int audioChartDisplayMode) {
        this.audioChartDisplayMode = audioChartDisplayMode;
    }

    public boolean isAntiAliasing() {
        return antiAliasing;
    }

    public void setAntiAliasing(boolean antiAliasing) {
        this.antiAliasing = antiAliasing;
    }

    public boolean isLyricTopShow() {
        return lyricTopShow;
    }

    public void setLyricTopShow(boolean topShow) {
        this.lyricTopShow = topShow;
    }

    public boolean isMouseScrollAjustTime() {
        return mouseScrollAjustTime;
    }

    public void setMouseScrollAjustTime(boolean mouseScrollAjustTime) {
        this.mouseScrollAjustTime = mouseScrollAjustTime;
    }

    public int getShowTimeStyle() {
        return showTimeStyle;
    }

    public void setShowTimeStyle(int showTimeStyle) {
        this.showTimeStyle = showTimeStyle;
    }

    public Dimension getLrcSize() {
        return lrcSize;
    }

    public void setLrcSize(Dimension lrcSize) {
        this.lrcSize = lrcSize;
    }

    public Dimension getPlSize() {
        return plSize;
    }

    public void setPlSize(Dimension plSize) {
        this.plSize = plSize;
    }

    public boolean isKaraoke() {
        return karaoke;
    }

    public void setKaraoke(boolean karaoke) {
        this.karaoke = karaoke;
    }

    public int getReadTagOrder() {
        return readTagOrder;
    }

    public void setReadTagOrder(int readTagOrder) {
        this.readTagOrder = readTagOrder;
    }

    public int getWriteTagMode() {
        return writeTagMode;
    }

    public void setWriteTagMode(int writeTagMode) {
        this.writeTagMode = writeTagMode;
    }

    public List<String> getMixers() {
        return mixers;
    }

    public void setMixers(List<String> mixers) {
        this.mixers = mixers;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyPwd() {
        return proxyPwd;
    }

    public void setProxyPwd(String proxyPwd) {
        this.proxyPwd = proxyPwd;
    }

    public String getProxyUserName() {
        return proxyUserName;
    }

    public void setProxyUserName(String proxyUserName) {
        this.proxyUserName = proxyUserName;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void updateDistance() {
        disLrc = new Point(lrcWindow.getLocation().x - topParent.getLocation().x,
                lrcWindow.getLocation().y - topParent.getLocation().y);
        disEq = new Point(eqWindow.getLocation().x - topParent.getLocation().x,
                eqWindow.getLocation().y - topParent.getLocation().y);
        disPl = new Point(plWindow.getLocation().x - topParent.getLocation().x,
                plWindow.getLocation().y - topParent.getLocation().y);
    }

    public boolean isLinux() {
        return isLinux;
    }

    public Point getDisEq() {
        return disEq;
    }

    public Point getDisLrc() {
        return disLrc;
    }

    public Point getDisPl() {
        return disPl;
    }

    public String getCurrentPlayListName() {
        return currentPlayListName;
    }

    public void setCurrentPlayListName(String currentPlayListName) {
        this.currentPlayListName = currentPlayListName;
    }

    public JDialog getPlWindow() {
        return plWindow;
    }

    public void setPlWindow(JDialog plWindow) {
        this.plWindow = plWindow;
    }

    public JDialog getLrcWindow() {
        return lrcWindow;
    }

    public void setLrcWindow(JDialog lrcWindow) {
        this.lrcWindow = lrcWindow;
    }

    public JDialog getEqWindow() {
        return eqWindow;
    }

    public void setEqWindow(JDialog eqWindow) {
        this.eqWindow = eqWindow;
    }

    public boolean isShadow() {
        return shadow;
//        return true;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void addPlayList(PlayList list) {
        playlists.add(list);
    }

    public Vector<PlayList> getPlayLists() {
        return playlists;
    }

    public Point getEqLocation() {
        return eqLocation;
    }

    public void setEqLocation(Point eqLocation) {
        this.eqLocation = eqLocation;
    }

    public Point getLrcLocation() {
        return lrcLocation;
    }

    public void setLrcLocation(Point lrcLocation) {
        this.lrcLocation = lrcLocation;
    }

    public Point getPlLocation() {
        return plLocation;
    }

    public void setPlLocation(Point plLocation) {
        this.plLocation = plLocation;
    }

    public boolean isSnapEqWindow() {
        return isSnapEqWindow;
    }

    public void setIsSnapEqWindow(boolean isSnapEqWindow) {
        this.isSnapEqWindow = isSnapEqWindow;
    }

    public boolean isSnapLrcWindow() {
        return isSnapLrcWindow;
    }

    public void setIsSnapLrcWindow(boolean isSnapLrcWindow) {
        this.isSnapLrcWindow = isSnapLrcWindow;
    }

    public boolean isSnapPlWindow() {
        return isSnapPlWindow;
    }

    public void setIsSnapPlWindow(boolean isSnapPlWindow) {
        this.isSnapPlWindow = isSnapPlWindow;
    }

    public int[] getLastEqualizer() {
        return lastEqualizer;
    }

//    public String getTagInfoPolicy() {
//        return tagInfoPolicy;
//    }
//
//    public void setTagInfoPolicy(String tag) {
//        this.tagInfoPolicy = tag;
//    }
    public int getXLocation() {
        return xLocation;
    }

    public int getYLocation() {
        return yLocation;
    }

    public String getAudioDevice() {
        return audioDevice;
    }

    public String getCurrentFileOrUrl() {
        return currentFileOrUrl;
    }

    public boolean isEqualizerOn() {
        return equalizerOn;
    }

    public String getPlaylistFilename() {
        return null;
    }

    public void setCurrentFileOrUrl(String currentFileOrUrl) {
        this.currentFileOrUrl = currentFileOrUrl;
    }

    public int getGainValue() {
        return gainValue;
    }

    public void setEqualizerAuto(boolean b) {
        this.equalizerAuto = b;
    }

    public boolean isEqualizerAuto() {
        return equalizerAuto;
    }

    public void setEqualizerOn(boolean b) {
        this.equalizerOn = b;
    }

    public void setGainValue(int gainValue) {
        this.gainValue = gainValue;
    }

    public void setIconParent(ImageIcon jlguiIcon) {
    }

    public void setLastEqualizer(int[] gainValue) {
        this.lastEqualizer = gainValue;
    }

    public void setLocation(int x, int y) {
        this.xLocation = x;
        this.yLocation = y;
    }

    public String getPlayListFileName() {
        return playListFileName;
    }

    public void setPlayListFileName(String playListFileName) {
        this.playListFileName = playListFileName;
    }

    public int getPlayStrategy() {
        return playStrategy;
    }

    public void setPlayStrategy(int playStrategy) {
        this.playStrategy = playStrategy;
    }

    public boolean isRepeatEnabled() {
        return repeatEnabled;
    }

    public void setRepeatEnabled(boolean b) {
        repeatEnabled = b;
    }

    public boolean isShowEq() {
        return showEq;
    }

    public void setAudioDevice(String mixerName) {
        this.audioDevice = mixerName;
    }

    public void setPlaylistFilename(String string) {
        this.playListFileName = string;
    }

    public void setShowEq(boolean showEq) {
        this.showEq = showEq;
    }

    public boolean isShowLrc() {
        return showLrc;
    }

    public void setShowLrc(boolean showLrc) {
        this.showLrc = showLrc;
    }

    public boolean isShowPlayList() {
        return showPlayList;
    }

    public void setShowPlayList(boolean showPlayList) {
        this.showPlayList = showPlayList;
    }

    public static Config getInstance() {
        return config;
    }

    public String getExtensions() {
        return null;
    }

    public static String getResource(String key) {
        return rb.getString(key);
    }

    public int getRepeatStrategy() {
        return repeatStrategy;
    }

    public void setRepeatStrategy(int repeatStrategy) {
        this.repeatStrategy = repeatStrategy;
    }

    public boolean isAutoResize() {
        return autoResize;
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    public int getLpState() {
        return lpState;
    }

    public void setLpState(int lpState) {
        this.lpState = lpState;
    }

    public Color getLyricBackground() {
        return lyricBackground;
    }

    public void setLyricBackground(Color BACK_GROUND) {
        this.lyricBackground = BACK_GROUND;
    }

    public Font getLyricFont() {
        return lyricFont;
    }

    public void setLyricFont(Font FONT) {
        this.lyricFont = FONT;
    }

    public Color getLyricForeground() {
        return lyricForeground;
    }

    public void setLyricForeground(Color FORE_GROUND) {
        this.lyricForeground = FORE_GROUND;
    }

    public Color getLyricHilight() {
        return lyricHilight;
    }

    public void setLyricHilight(Color HIGH_LIGHT) {
        this.lyricHilight = HIGH_LIGHT;
    }

    public int getH_SPACE() {
        return H_SPACE;
    }

    public void setH_SPACE(int H_SPACE) {
        this.H_SPACE = H_SPACE;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int REFRESH_INTERVAL) {
        this.refreshInterval = REFRESH_INTERVAL;
    }

    public int getV_SPACE() {
        return V_SPACE;
    }

    public void setTopParent(JFrame aThis) {
        this.topParent = aThis;
    }

    public JFrame getTopParent() {
        return topParent;
    }

    public void setV_SPACE(int V_SPACE) {
        this.V_SPACE = V_SPACE;
    }

    public void setVolume(int gainValue) {
        this.gainValue = gainValue;
    }

    public String getLastDir() {
        return lastDir;
    }

}
