/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import com.judy.momoplayer.lyric.SearchResult;
import com.judy.momoplayer.lyric.SearchResult.Task;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 此类是专门用来和Google app engine上面的应用来交互的
 * @author binfeng.li
 */
public final class GAEUtil {

    private static final String getSingleResultURL = "http://yoyolrc.appspot.com/YOYO?cmd=getSingleResult&artist={0}&title={1}";
    private static final String getLyricContentURL = "http://yoyolrc.appspot.com/YOYO?cmd=getLyricContent&id={0}&lrcId={1}&lrcCode={2}&artist={3}&title={4}";
    private static final String getResultListURL = "http://yoyolrc.appspot.com/YOYO?cmd=getResultList&artist={0}&title={1}";
    private static final String voteURL = "http://yoyolrc.appspot.com/YOYO?cmd={0}&yoyoVersion={1}";
    private static final String versionURL = "https://code.csdn.net/junehappylove/noname/tree/master/version.txt";
    private static final Logger log = Logger.getLogger(GAEUtil.class.getName());

    public static Version getRemoteVersion() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(versionURL).openConnection();
        Properties pro = new Properties();
        pro.load(conn.getInputStream());
        String version = pro.getProperty("Version");
        String url = pro.getProperty("URL");
        String des = pro.getProperty("Description");
        log.log(Level.INFO, "RemoteVersion={0}", version);
        return new Version(version, url, des);
    }

    /**
     * 投票
     * @param vote
     * @return 
     */
    public static boolean vote(String vote) {
        try {
            String urlContent = MessageFormat.format(voteURL, $(vote), $(Util.VERSION));
            ObjectInputStream ois = getObjectInputStream(urlContent);
            int back = ois.readInt();
            return back == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<SearchResult> getSearchResult(String artistParam, String titleParam) throws Exception {
        String urlContent = MessageFormat.format(getResultListURL, $(artistParam), $(titleParam));
        ObjectInputStream ois = getObjectInputStream(urlContent);
        int back = ois.readInt();
        List<SearchResult> list = new ArrayList<SearchResult>();
        if (back == 1) {
            int size = ois.readInt();
            for (int i = 0; i < size; i++) {
                final String artist = ois.readUTF();
                final String lrcCode = ois.readUTF();
                final String lrcId = ois.readUTF();
                final String title = ois.readUTF();
                final String id = ois.readUTF();
                final Task task = new Task() {

                    public String getLyricContent() {
                        return getLyricContent_S(id, lrcId, lrcCode, artist, title);
                    }
                };
                list.add(new SearchResult(id, lrcId, lrcCode, artist, title, task));
            }
        }
        return list;
    }

    static String getSingleResult(String artistParam, String titleParam) throws Exception {
        String urlContent = MessageFormat.format(getSingleResultURL, $(artistParam), $(titleParam));
        ObjectInputStream ois = getObjectInputStream(urlContent);
        int back = ois.readInt();
        if (back == 1) {
            return ois.readUTF();
        } else {
            return null;
        }
    }

    private static String getLyricContent_S(String id, String lrcId, String lrcCode, String artist, String title) {
        try {
            String urlContent = MessageFormat.format(getLyricContentURL, $(id), $(lrcId), $(lrcCode), $(artist), $(title));
            ObjectInputStream ois = getObjectInputStream(urlContent);
            int back = ois.readInt();
            if (back == 1) {
                return ois.readUTF();
            } else {
                return "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * 
     * @param urlContent
     * @return
     * @throws Exception 
     */
    private static ObjectInputStream getObjectInputStream(String urlContent) throws Exception {
        URL url = new URL(urlContent);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());
        return ois;
    }

    private static String $(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "UTF-8");
    }
}
