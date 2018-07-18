/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StringReader;
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

import com.judy.momoplayer.lyric.SearchResult;
import com.judy.momoplayer.lyric.SearchResult.Task;

/**
 * 此类是专门用来和Google app engine上面的应用来交互的
 * @author binfeng.li
 */
public final class GAEUtil {

    private static final String SINGLE_RESULT_URL = "http://127.0.0.1/MOMO?cmd=getSingleResult&artist={0}&title={1}";//歌词服务器webservice地址
    private static final String LYRIC_CONTENT_URL = "http://127.0.0.1/MOMO?cmd=getLyricContent&id={0}&lrcId={1}&lrcCode={2}&artist={3}&title={4}";
    private static final String LIST_RESULT_URL = "http://127.0.0.1/MOMO?cmd=getResultList&artist={0}&title={1}";//歌词服务器webservice地址
    private static final String VOTE_URL = "http://127.0.0.1/MOMO?cmd={0}&momoVersion={1}";//投票服务器webservice地址
    private static final String VERSION_URL = "http://blog.csdn.net/junehappylove/article/details/52850828";//id='article_content'
    private static final String MATCHED_ID = "article_content";
    private static final Logger log = Logger.getLogger(GAEUtil.class.getName());

    /**
     * 获取远程版本
     * @return 版本
     * @throws IOException
     * @date 2017年1月4日 下午8:06:13
     * @writer junehappylove
     */
	public static Version getRemoteVersion() throws IOException {
		HttpURLConnection conn = null;
		Version ver = null;
		InputStream is = null;
		try {
			conn = (HttpURLConnection) Util.urlConnection(VERSION_URL);
			is = conn.getInputStream();
			String text = Util.htmlContent(Util.stream2String(is), MATCHED_ID);
			Properties pro = new Properties();
			pro.load(new StringReader(text));
			String version = pro.getProperty("Version");
			String url = pro.getProperty("URL");
			String des = pro.getProperty("Description");
			log.log(Level.INFO, "RemoteVersion={0}", version);
			ver = new Version(version, url, des);
		} catch (IOException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Get the remote version faild!");
			ver = null;
		}finally{
			if(is!=null){
				is.close();
			}
			if(conn!=null){
				conn.disconnect();
			}
		}
		return ver;
	}

    /**
     * 投票
     * @param vote
     * @return 是否投票成功
     */
    public static boolean vote(String vote) {
        try {
            String urlContent = MessageFormat.format(VOTE_URL, $(vote), $(Util.VERSION));
            ObjectInputStream ois = getObjectInputStream(urlContent);
            int back = ois.readInt();
            return back == 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static List<SearchResult> getSearchResult(String artistParam, String titleParam) throws Exception {
        String urlContent = MessageFormat.format(LIST_RESULT_URL, $(artistParam), $(titleParam));
        ObjectInputStream ois = getObjectInputStream(urlContent);
        int back = ois.readInt();
        List<SearchResult> list = new ArrayList<>();
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
        String urlContent = MessageFormat.format(SINGLE_RESULT_URL, $(artistParam), $(titleParam));
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
            String urlContent = MessageFormat.format(LYRIC_CONTENT_URL, $(id), $(lrcId), $(lrcCode), $(artist), $(title));
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
    	String url = URLEncoder.encode(s, "UTF-8");
    	url = url.replaceAll("\\+", "%20");	//url中将空格转换成%20
        return url;
    }
}
