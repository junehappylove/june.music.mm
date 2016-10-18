/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.june.lrc.bean;

import java.util.List;

/**
 * 歌词迷的歌词类
 *
 * @author HappyLove
 */
public class Lyrics {

    private String count;//检索到的数目
    private String code;
    private List<Lyric> result;//歌词列表结果
    private String artist;//歌手名
    private String title;//歌曲名

    public Lyrics() {
    }

    public Lyrics(String count, String code, List<Lyric> result) {
        this.code = code;
        this.count = count;
        this.result = result;
    }

    /**
     * @return the count
     */
    public String getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the result
     */
    public List<Lyric> getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(List<Lyric> result) {
        this.result = result;
    }

    /**
     * @return the artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * @param artist the artist to set
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
