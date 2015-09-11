/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.lyric;

import com.judy.momoplayer.playlist.PlayListItem;
import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表示一首歌的歌词对象,它可以以某种方式来画自己
 * @author hadeslee
 */
public class Lyric implements Serializable {

    private static final long serialVersionUID = 20071125L;
    private static final Logger log = Logger.getLogger(Lyric.class.getName());
    private int width;//表示歌词的显示区域的宽度
    private int height;//表示歌词的显示区域的高度
    private long time;//表示当前的时间是多少了。以毫秒为单位
    private long tempTime;//表示一个暂时的时间,用于拖动的时候,确定应该到哪了
    private List<Sentence> list = new ArrayList<Sentence>();//里面装的是所有的句子
    private boolean isMoving;//是否正在被拖动 
    private int currentIndex;//当前正在显示的歌词的下标
    private boolean initDone;//是否初始化完毕了
    private transient PlayListItem info;//有关于这首歌的信息
    private transient File file;//该歌词所存在文件
    private boolean enabled = true;//是否起用了该对象,默认是起用的
    private long during = Integer.MAX_VALUE;//这首歌的长度
    private int offset;//整首歌的偏移量
    //用于缓存的一个正则表达式对象
    private static final Pattern pattern = Pattern.compile("(?<=\\[).*?(?=\\])");

    /**
     * 用ID3V1标签的字节和歌名来初始化歌词
     * 歌词将自动在本地或者网络上搜索相关的歌词并建立关联
     * 本地搜索将硬编码为user.home文件夹下面的Lyrics文件夹
     * 以后改为可以手动设置.
     * @param info
     */
    public Lyric(final PlayListItem info) {
        this.offset = info.getOffset();
        this.info = info;
        this.during = info.getLength() * 1000;
        this.file = info.getLyricFile();
        log.log(Level.INFO, "\u4f20\u8fdb\u6765\u7684\u6b4c\u540d\u662f:{0}", info.toString());//传进来的歌名是
        //只要有关联好了的，就不用搜索了直接用就是了
        if (file != null && file.exists()) {
            log.log(Level.INFO, "\u4e0d\u7528\u627e\u4e86\uff0c\u76f4\u63a5\u5173\u8054\u5230\u7684\u6b4c\u8bcd\u662f\uff1a{0}", file);//不用找了，直接关联到的歌词是
            init(file);
            initDone = true;
        } else {
            //否则就起一个线程去找了，先是本地找，然后再是网络上找
            new Thread() {

                @Override
                public void run() {
                    doInit(info);
                    initDone = true;
                }
            }.start();
        }

    }

    /**
     * 读取某个指定的歌词文件,这个构造函数一般用于
     * 拖放歌词文件到歌词窗口时调用的,拖放以后,两个自动关联
     * @param file 歌词文件
     * @param info 歌曲信息
     */
    public Lyric(File file, PlayListItem info) {
        this.offset = info.getOffset();
        this.file = file;
        this.info = info;
        init(file);
        initDone = true;
    }

    /**
     * 根据歌词内容和播放项构造一个
     * 歌词对象
     * @param lyric 歌词内容
     * @param info 播放项
     */
    public Lyric(String lyric, PlayListItem info) {
        this.offset = info.getOffset();
        this.info = info;
        this.init(lyric);
        initDone = true;
    }

    private void doInit(PlayListItem info) {
        init(info);

        Sentence temp = null;
        //这个时候就要去网络上找了
        if (list.size() == 1) {
            temp = list.remove(0);
            try {
                String lyric = Util.getLyric(info);
                if (lyric != null) {
                    init(lyric);
                    saveLyric(lyric, info);
                } else {//如果网络也没有找到,就要加回去了
                    list.add(temp);
                }
            } catch (IOException ex) {
                Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);
                //如果抛了任何异常,也要加回去了
                list.add(temp);
            }
        }
    }

    /**
     * 把下载到的歌词保存起来,免得下次再去找
     * @param lyric 歌词内容
     * @param info 歌的信息
     */
    private void saveLyric(String lyric, PlayListItem info) {
        try {
            //如果歌手不为空,则以歌手名+歌曲名为最好组合
            String name = info.getFormattedName() + ".lrc";
//            File dir = new File(Config.HOME, "Lyrics" + File.separator);
            File dir = Config.getConfig().getSaveLyricDir();
            log.log(Level.INFO, "歌词保存地址：{0}",dir);
            dir.mkdirs();
            file = new File(dir, name);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "GBK"));
            bw.write(lyric);
            bw.close();
            info.setLyricFile(file);
            log.log(Level.INFO, "\u4fdd\u5b58\u5b8c\u6bd5,\u4fdd\u5b58\u5728:{0}", file);//保存完毕,保存在
        } catch (IOException exe) {
            log.log(Level.SEVERE, "保存歌词出错", exe);//保存歌词出错
        }
    }

    /**
     * 设置此歌词是否起用了,否则就不动了
     * @param b 是否起用
     */
    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    /**
     * 得到此歌词保存的地方
     * @return 文件
     */
    public File getLyricFile() {
        return file;
    }

    /**
     * 调整整体的时间,比如歌词统一快多少
     * 或者歌词统一慢多少,为正说明要快,为负说明要慢
     * @param time 要调的时间,单位是毫秒
     */
    public void adjustTime(int time) {
        //如果是只有一个显示的,那就说明没有什么效对的意义了,直接返回
        if (list.size() == 1) {
            return;
        }
        offset += time;
        info.setOffset(offset);
    }

    /**
     * 根据一个文件夹,和一个歌曲的信息
     * 从本地搜到最匹配的歌词
     * @param dir 目录
     * @param info 歌曲信息 
     * @return 歌词文件
     */
    private File getMathedLyricFile(File dir, PlayListItem info) {
        File matched = null;//已经匹配的文件
        File[] fs = dir.listFiles(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".lrc");
            }
        });
        for (File f : fs) {
            //全部匹配或者部分匹配都行
            if (matchAll(info, f) || matchSongName(info, f)) {
                matched = f;
                break;
            }
        }
        return matched;
    }

    /**
     * 根据歌的信息去初始化,这个时候
     * 可能在本地找到歌词文件,也可能要去网络上搜索了
     * @param info 歌曲信息
     */
    private void init(PlayListItem info) {
        File matched = null;
        for (File dir : Config.getConfig().getSearchLyricDirs()) {
            log.log(Level.FINE, "\u6b63\u5728\u641c\u7d22\u6587\u4ef6\u5939:{0}", dir);//正在搜索文件夹
            //得到歌曲信息后,先本地搜索,先搜索HOME文件夹
            //如果还不存在的话,那建一个目录,然后直接退出不管了
            if (!dir.exists()) {
                dir.mkdirs();
            }
            matched = getMathedLyricFile(dir, info);
            //当搜索到了,就退出
            if (matched != null) {
                break;
            }
        }
        log.info("找到的是:" + matched);
        if (matched != null && matched.exists()) {
            info.setLyricFile(matched);
            file = matched;
            init(matched);
        } else {
            init("");
        }
    }

    /**
     * 根据文件来初始化
     * @param file 文件
     */
    private void init(File file) {
        BufferedReader br = null;
        try {
            //br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp).append("\n");
            }
            init(sb.toString());
        } catch (IOException ex) {
            Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * 是否完全匹配,完全匹配是指直接对应到ID3V1的标签,
     * 如果一样,则完全匹配了,完全匹配的LRC的文件格式是:
     * 阿木 - 有一种爱叫放手.lrc
     * @param info 歌曲信息
     * @param file 侯选文件
     * @return 是否合格
     */
    private boolean matchAll(PlayListItem info, File file) {
        String name = info.getFormattedName();
        String fn = file.getName().substring(0, file.getName().lastIndexOf("."));
        return name.equals(fn);
    }

    /**
     * 是否匹配了歌曲名
     * @param info 歌曲信息
     * @param file 侯选文件
     * @return 是否合格
     */
    private boolean matchSongName(PlayListItem info, File file) {
        String name = info.getFormattedName();
        String rn = file.getName().substring(0, file.getName().lastIndexOf("."));
        return name.equalsIgnoreCase(rn) || info.getTitle().equalsIgnoreCase(rn);
    }

    /**
     * 最重要的一个方法，它根据读到的歌词内容
     * 进行初始化，比如把歌词一句一句分开并计算好时间
     * @param content 歌词内容
     */
    private void init(String content) {
        //如果歌词的内容为空,则后面就不用执行了
        //直接显示歌曲名就可以了
        if (content == null || content.trim().equals("")) {
            list.add(new Sentence(info.getFormattedName(), Integer.MIN_VALUE, Integer.MAX_VALUE));
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new StringReader(content));
            String temp = null;
            while ((temp = br.readLine()) != null) {
                parseLine(temp.trim());
            }
            br.close();
            //读进来以后就排序了
            Collections.sort(list, new Comparator<Sentence>() {

                public int compare(Sentence o1, Sentence o2) {
                    return (int) (o1.getFromTime() - o2.getFromTime());
                }
            });
            //处理第一句歌词的起始情况,无论怎么样,加上歌名做为第一句歌词,并把它的
            //结尾为真正第一句歌词的开始
            if (list.isEmpty()) {
                list.add(new Sentence(info.getFormattedName(), 0, Integer.MAX_VALUE));
                return;
            } else {
                Sentence first = list.get(0);
                list.add(0, new Sentence(info.getFormattedName(), 0, first.getFromTime()));
            }

            int size = list.size();
            for (int i = 0; i < size; i++) {
                Sentence next = null;
                if (i + 1 < size) {
                    next = list.get(i + 1);
                }
                Sentence now = list.get(i);
                if (next != null) {
                    now.setToTime(next.getFromTime() - 1);
                }
            }
            //如果就是没有怎么办,那就只显示一句歌名了
            if (list.size() == 1) {
                list.get(0).setToTime(Integer.MAX_VALUE);
            } else {
                Sentence last = list.get(list.size() - 1);
                last.setToTime(info == null ? Integer.MAX_VALUE : info.getLength() * 1000 + 1000);
            }
        } catch (IOException ex) {
            Logger.getLogger(Lyric.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 分析出整体的偏移量
     * @param str 包含内容的字符串
     * @return 偏移量，当分析不出来，则返回最大的正数
     */
    private int parseOffset(String str) {
        String[] ss = str.split("\\:");
        if (ss.length == 2) {
            if (ss[0].equalsIgnoreCase("offset")) {
                int os = Integer.parseInt(ss[1]);
                System.err.println("整体的偏移量：" + os);
                return os;
            } else {
                return Integer.MAX_VALUE;
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 分析这一行的内容，根据这内容
     * 以及标签的数量生成若干个Sentence对象
     * 当此行中的时间标签分布不在一起时，也要能分析出来
     * 所以更改了一些实现
     * 20080824更新
     * @param line 这一行
     */
    private void parseLine(String line) {
        if (line.equals("")) {
            return;
        }
        Matcher matcher = pattern.matcher(line);
        List<String> temp = new ArrayList<String>();
        int lastIndex = -1;//最后一个时间标签的下标
        int lastLength = -1;//最后一个时间标签的长度
        while (matcher.find()) {
            String s = matcher.group();
            int index = line.indexOf("[" + s + "]");
            if (lastIndex != -1 && index - lastIndex > lastLength + 2) {
                //如果大于上次的大小，则中间夹了别的内容在里面
                //这个时候就要分段了
                String content = line.substring(lastIndex + lastLength + 2, index);
                for (String str : temp) {
                    long t = parseTime(str);
                    if (t != -1) {
                        list.add(new Sentence(content, t));
                    }
                }
                temp.clear();
            }
            temp.add(s);
            lastIndex = index;
            lastLength = s.length();
        }
        //如果列表为空，则表示本行没有分析出任何标签
        if (temp.isEmpty()) {
            return;
        }
        try {
            int length = lastLength + 2 + lastIndex;
            String content = line.substring(length > line.length() ? line.length() : length);
            if (Config.getConfig().isCutBlankChars()) {
                content = content.trim();
            }
            //当已经有了偏移量的时候，就不再分析了
            if (content.equals("") && offset == 0) {
                for (String s : temp) {
                    int of = parseOffset(s);
                    if (of != Integer.MAX_VALUE) {
                        offset = of;
                        info.setOffset(offset);
                        break;//只分析一次
                    }
                }
                return;
            }
            for (String s : temp) {
                long t = parseTime(s);
                if (t != -1) {
                    list.add(new Sentence(content, t));
                }
            }
        } catch (Exception exe) {
        }
    }

    /**
     * 把如00:00.00这样的字符串转化成
     * 毫秒数的时间，比如 
     * 01:10.34就是一分钟加上10秒再加上340毫秒
     * 也就是返回70340毫秒
     * @param time 字符串的时间
     * @return 此时间表示的毫秒
     */
    private long parseTime(String time) {
        String[] ss = time.split("\\:|\\.");
        //如果 是两位以后，就非法了
        if (ss.length < 2) {
            return -1;
        } else if (ss.length == 2) {//如果正好两位，就算分秒
            try {
                //先看有没有一个是记录了整体偏移量的
                if (offset == 0 && ss[0].equalsIgnoreCase("offset")) {
                    offset = Integer.parseInt(ss[1]);
                    info.setOffset(offset);
                    System.err.println("整体的偏移量：" + offset);
                    return -1;
                }
                int min = Integer.parseInt(ss[0]);
                int sec = Integer.parseInt(ss[1]);
                if (min < 0 || sec < 0 || sec >= 60) {
                    throw new RuntimeException("数字不合法!");
                }
                return (min * 60 + sec) * 1000L;
            } catch (RuntimeException exe) {
                return -1;
            }
        } else if (ss.length == 3) {//如果正好三位，就算分秒，十毫秒
            try {
                int min = Integer.parseInt(ss[0]);
                int sec = Integer.parseInt(ss[1]);
                int mm = Integer.parseInt(ss[2]);
                if (min < 0 || sec < 0 || sec >= 60 || mm < 0 || mm > 99) {
                    throw new RuntimeException("数字不合法!");
                }
                return (min * 60 + sec) * 1000L + mm * 10;
            } catch (RuntimeException exe) {
                return -1;
            }
        } else {//否则也非法
            return -1;
        }
    }

    /**
     * 设置其显示区域的高度
     * @param height 高度
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * 设置其显示区域的宽度
     * @param width 宽度
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 设置时间,设置的时候，要把整体的偏移时间算上
     * @param time 时间
     */
    public void setTime(long time) {
        if (!isMoving) {
            tempTime = this.time = time + offset;
        }
    }

    /**
     * 得到是否初始化完成了
     * @return 是否完成
     */
    public boolean isInitDone() {
        return initDone;
    }

    private void drawKaraoke(Graphics2D gd, Sentence now, int x, int y, long t) {
        int nowWidth = now.getContentWidth(gd);
        Color gradient = null;
        //如果要渐入渐出才去求中间色，否则直接用高亮色画
        if (Config.getConfig().isLyricShadow()) {
            gradient = now.getBestInColor(Config.getConfig().getLyricHilight(), Config.getConfig().getLyricForeground(), t);
        } else {
            gradient = Config.getConfig().getLyricHilight();
        }
        if (Config.getConfig().isKaraoke()) {
            float f = (t - now.getFromTime()) * 1.0f / (now.getToTime() - now.getFromTime());
            if (f > 0.98f) {
                f = 0.98f;
            }
            if (x == 0) {
                x = 1;
            }
            if (nowWidth == 0) {
                nowWidth = 1;
            }
            gd.setPaint(new LinearGradientPaint(x, y, x + nowWidth, y, new float[]{f, f + 0.01f}, new Color[]{gradient, Config.getConfig().getLyricForeground()}));
        } else {
            gd.setPaint(gradient);
        }

        Util.drawString(gd, now.getContent(), x, y);
    }

    /**
     * 自力更生，画出自己在水平方向的方法
     * 这个做是为了更方便地把歌词显示在
     * 任何想显示的地方
     * @param g 画笔
     */
    public synchronized void drawH(Graphics g) {
        if (!enabled) {
            Sentence sen = new Sentence(info.getFormattedName());
            int x = (width - sen.getContentWidth(g)) / 2;
            int y = (height - sen.getContentHeight(g) + Config.getConfig().getV_SPACE()) / 2;
            g.setColor(Config.getConfig().getLyricHilight());
            Util.drawString(g, sen.getContent(), x, y);
            return;
        }
        //首先看是不是初始化完毕了
        if (!initDone) {
            Sentence temp = new Sentence("正在搜索歌词");
            int x = (width - temp.getContentWidth(g)) / 2;
            int y = (height - temp.getContentHeight(g)) / 2;
            g.setColor(Config.getConfig().getLyricHilight());
            Util.drawString(g, temp.getContent(), x, y);
            return;
        }
        //如果只存在一句的话,那就不要浪费那么多计算的时候了
        //直接画在中间就可以了
        if (list.size() == 1) {
            Sentence sen = list.get(0);
            int x = (width - sen.getContentWidth(g)) / 2;
            int y = (height - sen.getContentHeight(g) + Config.getConfig().getV_SPACE()) / 2;
            g.setColor(Config.getConfig().getLyricHilight());
            Util.drawString(g, sen.getContent(), x, y);
        } else {
            //取一个time的副本，以防止在一个方法里面产生两种time的情况
            long t = tempTime;
            Graphics2D gd = (Graphics2D) g;
            int index = getNowSentenceIndex(t);
            if (!isMoving) {
                currentIndex = index;
            }
            if (index == -1) {
                Sentence sen = new Sentence(info.getFormattedName(), Integer.MIN_VALUE, Integer.MAX_VALUE);
                int x = (width - sen.getContentWidth(g) - Config.getConfig().getH_SPACE()) / 2;
                int y = (height - sen.getContentHeight(g) + Config.getConfig().getV_SPACE()) / 2;
                g.setColor(Config.getConfig().getLyricHilight());
                Util.drawString(g, sen.getContent(), x, y);
                return;
            }
            Sentence now = list.get(index);
            int nowWidth = now.getContentWidth(g) + Config.getConfig().getH_SPACE();
            int x = (width) / 2 - now.getHIncrease(g, t);
            int y = (height - now.getContentHeight(g)) / 2;
            this.drawKaraoke(gd, now, x, y, t);
            gd.setPaint(Config.getConfig().getLyricForeground());
            int tempX = x;
            //画出中间那句之前的句子
            for (int i = index - 1; i >= 0; i--) {
                Sentence sen = list.get(i);
                int wid = sen.getContentWidth(g) + Config.getConfig().getH_SPACE();
                tempX = tempX - wid;
                if (tempX + wid < 0) {
                    break;
                }
                if (Config.getConfig().isLyricShadow()) {
                    if (i == index - 1) {
                        gd.setPaint(sen.getBestOutColor(Config.getConfig().getLyricHilight(),
                                Config.getConfig().getLyricForeground(), time));
                    } else {
                        gd.setPaint(Config.getConfig().getLyricForeground());
                    }
                }
                Util.drawString(g, sen.getContent(), tempX, y);
            }
            gd.setPaint(Config.getConfig().getLyricForeground());
            tempX = x;
            int tempWidth = nowWidth;
            //画出中间那句之后的句子
            for (int i = index + 1; i < list.size(); i++) {
                Sentence sen = list.get(i);
                tempX = tempX + tempWidth;
                if (tempX > width) {
                    break;
                }
                Util.drawString(g, sen.getContent(), tempX, y);
                tempWidth = sen.getContentWidth(g) + Config.getConfig().getH_SPACE();
            }
        }
    }

    /**
     * 得到这批歌词里面,最长的那一句的长度
     * @param g
     * @return 最长的长度
     */
    public int getMaxWidth(Graphics g) {
        int max = 0;
        for (Sentence sen : list) {
            int w = sen.getContentWidth(g);
            if (w > max) {
                max = w;
            }
        }
        return max;
    }

    /**
     * 得到一句话的X座标，因为可能对齐方式有
     * 多种，针对每种对齐方式，X的座标不一
     * 定一样。
     * @param g 画笔
     * @param sen 要求的句子
     * @return 本句的X座标
     */
    private int getSentenceX(Graphics g, Sentence sen) {
        int x = 0;
        int i = Config.getConfig().getLyricAlignMode();
        switch (i) {
            case Config.LYRIC_CENTER_ALIGN:
                x = (width - sen.getContentWidth(g)) / 2;
                break;
            case Config.LYRIC_LEFT_ALIGN:
                x = 0;
                break;
            case Config.LYRIC_RIGHT_ALIGN:
                x = width - sen.getContentWidth(g);
                break;
            default://默认情况还是中间对齐
                x = (width - sen.getContentWidth(g)) / 2;
                break;
        }
        return x;
    }

    /**
     * 画出自己在垂直方向上的过程
     * @param g 画笔
     */
    public synchronized void drawV(Graphics g) {
        if (!enabled) {
            Sentence sen = new Sentence(info.getFormattedName());
            int x = (width - sen.getContentWidth(g)) / 2;
            int y = (height - sen.getContentHeight(g) + Config.getConfig().getV_SPACE()) / 2;
            g.setColor(Config.getConfig().getLyricHilight());
            Util.drawString(g, sen.getContent(), x, y);
            return;
        }
        //首先看是不是初始化完毕了
        if (!initDone) {
            Sentence temp = new Sentence("正在搜索歌词");
            int x = getSentenceX(g, temp);
            int y = (height - temp.getContentHeight(g)) / 2;
            g.setColor(Config.getConfig().getLyricHilight());
            Util.drawString(g, temp.getContent(), x, y);
            return;
        }
        //如果只存在一句的话,那就不要浪费那么多计算的时候了
        //直接画在中间就可以了
        if (list.size() == 1) {
            Sentence sen = list.get(0);
            int x = getSentenceX(g, sen);
            int y = (height - sen.getContentHeight(g)) / 2;
            g.setColor(Config.getConfig().getLyricHilight());
            Util.drawString(g, sen.getContent(), x, y);
        } else {
            long t = tempTime;
            Graphics2D gd = (Graphics2D) g;
            int index = getNowSentenceIndex(t);
            if (!isMoving) {
                currentIndex = index;
            }
            if (index == -1) {
                Sentence sen = new Sentence(info.getFormattedName(), Integer.MIN_VALUE, Integer.MAX_VALUE);
                int x = getSentenceX(g, sen);
                int y = (height - sen.getContentHeight(g)) / 2;
                gd.setPaint(Config.getConfig().getLyricHilight());
                Util.drawString(g, sen.getContent(), x, y);
                return;
            }
            Sentence now = list.get(index);
            //先求出中间的最基准的纵座标
            int y = (height + now.getContentHeight(g)) / 2 - now.getVIncrease(g, t);
            int x = getSentenceX(g, now);
            this.drawKaraoke(gd, now, x, y, t);
            gd.setColor(Config.getConfig().getLyricForeground());
            //然后再画上面的部份以及下面的部份
            //这样就可以保证正在唱的歌词永远在正中间显示
            int tempY = y;
            //画出本句之前的句子
            for (int i = index - 1; i >= 0; i--) {
                Sentence sen = list.get(i);
                int x1 = getSentenceX(g, sen);
                tempY = tempY - sen.getContentHeight(g) - Config.getConfig().getV_SPACE();
                if (tempY + sen.getContentHeight(g) < 0) {
                    break;
                }
                if (Config.getConfig().isLyricShadow()) {
                    if (i == index - 1) {
                        gd.setColor(sen.getBestOutColor(Config.getConfig().getLyricHilight(),
                                Config.getConfig().getLyricForeground(), time));
                    } else {
                        gd.setColor(Config.getConfig().getLyricForeground());
                    }
                }
                Util.drawString(g, sen.getContent(), x1, tempY);
            }
            gd.setColor(Config.getConfig().getLyricForeground());
            tempY = y;
            //画出本句之后的句子 
            for (int i = index + 1; i < list.size(); i++) {
                Sentence sen = list.get(i);
                int x1 = getSentenceX(g, sen);
                tempY = tempY + sen.getContentHeight(g) + Config.getConfig().getV_SPACE();
                if (tempY > height) {
                    break;
                }
                Util.drawString(g, sen.getContent(), x1, tempY);
            }
        }
    }

    /**
     * 得到当前正在播放的那一句的下标
     * 不可能找不到，因为最开头要加一句
     * 自己的句子 ，所以加了以后就不可能找不到了
     * @return 下标
     */
    private int getNowSentenceIndex(long t) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isInTime(t)) {
                return i;
            }
        }
//        throw new RuntimeException("竟然出现了找不到的情况！");
        return -1;
    }

    /**
     * 水平移动多少个象素,这个方法是给面板调用的
     * 移动了这些象素以后,要马上算出这个象素所
     * 对应的时间是多少,要注意时间超出的情况
     * @param length
     * @param g 画笔,因为对于每一个画笔长度不一样的
     */
    public void moveH(int length, Graphics g) {
        if (list.size() == 1 || !enabled) {
            return;
        }
        //如果长度是大于0的,则说明是正向移动,快进
        if (length > 0) {
            Sentence now = list.get(currentIndex);
            int nowWidth = now.getContentWidth(g);
            float f = (time - now.getFromTime()) * 1.0f / (now.getToTime() - now.getFromTime());
            //先算出当前的这一句还剩多少长度了
            int rest = (int) ((1 - f) * nowWidth);
            long timeAdd = 0;//要加多少时间
            //如果剩下的长度足够了,那是最好,马上就可以返回了
            if (rest > length) {
                timeAdd = now.getTimeH(length, g);
            } else {
                timeAdd = now.getTimeH(rest, g);
                for (int i = currentIndex; i < list.size(); i++) {
                    Sentence sen = list.get(i);
                    int len = sen.getContentWidth(g);
                    //如果加上下一句的长度还不够,就把时间再加,继续下一句
                    if (len + rest < length) {
                        timeAdd += sen.getDuring();
                        rest += len;
                    } else {
                        timeAdd += sen.getTimeH(length - rest, g);
                        break;
                    }
                }
            }
            tempTime = time + timeAdd;
            checkTempTime();
        } else {//否则就是反向移动,要快退了
            length = 0 - length;//取它的正数
            Sentence now = list.get(currentIndex);
            int nowWidth = now.getContentWidth(g);
            float f = (time - now.getFromTime()) * 1.0f / (now.getToTime() - now.getFromTime());
            //先算出当前的这一句已经用了多少长度了
            int rest = (int) (f * nowWidth);
            long timeAdd = 0;//要加多少时间
            //如果剩下的长度足够了,那是最好,马上就可以返回了
            if (rest > length) {
                timeAdd = now.getTimeH(length, g);
            } else {
                timeAdd = now.getTimeH(rest, g);
                for (int i = currentIndex; i > 0; i--) {
                    Sentence sen = list.get(i);
                    int len = sen.getContentWidth(g);
                    //如果加上下一句的长度还不够,就把时间再加,继续下一句
                    if (len + rest < length) {
                        timeAdd += sen.getDuring();
                        rest += len;
                    } else {
                        timeAdd += sen.getTimeH(length - rest, g);
                        break;
                    }
                }
            }
            tempTime = time - timeAdd;
            checkTempTime();
        }
    }

    /**
     * 竖直移动多少个象素,这个方法是给面板调用的
     * 移动了这些象素以后,要马上算出这个象素所
     * 对应的时间是多少,要注意时间超出的情况
     * @param length
     * @param g 画笔,因为对于每一个画笔长度不一样的
     */
    public void moveV(int length, Graphics g) {
        if (list.size() == 1 || !enabled) {
            return;
        }
        //如果长度是大于0的,则说明是正向移动,快进
        if (length > 0) {
            Sentence now = list.get(currentIndex);
            int nowHeight = now.getContentHeight(g);
            float f = (time - now.getFromTime()) * 1.0f / (now.getToTime() - now.getFromTime());
            //先算出当前的这一句还剩多少长度了
            int rest = (int) ((1 - f) * nowHeight);
            long timeAdd = 0;//要加多少时间
            //如果剩下的长度足够了,那是最好,马上就可以返回了
            if (rest > length) {
                timeAdd = now.getTimeV(length, g);
            } else {
                timeAdd = now.getTimeV(rest, g);
                for (int i = currentIndex; i < list.size(); i++) {
                    Sentence sen = list.get(i);
                    int len = sen.getContentHeight(g);
                    //如果加上下一句的长度还不够,就把时间再加,继续下一句
                    if (len + rest < length) {
                        timeAdd += sen.getDuring();
                        rest += len;
                    } else {
                        timeAdd += sen.getTimeV(length - rest, g);
                        break;
                    }
                }
            }
            tempTime = time + timeAdd;
            checkTempTime();
        } else {//否则就是反向移动,要快退了
            length = 0 - length;//取它的正数
            Sentence now = list.get(currentIndex);
            int nowHeight = now.getContentHeight(g);
            float f = (time - now.getFromTime()) * 1.0f / (now.getToTime() - now.getFromTime());
            //先算出当前的这一句已经用了多少长度了
            int rest = (int) (f * nowHeight);
            long timeAdd = 0;//要加多少时间
            //如果剩下的长度足够了,那是最好,马上就可以返回了
            if (rest > length) {
                timeAdd = now.getTimeV(length, g);
            } else {
                timeAdd = now.getTimeV(rest, g);
                for (int i = currentIndex; i > 0; i--) {
                    Sentence sen = list.get(i);
                    int len = sen.getContentHeight(g);
                    //如果加上下一句的长度还不够,就把时间再加,继续下一句
                    if (len + rest < length) {
                        timeAdd += sen.getDuring();
                        rest += len;
                    } else {
                        timeAdd += sen.getTimeV(length - rest, g);
                        break;
                    }
                }
            }
            tempTime = time - timeAdd;
            checkTempTime();
        }
    }

    /**
     * 是否能拖动,只有有歌词才可以被拖动,否则没有意义了
     * @return 能否拖动
     */
    public boolean canMove() {
        return list.size() > 1 && enabled;
    }

    /**
     * 得到当前的时间,一般是由显示面板调用的
     * @return 
     */
    public long getTime() {
        return tempTime;
    }

    /**
     * 在对tempTime做了改变之后,检查一下它的
     * 值,看是不是在有效的范围之内
     */
    private void checkTempTime() {
        if (tempTime < 0) {
            tempTime = 0;
        } else if (tempTime > during) {
            tempTime = during;
        }
    }

    /**
     * 告诉歌词,要开始移动了,
     * 在此期间,所有对歌词的直接的时间设置都不理会
     */
    public void startMove() {
        isMoving = true;
    }

    /**
     * 告诉歌词拖动完了,这个时候的时间改
     * 变要理会,并做更改
     */
    public void stopMove() {
        isMoving = false;
    }

    public static void main(String[] args) {
    }
}
