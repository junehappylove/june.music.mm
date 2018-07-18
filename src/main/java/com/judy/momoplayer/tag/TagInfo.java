/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.tag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Vector;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 标签信息
 * TagInfo <br>
 * 
 * @author 王俊伟 wjw.happy.love@163.com
 * @blog https://www.github.com/junehappylove
 * @date 2017年1月12日 下午9:13:58
 * @version 1.0.0
 */
public interface TagInfo extends Serializable {

    public void load(InputStream input) throws IOException, UnsupportedAudioFileException;

    public void load(URL input) throws IOException, UnsupportedAudioFileException;

    public void load(File input) throws IOException, UnsupportedAudioFileException;

    /**
     * Get Sampling Rate
     *
     * @return sampling rate
     */
    public int getSamplingRate();

    /**
     * Get Nominal Bitrate
     *
     * @return bitrate in bps
     */
    public int getBitRate();

    /**
     * Get channels.
     *
     * @return channels
     */
    public int getChannels();

    /**
     * Get play time in seconds.
     *
     * @return play time in seconds
     */
    public long getPlayTime();

    /**
     * Get the title of the song.
     *
     * @return the title of the song
     */
    public String getTitle();

    /**
     * Get the artist that performed the song
     *
     * @return the artist that performed the song
     */
    public String getArtist();

    /**
     * Get the name of the album upon which the song resides
     *
     * @return the album name
     */
    public String getAlbum();

    /**
     * Get the track number of this track on the album
     *
     * @return the track number
     */
    public String getTrack();

    /**
     * Get the genre string of the music
     *
     * @return the genre string
     */
    public String getGenre();

    /**
     * Get the year the track was released
     *
     * @return the year the track was released
     */
    public String getYear();

    /**
     * Get any comments provided about the song
     *
     * @return the comments
     */
    public Vector<String> getComment();

    /**
     * 得到某种音乐的格式,比如MP3,FLAC,等等 
     * @return 格式
     */
    public String getType();
}
