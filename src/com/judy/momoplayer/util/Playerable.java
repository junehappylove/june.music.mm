/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.judy.momoplayer.util;

import com.judy.momoplayer.lyric.Lyric;
import com.judy.momoplayer.lyric.LyricUI;
import com.judy.momoplayer.playlist.PlayListItem;
import javax.swing.JFrame;

/**
 * 所有播放器都应该实现的接口
 * @author hadeslee
 */
public interface Playerable {
    public void setLyric(Lyric ly);
    public void setTime(long time);
    public void setShowLyric(boolean b);
    public JFrame getTopParent();
    public void play();
    public void pause();
    public void stop();
    public void nextSong();
    public void previousSong();
    public PlayListItem getCurrentItem();
    public Loader getLoader();
    public LyricUI getLyricUI();
    public long getTime();
}
