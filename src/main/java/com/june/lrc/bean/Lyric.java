/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.june.lrc.bean;

/**
 * 针对歌词米的歌词对象
 * @author HappyLove
 */
public class Lyric {
    //根据歌词米的json格式
    private String aid;//albem 封面id
    private String lrc;//歌词下载地址
    private String sid;//song id 歌曲id
    private String artist_id;//歌手id
    private String song;//歌曲名称

    public Lyric(){}
    
    public Lyric(String aid,String lrcUri,String sid,String artistid,String songName){
        this.aid = aid;
        this.artist_id = artistid;
        this.lrc = lrcUri;
        this.sid = sid;
        this.song = songName;
    }
    
    /**
     * @return the aid
     */
    public String getAid() {
        return aid;
    }

    /**
     * @param aid the aid to set
     */
    public void setAid(String aid) {
        this.aid = aid;
    }

    /**
     * @return the lrc
     */
    public String getLrc() {
        return lrc;
    }

    /**
     * @param lrc the lrc to set
     */
    public void setLrc(String lrc) {
        this.lrc = lrc;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return sid;
    }

    /**
     * @param sid the sid to set
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * @return the artist_id
     */
    public String getArtist_id() {
        return artist_id;
    }

    /**
     * @param artist_id the artist_id to set
     */
    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    /**
     * @return the song
     */
    public String getSong() {
        return song;
    }

    /**
     * @param song the song to set
     */
    public void setSong(String song) {
        this.song = song;
    }
    
}
