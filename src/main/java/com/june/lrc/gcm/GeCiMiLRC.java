/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.june.lrc.gcm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.june.lrc.ILrcDownload;
import com.june.lrc.bean.Lyric;
import com.june.lrc.bean.Lyrics;
import com.june.lrc.comm.LRCUtil;

/**
 *
 * @author HappyLove
 */
public class GeCiMiLRC implements ILrcDownload {
	
    private static final Logger log = Logger.getLogger(GeCiMiLRC.class.getName());

    public boolean download(String title, String artist) {
        log.log(Level.OFF, "Not supported yet.");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getLrcContent(String title, String artist) throws IOException {
        //取第一条数据
        String url = null;
        try {
            log.log(Level.INFO, "title:{0},artist:{1}", new String[]{title,artist});
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

    public Lyrics getLyrics(String title, String artist) throws Exception {
        return LRCUtil.getGCMLyrics(title, artist);
    }

}
