/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.june.lrc.comm;

import com.judy.momoplayer.lyric.SearchResult;
import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;
import com.june.lrc.bean.Lrc;
import com.june.lrc.bean.Lyric;
import com.june.lrc.bean.Lyrics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * 歌词工具类
 *
 * @author HappyLove
 */
public class LRCUtil {

    /**
     * 根据歌曲的歌名称和歌手的名称获取歌曲的内容
     *
     * @param name
     * @param author
     * @return String
     */
    public static String getLRCContent(String name, String author) {
        String url = null;
        url = LRCUtil.getDownloadPath(name, author);
        return LRCUtil.getContentFormWeb(url, "GBK");
    }

    /**
     * 从网络下载歌曲文件
     *
     * @param urlPath
     * @return
     */
    private static File downloadNet(String urlPath) {
        int byteread = 0;

        try {
            URL url = new URL(urlPath);
            URLConnection conn = url.openConnection();
            java.io.InputStream inStream = conn.getInputStream();

            FileOutputStream fs = new FileOutputStream("c:/abc.lrc");

            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            fs.flush();
            fs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new File("c:/abc.lrc");
    }

    /**
     * 下载歌曲文件
     *
     * @param name
     * @param author
     * @return
     */
    public static File getLRCFile(String name, String author) {
        File lrcFile = null;
        String filePath = null;
        filePath = getDownloadPath(name, author);
        if (filePath != null) {
            lrcFile = LRCUtil.downloadNet(filePath);
        }
        return lrcFile;
    }

    /**
     * 获取lrc的下载地址
     *
     * @param name
     * @param author
     * @return
     */
    private static String getDownloadPath(String name, String author) {
        String filePath = null;
        try {
            String contentXML = LRCUtil.getXMLContent(name, author);
            if (contentXML == null) {
                log.log(Level.INFO, "从百度上解析的xml内容：{0} ,提取xml失败！", contentXML);
            }
            List<Lrc> list = LRCUtil.getLrcsFromXML(contentXML);
            if (list != null) {
                filePath = list.get(0).getLrcDownloadUrl();//这里只去第一条lrc
            } else {
                filePath = null;
            }
        } catch (IOException ex) {
            Logger.getLogger(LRCUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filePath;
    }

    /**
     * 根据xml获取每一个可以下载的lrc对象
     *
     * @param xml
     * @return
     */
    public static List<Lrc> getLrcsFromXML(String xml) {
        List<Lrc> list = null;
        Lrc lrc = null;
        Document doc = null;
        try {
            // 读取并解析XML文档
            // SAXReader就是一个管道，用一个流的方式，把xml文件读出来
            // 
            // SAXReader reader = new SAXReader(); //User.hbm.xml表示你要解析的xml文档
            // Document document = reader.read(new File("User.hbm.xml"));
            // 下面的是通过解析xml字符串的
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            if (doc != null) {
                list = new ArrayList<Lrc>();
                lrc = new Lrc();
            } else {
                return null;
            }
            Element rootElt = doc.getRootElement(); // 获取根节点

            Element nextE = rootElt.element("count");
            String count = nextE.getTextTrim();
            log.log(Level.INFO, "count节点的值是：{0}", count);
            if ("0".equals(count) || "-1".equals(count)) {
                list = null;
            } else {
                put(rootElt, lrc, list, "url");
                //put(rootElt, lrc, list, "durl");
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            list = null;
        }
        //System.out.println(list.toString());
        return list;
    }

    private static void put(Element rootElt, Lrc lrc, List<Lrc> list, String node) {
        String encode, decode, type, lrcid, flag;
        Iterator<?> iter = rootElt.elementIterator(node); // 获取根节点下的子节点head
        // 遍历head节点
        while (iter.hasNext()) {
            Element recordEle = (Element) iter.next();
            encode = recordEle.elementTextTrim("encode"); // 拿到head节点下的子节点encode值
            //System.out.println("encode:" + encode);
            decode = recordEle.elementTextTrim("decode"); // 拿到head节点下的子节点decode值
            //System.out.println("decode:" + decode);
            type = recordEle.elementTextTrim("type"); // 拿到head节点下的子节点 type 值
            // System.out.println("type:" + type);
            lrcid = recordEle.elementTextTrim("lrcid"); // 拿到head节点下的子节点 lrcid 值
            //System.out.println("lrcid:" + lrcid);
            flag = recordEle.elementTextTrim("flag"); // 拿到head节点下的子节点 flag 值
            //System.out.println("flag:" + flag);
            lrc.setEncode(encode);
            lrc.setDecode(decode);
            lrc.setFlag(flag);
            lrc.setLrcid(lrcid);
            lrc.setType(type);
            list.add(lrc);
        }
    }

    /**
     * 根据歌曲的歌名称和歌手的名称获取歌曲的内容
     *
     * @param name
     * @param author
     * @return
     * @throws java.io.IOException
     */
    public static String getXMLContent(String name, String author) throws IOException {
        HttpClient http = new HttpClient();
        Config config = Config.getConfig();
        if (config.isUseProxy()) {
            if (config.getProxyUserName() != null && config.getProxyPwd() != null) {
                http.getState().setProxyCredentials(
                        new AuthScope(config.getProxyHost(), Integer.parseInt(config.getProxyPort())),
                        new UsernamePasswordCredentials(config.getProxyUserName(), config.getProxyPwd()));
            }
            http.getHostConfiguration().setProxy(config.getProxyHost(),
                    Integer.parseInt(config.getProxyPort()));
        }
        http.getParams().setContentCharset("GBK");
        //大约在冬季$$齐秦$$$$
        String clause = name + "$$" + author + "$$$$";
        String uri = LRCConstants.BD_SEARCH_URI + URLEncoder.encode(clause, "GBK");
        log.log(Level.INFO, "歌曲的歌词的Xml解析地址是：{0}", uri);
        GetMethod get = new GetMethod(uri);
        get.addRequestHeader("Host", "www.baidu.com");
        get.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11");
        get.addRequestHeader("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        get.addRequestHeader("Accept-Language", "zh-cn,zh;q=0.5");
        get.addRequestHeader("Keep-Alive", "300");
        get.addRequestHeader("Referer", "http://www.baidu.com/");
        get.addRequestHeader("Connection", "keep-alive");
        int code = http.executeMethod(get);
        if(code==0){}
        String temp = Util.getString(get.getResponseBodyAsStream());
        get.releaseConnection();
        return temp;
    }

    private static final Logger log = Logger.getLogger(LRCUtil.class.getName());

    /**
     * 根据歌名和歌手名获取json的歌词对象
     *
     * @param title
     * @param artist
     * @return
     */
    public static Lyrics getGCMLyrics(String title, String artist) {
        String url = null;
		try {
			artist = ("".equalsIgnoreCase(artist) || null == artist) ? "" : "/" + $(format(artist));
			url = LRCConstants.GCM_URL + $(format(title)) + artist;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
        //log.log(Level.INFO, "call url:{0}", url);
        String content = getContentFormWeb(url);
        //log.log(Level.INFO, "return jsons :{0}", content);
        Lyrics lys = null;
        if (content != null) {
            lys = convert2Lycs(content);
        }
        if (lys != null) {
            lys.setTitle(title);
            lys.setArtist(artist == null ? "" : artist);
        }
        return lys;
    }
    
    /**
     * 整理搜索内容
     * @param name
     * @return
     * @date 2017年1月4日 下午9:55:46
     * @writer junehappylove
     */
    private static String format(String name){
    	if(name.contains("http")){
    		String[] array = name.split("/");
    		name = array[array.length-1];
    	}
    	String[] array2 = name.split("\\.");
    	name = array2[0];
		return name;
    }

    /**
     * json串转换成json歌词对象
     *
     * @param content
     * @return
     */
    private static Lyrics convert2Lycs(String content) {
        //use a json package jar
        List<Lyric> list = null;
        Lyric lyric = null;
        JSONObject jobj = JSONObject.fromString(content);
        JSONArray jarray = jobj.getJSONArray("result");
        list = new ArrayList<Lyric>();
        //lyric = new Lyric();
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject obj = jarray.getJSONObject(i);
            lyric = (Lyric) JSONObject.toBean(obj, Lyric.class);
            if (lyric != null) {
                list.add(lyric);
            }
        }
        Lyrics lyrics = null;
        lyrics = new Lyrics();
        lyrics.setCode(jobj.getString("code"));
        lyrics.setCount(jobj.getString("count"));
        lyrics.setResult(list);
        //lyrics = (Lyrics)JSONObject.toBean(jobj,Lyrics.class);
        return lyrics;
    }

    public static void main(String[] args) {
        String con = "";
        try {
            //con = LRCUtil.getXMLContent("大约在冬季", "齐秦"); //大约在冬季$$齐秦$$$$
            //LRCUtil.getLrcsFromXML(con);
            //con = LRCUtil.getLRCContent("大约在冬季", "齐秦");
            Lyrics lyrics = LRCUtil.getGCMLyrics("海阔天空", "Beyond");
            //Lyrics lyrics = LRCUtil.getGCMLyrics("大约在冬季", "齐秦");
            //Lyrics lyrics = LRCUtil.getGCMLyrics("aaabbbccc", "dddeeefff");
            System.out.println("count：" + lyrics.getCount());
        } catch (Exception ex) {
            Logger.getLogger(LRCUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(con);
    }

    /**
     * 一个简单的方法,得到传进去的歌手和标题的 歌词搜索结果,以一个列表形式返回
     *
     * @param lyrics
     * @return
     */
    public static List<SearchResult> getSearchResults(Lyrics lyrics) {
        List<SearchResult> list = new ArrayList<SearchResult>();
        List<Lyric> lrcs = lyrics.getResult();//检索结果
        //int count = Integer.parseInt(lyrics.getCount());//检索到的数量
        //String downUrl = "";
        String id, lrcid = null, lrcCode = null, artist, title;
        artist = lyrics.getArtist();//歌手  --- 可能为空
        title = lyrics.getTitle();//歌曲名称

        for (Lyric lrc : lrcs) {
            final String downUrl = lrc.getLrc();//下载地址
            id = lrc.getAid();
            title = lrc.getSong();
            SearchResult.Task task = new SearchResult.Task() {
                public String getLyricContent() {
                    //return getContentFormWeb(downUrl);//千里冰封写的有问题
                    return getContentFormWeb(downUrl, null);
                }
            };
            list.add(new SearchResult(id, lrcid, lrcCode, artist, title, task));
        }
        return list;
    }

	/**
	 * 根据下载地址获取到下载的内容
	 *
	 * @see #getContentFormWeb(java.lang.String)
	 * @param url
	 *            url中如果包含中文等必须是警告编码后的地址
	 * @return
	 */
	public static String getContentFormWeb(String url) {
		return getContentFormWeb(url, "GBK");
	}

	/**
	 * 根据歌词地址获取歌词内容
	 *
	 * @param url
	 *            url中如果包含中文等必须是警告编码后的地址
	 * @param code
	 *            指定歌词内容编码，如果不指定默认为UTF-8
	 * @see #getContentFormWeb(java.lang.String)
	 * @return
	 */
	public static String getContentFormWeb(String url, String code) {
        HttpURLConnection conn = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            if (url != null) {
                //将url加码
                log.log(Level.INFO, url);
                //url = $(url);//URLEncoder.encode(url,LRCConstants.UTF8);
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(10000);//10s无返回则链接失败
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), code == null ? LRCConstants.UTF8 : code));//UTF-8
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sb.append(temp).append("\n");
                }
            } else {
                sb = null;
            }
            return sb.toString();
        } catch (IOException exe) {
            exe.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(LRCUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * 网络数据要加码
     *
     * @param s
     * @return 经过utf-8处理
     * @throws UnsupportedEncodingException
     */
    public static String $(String s) throws UnsupportedEncodingException {
    	String url = URLEncoder.encode(s, "UTF-8");
    	url = url.replaceAll("\\+", "%20");	//url中将空格转换成%20
        return url;
    }
}
