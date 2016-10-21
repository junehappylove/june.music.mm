/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.june.lrc;

import com.june.lrc.bean.Lyrics;

/**
 * download lrc interface
 *
 * @author HappyLove
 */
public interface ILrcDownload {

    /**
     *
     * @param title 歌曲名称
     * @param artist 歌手名称
     * @return 歌词是否下载成功
     */
    boolean download(String title, String artist);

    /**
     * 获取一首歌的内容
     *
     * @param title 歌曲名称
     * @param artist 歌手名
     * @return
     */
    String getLrcContent(String title, String artist);

    /**
     * 根据歌曲名和歌手名获取歌词对象列表
     *
     * @param title
     * @param artist
     * @return
     */
    Lyrics getLyrics(String title, String artist);
}
