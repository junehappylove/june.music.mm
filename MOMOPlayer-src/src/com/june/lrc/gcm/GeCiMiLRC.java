/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.june.lrc.gcm;

import com.june.lrc.ILrcDownload;
import com.june.lrc.bean.Lyric;
import com.june.lrc.bean.Lyrics;
import com.june.lrc.comm.LRCConstants;
import com.june.lrc.comm.LRCUtil;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HappyLove
 */
public class GeCiMiLRC implements ILrcDownload {

    public boolean download(String title, String artist) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getLrcContent(String title, String artist) {
        //去第一条数据
        String url = null;
        try {
            //url = MessageFormat.format(LRCConstants.GCM_URL_, LRCUtil.$(title), LRCUtil.$(artist));
            System.out.println("title:"+title+",artist:"+artist);
            Lyrics lyrics = getLyrics(title, artist);
            if (!"0".equals(lyrics.getCount())) {
                Lyric lrc = lyrics.getResult() == null ? null : lyrics.getResult().get(0);
                url = lrc == null ? null : lrc.getLrc();
            }
        } catch (Exception ex) {
            Logger.getLogger(GeCiMiLRC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return LRCUtil.getContentFormWeb(url);
    }

    public Lyrics getLyrics(String title, String artist) {
        Lyrics lyrics = null;
        lyrics = LRCUtil.getGCMLyrics(title, artist);
        return lyrics;
    }

}
