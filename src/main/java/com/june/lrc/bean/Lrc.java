/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.june.lrc.bean;

import com.june.lrc.comm.LRCConstants;
import com.june.lrc.comm.LRCUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HappyLove
 */
public class Lrc {

    private static final Logger log = Logger.getLogger(LRCUtil.class.getName());
    //private String count = "1";// this xml get how many musics
    private String encode;
    private String decode;
    private String type;
    private String lrcid;
    private String flag;

    private String lrcDownloadUrl; //歌词的下载地址
    private String musicDownloadUrl;//歌曲的下载地址
    
    private String lrcContent;//歌词的内容

    /**
     * @return the encode
     */
    public String getEncode() {
        return encode;
    }

    /**
     * @param encode the encode to set
     */
    public void setEncode(String encode) {
        this.encode = encode;
    }

    /**
     * @return the decode
     */
    public String getDecode() {
        return decode;
    }

    /**
     * @param decode the decode to set
     */
    public void setDecode(String decode) {
        this.decode = decode;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the lrcid
     */
    public String getLrcid() {
        return lrcid;
    }

    /**
     * @param lrcid the lrcid to set
     */
    public void setLrcid(String lrcid) {
        this.lrcid = lrcid;
    }

    /**
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * 获取歌曲的歌词文件的下载地址
     *
     * @return the lrcDownloadUrl
     */
    public String getLrcDownloadUrl() {
        if (this.caluteRoot(lrcid) != null) {
            lrcDownloadUrl = LRCConstants.BD_LRC_URL
                    + this.caluteRoot(lrcid) + "/" + lrcid + ".lrc";
            log.log(Level.INFO, "歌曲的歌词地址是：{0}",lrcDownloadUrl);
        } else {
            log.log(Level.INFO, "暂无此歌曲的歌词。");
            lrcDownloadUrl = null;
        }
        return lrcDownloadUrl;
    }

    /**
     * 获取歌曲的下载地址
     *
     * @return the musicDownloadUrl
     */
    public String getMusicDownloadUrl() {
        int endIndex = encode.lastIndexOf("/");
        encode = encode.substring(0, endIndex);
        musicDownloadUrl = encode + "/" + decode;
        return musicDownloadUrl;
    }

    /**
     * 根据歌词的名称计算前级目录 例如： 歌词名称为45687.mp3，最后计算为 456
     *
     * @param fileName
     * @return
     */
    public String caluteRoot(String fileName) {
        if (fileName.length() > 4) {
            fileName = fileName.substring(0, fileName.length()-2);
        } else {
            fileName = null;
        }
        //int root = Integer.parseInt(fileName);
        return fileName;
    }

    public static void main(String[] args) {
        Lrc lrc = new Lrc();
        lrc.setDecode("111111.mp3");
        lrc.setEncode("http://wwww.sadfsda.adcom/22222/33333/4/7777777");
        lrc.setLrcid("78965");
        System.out.println(lrc.getLrcDownloadUrl());
        System.out.println(lrc.getMusicDownloadUrl());
    }

    @Override
    public String toString() {
        return "encode=" + this.encode + "\ndecode=" + decode + "\ntype=" + type + "\nlrcid=" + lrcid + "\nflag=" + flag
                + "\nlrcurl=" + this.getLrcDownloadUrl() + "\nmusicurl=" + this.getMusicDownloadUrl() + "\n";
    }

    /**
     * @return the lrcContent
     */
    public String getLrcContent() {
        return lrcContent;
    }

    /**
     * @param lrcContent the lrcContent to set
     */
    public void setLrcContent(String lrcContent) {
        this.lrcContent = lrcContent;
    }

}
