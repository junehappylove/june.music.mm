/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.june.lrc.baidu;

import com.june.lrc.ILrcDownload;
import com.june.lrc.comm.LRCUtil;
import com.june.lrc.bean.Lyrics;

/**
 * 百度下载歌词类
 *
 * @author HappyLove
 */
public class BaiDuLRC implements ILrcDownload {

    public boolean download(String name, String author) {
        return LRCUtil.getLRCFile(name, author) != null;
    }

    public String getLrcContent(String name, String author) throws Exception{
        return LRCUtil.getLRCContent(name, author);
    }

    public Lyrics getLyrics(String title, String artist) throws Exception{
        throw new UnsupportedOperationException("Not supported yet."); 
    }

}
