/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.june.lrc.comm;

/**
 *
 * @author HappyLove
 */
public class LRCConstants {

    /**
     * Use like this:
     * http://box.zhangmen.baidu.com/x?op=12&count=1&title=大约在冬季$$齐秦$$$$
     */
    public static final String BD_SEARCH_URI = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=";

    /**
     * like : http://box.zhangmen.baidu.com/bdlrc/147/14706.lrc
     */
    public static final String BD_LRC_URL = "http://box.zhangmen.baidu.com/bdlrc/";

    /**
     * like: 'http://geci.me/api/lyric/海阔天空/Beyond'
     */
    public static final String GCM_URL = "http://geci.me/api/lyric/";
    /**
     * like: 'http://geci.me/api/lyric/海阔天空/Beyond'
     */
    public static final String GCM_URL_ = "http://geci.me/api/lyric/{0}/{1}";

    /**
     * 根据歌手编号获取歌手信息(暂时只有歌手名)
     *
     * 请求地址
     *
     * http://geci.me/api/artist/:artist_id
     */
    public static final String GCM_GET_ARTIST = "http://geci.me/api/artist/";

    /**
     * 根据歌手编号获取歌手信息(暂时只有歌手名)
     *
     * 请求地址
     *
     * http://geci.me/api/artist/:artist_id
     */
    public static final String GCM_GET_ARTIST_ = "http://geci.me/api/artist/{0}";

    /**
     * 根据专辑编号获取专辑封面URL
     *
     * 请求地址
     *
     * http://geci.me/api/cover/:album_id
     */
    public static final String GCM_GET_ALBUM = "http://geci.me/api/cover/";

    /**
     * 根据专辑编号获取专辑封面URL
     *
     * 请求地址
     *
     * http://geci.me/api/cover/:album_id
     */
    public static final String GCM_GET_ALBUM_ = "http://geci.me/api/cover/{0}";
    
    
    public static final String GBK = "GBK";
    
    public static final String UTF8="UTF-8";
}
