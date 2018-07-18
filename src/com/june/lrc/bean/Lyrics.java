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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Lyric> getResult() {
        return result;
    }

    public void setResult(List<Lyric> result) {
        this.result = result;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
