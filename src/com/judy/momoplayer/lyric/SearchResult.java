/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.lyric;

import com.judy.momoplayer.util.Config;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * 搜索到的结果的对象表示法
 * @author judy
 */
public class SearchResult {

    private String id;
    private String lrcId;
    private String lrcCode;
    private String artist;//表示结果的歌手名
    private String title;//表示结果的标题 
    private Task task;//下载歌词的任务
    private String content;

    public static interface Task{
        String getLyricContent() throws Exception;
    }

    public SearchResult(String id, String lrcId, String lrcCode, String artist, String title, Task task) {
        this.id = id;
        this.lrcId = lrcId;
        this.lrcCode = lrcCode;
        this.artist = artist;
        this.title = title;
        this.task = task;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getLrcCode() {
        return lrcCode;
    }

    public String getLrcId() {
        return lrcId;
    }

    public String getContent() throws Exception {
        if (content == null) {
            content = task.getLyricContent();
        }
        return content;
    }

    public void save(String name) throws Exception {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(Config.HOME, "Lyrics/" + name))));
        bw.write(String.valueOf(getContent()));
        bw.close();
    }

    @Override
    public String toString() {
        return artist + ":" + title;
    }
}
