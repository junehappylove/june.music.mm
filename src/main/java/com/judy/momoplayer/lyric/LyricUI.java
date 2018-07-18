/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.lyric;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.MultiImageBorder;
import com.judy.momoplayer.util.Playerable;
import com.judy.momoplayer.util.Util;

/**
 *
 * @author judy
 */
public class LyricUI extends JPanel {

    private static final long serialVersionUID = 20071214L;
    @SuppressWarnings("unused")
	private Config config;
    private LyricPanel lp;//一个实际显示歌词的面板
    private Playerable player;
    private MultiImageBorder border;//即是边界,又是监听器
    public LyricUI() {
        super(new BorderLayout());
        this.setPreferredSize(new Dimension(285, 465));
        this.setMinimumSize(new Dimension(285, 50));
    }

    public void setPlayer(Playerable player) {
        this.player = player;
    }
    public void setParent(Component parent){
        border.setParent(parent);
    }
    public void setBorderEnabled(boolean b){
        if(b){
            this.setBorder(border);
        }else{
            this.setBorder(null);
        }
    }
    public void loadUI(Component parent, Config config) {
        this.config = config;
        border = new MultiImageBorder(parent, config);
        border.setCorner1(Util.getImage("lyric/corner1.png"));
        border.setCorner2(Util.getImage("playlist/corner2.png"));
        border.setCorner3(Util.getImage("playlist/corner3.png"));
        border.setCorner4(Util.getImage("playlist/corner4.png"));
        border.setTop(Util.getImage("playlist/top.png"));
        border.setBottom(Util.getImage("playlist/bottom.png"));
        border.setLeft(Util.getImage("playlist/left.png"));
        border.setRight(Util.getImage("playlist/right.png"));
        this.setBorder(border);
        this.addMouseListener(border);
        this.addMouseMotionListener(border);
        lp = new LyricPanel(player);
        lp.setConfig(config);
        this.add(lp, BorderLayout.CENTER);
    }

    public void setShowLogo(boolean b) {
        lp.setShowLogo(b);
    }

    /**
     * 设置播放列表
     * @param pl 播放列表
     */
    public void setPlayList(Playerable pl) {
        lp.setPlayList(player);
    }

    public LyricPanel getLyricPanel() {
        return lp;
    }

    /**
     * 设置一个新的歌词对象,此方法可能会被
     * PlayList调用
     * @param ly 歌词
     */
    public void setLyric(Lyric ly) {
        lp.setLyric(ly);
    }

    public void pause() {
        lp.pause();
    }

    public void start() {
        lp.start();
    }
}
