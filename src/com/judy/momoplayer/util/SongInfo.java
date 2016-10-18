/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import com.judy.momoplayer.playlist.PlayListItem;

/**
 * 此对象是一首歌曲的一些信息,包括比特率,单双声道
 * 采样率,歌曲,歌手,专辑,等等,它只用于显示在播放
 * 窗口上面,并且还是滚动显示,因为一次只能显示一个项目
 * 所以会起一个定时器去定时滚动要显示的内容或者接受
 * 鼠标事件,点击它的时候,它也会主动更新一项显示的内容
 * @author hadeslee
 */
public class SongInfo {
    private PlayListItem item;
    private int index=-1;

    public int getSize() {
        return 7;
    }
    public void reset(PlayListItem item){
        this.item=item;
        index=0;
    }
    public String get(int index) {
        if(item==null){
            return "";
        }
        switch (index) {
            case 0:
                return item.getName();
            case 1:
                return item.getTitle();
            case 2:
                return item.getArtist();
            case 3:
                return item.getAlbum();
            case 4:
                return item.getChannelInfo();
            case 5:
                return item.getBitRate();
            case 6:
                return item.getSampled();
            case 7:
                return item.getFormattedLength();
            default:
                return "MOMOPlayer";
        }
    }
    private String getResource(int index) {
        if(index==-1){
            return "";
        }
        String[] ss = {"songinfo.empty","songinfo.title", "songinfo.artist", "songinfo.album",
            "songinfo.channel", "songinfo.bitrate", "songinfo.samplerate",
            "songinfo.length"
        };
        return Config.getResource(ss[index]);

    }

    public String getNext() {
        String s = get(index);
        s=getResource(index)+s;
        index++;
        if (index > 7) {
            index = 0;
        }
        return s;
    }
    
}
