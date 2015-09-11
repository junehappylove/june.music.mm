package com.judy.momoplayer.lyric;

import com.judy.momoplayer.playlist.PlayListItem;
import com.judy.momoplayer.util.GAEUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LRCUtil {


    /**
     * 根据传入的歌名和歌手名，得到一个搜索的列表
     * 注意，传进来的不能为null,否则将会出现不可意料的
     * 异常
     * 这个方法调用另一个方法，并且进行多种组合进行查找，直到
     * 有结果或者组合都有完了为止
     * @param item 要搜索的项
     * @return 一个搜索的列表
     */
    public static List<SearchResult> search(PlayListItem item) {
        if (!item.isInited()) {
            item.reRead();
        }
        List<SearchResult> list = new ArrayList<SearchResult>();
        try {
            //先把歌手名和歌名一起附上
            List<SearchResult> temp = search(item.getArtist(), item.getTitle());
            if (temp.isEmpty()) {
                temp = search("", item.getTitle());
                if (temp.isEmpty()) {
                    temp = search("", item.getName());
                }
            }
            list.addAll(temp);
        } catch (Exception ex) {
            Logger.getLogger(LRCUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return list;
    }

    /**
     * 实际上进行搜索的方法
     * @param singer
     * @param title
     * @return
     */
    public static List<SearchResult> search(String singer, String title) throws Exception {
        if (singer == null) {
            singer = "";
        }
        if (title == null) {
            title = "";
        }
        return GAEUtil.getSearchResult(singer, title);
    }

    private static String readURL(String url) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String temp = null;
            StringBuilder sb = new StringBuilder();
            while ((temp = br.readLine()) != null) {
                sb.append(temp).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (Exception exe) {
            exe.printStackTrace();
            return null;
        }
    }
}
