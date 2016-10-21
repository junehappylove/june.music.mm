/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.tag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import com.judy.audiotag.audio.exceptions.InvalidAudioFrameException;
import com.judy.audiotag.audio.exceptions.ReadOnlyFileException;
import com.judy.audiotag.audio.mp3.MP3AudioHeader;
import com.judy.audiotag.audio.mp3.MP3File;
import com.judy.audiotag.tag.Tag;
import com.judy.audiotag.tag.TagException;
import com.judy.audiotag.tag.ape.APEv2Tag;
import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;

/**
 * Mpeg格式的音频文件的信息读取类
 * @author judy
 */
@SuppressWarnings("unchecked")
public class MpegInfo implements TagInfo {

    private static final long serialVersionUID = 20071213L;
    private static Logger log = Logger.getLogger(MpegInfo.class.getName());
    protected int channels = -1;
    protected String channelsMode = null;
    protected String version = null;
    protected int rate = 0;
    protected String layer = null;
    protected String emphasis = null;
    protected int nominalbitrate = 0;
    protected long total = 0;
    protected String vendor = null;
    protected String location = null;
    protected long size = 0;
    protected boolean copyright = false;
    protected boolean crc = false;
    protected boolean original = false;
    protected boolean priv = false;
    protected boolean vbr = false;
    protected String track;
    protected String year = null;
    protected String genre = null;
    protected String title = null;
    protected String artist = null;
    protected String album = null;
    protected Vector<String> comments = null;
    private final String type = "mp3";
    protected transient MP3AudioHeader header;//音频文件头
    protected transient Tag tag;

    /**
     * Constructor.
     */
    public MpegInfo() {
        super();
    }

    /**
     * Load and parse MPEG info from File.
     *
     * @param input
     * @throws IOException
     */
    public void load(File input) throws IOException, UnsupportedAudioFileException {
        size = input.length();
        location = input.getPath();
        loadInfo(input);
    }

    /**
     * Load and parse MPEG info from URL.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void load(URL input) throws IOException, UnsupportedAudioFileException {
        location = input.toString();
        loadInfo(input);
    }

    /**
     * Load and parse MPEG info from InputStream.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void load(InputStream input) throws IOException, UnsupportedAudioFileException {
        loadInfo(input);
    }

    /**
     * Load info from input stream.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected void loadInfo(InputStream input) throws IOException, UnsupportedAudioFileException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
        loadInfo(aff);
    }

    /**
     * Load MP3 info from file.
     *
     * @param file
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected void loadInfo(File file) throws IOException, UnsupportedAudioFileException {
        try {
            AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
            loadInfo(aff);
            MP3File mp3 = new MP3File(file, MP3File.LOAD_ALL, true);
            header = mp3.getMP3AudioHeader();
            readHead();
            int tp = Config.getConfig().getReadTagOrder();
            log.log(Level.INFO, "读取顺序是:" + tp);
            switch (tp) {
                case Config.APEv2_ID3v2_ID3v1:
                    readAPEv2_ID3v2_ID3v1(mp3);
                    break;
                case Config.ID3v1_APEv2_ID3v2:
                    readID3v1_APEv2_ID3v2(mp3);
                    break;
                case Config.ID3v1_ID3v2_APEv2:
                    readID3v1_ID3v2_APEv2(mp3);
                    break;
                case Config.ID3v2_APEv2_ID3v1:
                    readID3v2_APEv2_ID3v1(mp3);
                    break;
            }
            if (tag == null) {
                log.log(Level.SEVERE, "没有合法的标签可读!!");
//                throw new UnsupportedAudioFileException("没有标签");
            } else {//否则就把标签读出来

                readTag();
            }
        } catch (TagException ex) {
            throw new IOException(ex);
        } catch (ReadOnlyFileException ex) {
            throw new IOException(ex);
        } catch (InvalidAudioFrameException ex) {
            throw new IOException(ex);
        }

    }

    private void readAPEv2_ID3v2_ID3v1(MP3File mp3) {
        if (mp3.hasAPEv2Tag()) {
            tag = mp3.getAPEv2Tag();
        } else if (mp3.hasID3v2Tag()) {
            tag = mp3.getID3v2Tag();
        } else if (mp3.hasID3v1Tag()) {
            tag = mp3.getID3v1Tag();
        }
    }

    private void readID3v1_APEv2_ID3v2(MP3File mp3) {
        if (mp3.hasID3v1Tag()) {
            tag = mp3.getID3v1Tag();
        } else if (mp3.hasAPEv2Tag()) {
            tag = mp3.getAPEv2Tag();
        } else if (mp3.hasID3v2Tag()) {
            tag = mp3.getID3v2Tag();
        }
    }

    private void readID3v1_ID3v2_APEv2(MP3File mp3) {
        if (mp3.hasID3v1Tag()) {
            tag = mp3.getID3v1Tag();
        } else if (mp3.hasID3v2Tag()) {
            tag = mp3.getID3v2Tag();
        } else if (mp3.hasAPEv2Tag()) {
            tag = mp3.getAPEv2Tag();
        }
    }

    private void readID3v2_APEv2_ID3v1(MP3File mp3) {
        System.out.println("读id3v2...");
        if (mp3.hasID3v2Tag()) {
            System.out.println("有id3v2标答........");
            tag = mp3.getID3v2Tag();
        } else if (mp3.hasAPEv2Tag()) {
            tag = mp3.getAPEv2Tag();
        } else if (mp3.hasID3v1Tag()) {
            tag = mp3.getID3v1Tag();
        }
    }

    /**
     * 读取标签,以替换以前读的标签
     */
    private void readTag() {
        //只有APE格式的标签是UTF-8的编码,不用变格式,其它的都要做字符转换
        if (tag instanceof APEv2Tag) {
            log.log(Level.INFO, "是APE的标签.......");
            this.album = tag.getFirstAlbum();
            this.artist = tag.getFirstArtist();
            this.genre = tag.getFirstGenre();
            this.track = tag.getFirstTrack();
            if (comments == null) {
                comments = new Vector<String>();
            }
            comments.add(tag.getFirstComment());
            this.title = tag.getFirstTitle();
            this.year = tag.getFirstYear();
        } else {
            System.out.println("tag=" + tag.getClass());
            this.album= getChineseString(tag.getFirstAlbum());
            this.artist = getChineseString(tag.getFirstArtist());
            this.genre = getChineseString(tag.getFirstGenre());
            this.track = getChineseString(tag.getFirstTrack());
            if (comments == null) {
                comments = new Vector<String>();
            }
            comments.add(getChineseString(tag.getFirstComment()));
            this.title = getChineseString(tag.getFirstTitle());
            this.year = getChineseString(tag.getFirstYear());
//            if (Config.getConfig().getEncoding().equals("ISO8859-1")) {
//                this.album = tag.getFirstAlbum();
//                this.artist = tag.getFirstArtist();
//                this.genre = tag.getFirstGenre();
//                this.track = tag.getFirstTrack();
//                if (comments == null) {
//                    comments = new Vector();
//                }
//                comments.add(tag.getFirstComment());
//                this.title = tag.getFirstTitle();
//                this.year = tag.getFirstYear();
//            } else {
//                this.album = Util.convertString(tag.getFirstAlbum());
//                this.artist = Util.convertString(tag.getFirstArtist());
//                this.genre = Util.convertString(tag.getFirstGenre());
//                this.track = tag.getFirstTrack();
//                if (comments == null) {
//                    comments = new Vector();
//                }
//                comments.add(Util.convertString(tag.getFirstComment()));
//                this.title = Util.convertString(tag.getFirstTitle());
//                this.year = Util.convertString(tag.getFirstYear());
//            }
        }
    }
    private String getChineseString(String source){
        String temp=Util.convertString(source);
        if(temp.indexOf("??")==-1&&temp.indexOf("�")==-1){
            return temp;
        }else{
            return source;
        }
    }
    private void readHead() {
        this.total = header.getTrackLength();
    }

    /**
     * Load info from AudioFileFormat.
     *
     * @param aff
     */
    protected void loadInfo(AudioFileFormat aff) throws UnsupportedAudioFileException {
        String tp = aff.getType().toString();
        System.out.println("format:" + aff.getType().getExtension());
        if (!tp.equalsIgnoreCase("mp3")) {
            throw new UnsupportedAudioFileException("Not MP3 audio format");
        }
        if (aff instanceof TAudioFileFormat) {
            Map<?, ?> props = ((TAudioFileFormat) aff).properties();
            if (props.containsKey("mp3.channels")) {
                channels = ((Integer) props.get("mp3.channels")).intValue();
            }
            if (props.containsKey("mp3.frequency.hz")) {
                rate = ((Integer) props.get("mp3.frequency.hz")).intValue();
            }
            if (props.containsKey("mp3.bitrate.nominal.bps")) {
                nominalbitrate = ((Integer) props.get("mp3.bitrate.nominal.bps")).intValue();
            }
            if (props.containsKey("mp3.version.layer")) {
                layer = "Layer " + props.get("mp3.version.layer");
            }
            if (props.containsKey("mp3.version.mpeg")) {
                version = (String) props.get("mp3.version.mpeg");
                if (version.equals("1")) {
                    version = "MPEG1";
                } else if (version.equals("2")) {
                    version = "MPEG2-LSF";
                } else if (version.equals("2.5")) {
                    version = "MPEG2.5-LSF";
                }
            }
            if (props.containsKey("mp3.mode")) {
                int mode = ((Integer) props.get("mp3.mode")).intValue();
                if (mode == 0) {
                    channelsMode = "Stereo";
                } else if (mode == 1) {
                    channelsMode = "Joint Stereo";
                } else if (mode == 2) {
                    channelsMode = "Dual Channel";
                } else if (mode == 3) {
                    channelsMode = "Single Channel";
                }
            }
            if (props.containsKey("mp3.crc")) {
                crc = ((Boolean) props.get("mp3.crc")).booleanValue();
            }
            if (props.containsKey("mp3.vbr")) {
                vbr = ((Boolean) props.get("mp3.vbr")).booleanValue();
            }
            if (props.containsKey("mp3.copyright")) {
                copyright = ((Boolean) props.get("mp3.copyright")).booleanValue();
            }
            if (props.containsKey("mp3.original")) {
                original = ((Boolean) props.get("mp3.original")).booleanValue();
            }
            emphasis = "none";
            if (props.containsKey("title")) {
                title = (String) props.get("title");
                title = toGBK(title);
            }
            if (props.containsKey("author")) {
                artist = (String) props.get("author");
                artist = toGBK(artist);
            }
            if (props.containsKey("album")) {
                album = (String) props.get("album");
                album = toGBK(album);
            }
            if (props.containsKey("date")) {
                year = (String) props.get("date");

            }
            if (props.containsKey("duration")) {
                total = (long) Math.round((((Long) props.get("duration")).longValue()) / 1000000);
            }
            if (props.containsKey("mp3.id3tag.genre")) {
                genre = (String) props.get("mp3.id3tag.genre");
                genre = toGBK(genre);
            }
            if (props.containsKey("mp3.id3tag.track")) {
                try {
                    track = ((String) props.get("mp3.id3tag.track"));
                } catch (NumberFormatException e1) {
                    // Not a number
                }
            }
        }
    }

    private String toGBK(String s) {
//        return s;
        try {
            return new String(s.getBytes("iso8859-1"), Config.getConfig().getEncoding());
        } catch (Exception ex) {
            Logger.getLogger(MpegInfo.class.getName()).log(Level.SEVERE, null, ex);
            return s;
        }
    }

    /**
     * Load MP3 info from URL.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected void loadInfo(URL input) throws IOException, UnsupportedAudioFileException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
        loadInfo(aff);
        loadShoutastInfo(aff);
    }

    /**
     * Load Shoutcast info from AudioFileFormat.
     *
     * @param aff
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected void loadShoutastInfo(AudioFileFormat aff) throws IOException, UnsupportedAudioFileException {
        String tp = aff.getType().toString();
        if (!tp.equalsIgnoreCase("mp3")) {
            throw new UnsupportedAudioFileException("Not MP3 audio format");
        }
        if (aff instanceof TAudioFileFormat) {
            Map<String, ?> props = ((TAudioFileFormat) aff).properties();
            // Try shoutcast meta data (if any).
            Iterator<String> it = props.keySet().iterator();
            comments = new Vector<String>();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.startsWith("mp3.shoutcast.metadata.")) {
                    String value = (String) props.get(key);
                    key = key.substring(23, key.length());
                    if (key.equalsIgnoreCase("icy-name")) {
                        title = value;
                    } else if (key.equalsIgnoreCase("icy-genre")) {
                        genre = value;
                    } else {
                        comments.add(key + "=" + value);
                    }
                }
            }
        }
    }

    public boolean getVBR() {
        return vbr;
    }

    public int getChannels() {
        return channels;
    }

    public String getVersion() {
        return version;
    }

    public String getEmphasis() {
        return emphasis;
    }

    public boolean getCopyright() {
        return copyright;
    }

    public boolean getCRC() {
        return crc;
    }

    public boolean getOriginal() {
        return original;
    }

    public String getLayer() {
        return layer;
    }

    public long getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }

    /*-- TagInfo Implementation --*/
    public int getSamplingRate() {
        return rate;
    }

    public int getBitRate() {
        return nominalbitrate;
    }

    public long getPlayTime() {
        return total;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getTrack() {
        return track;
    }

    public String getGenre() {
        return genre;
    }

    public Vector<String> getComment() {
        return comments;
    }

    public String getYear() {
        return year;
    }

    public String getType() {
        return type;
    }

    /**
     * Get channels mode.
     *
     * @return channels mode
     */
    public String getChannelsMode() {
        return channelsMode;
    }

    public static void main(String[] args) throws Exception {
        MpegInfo info = new MpegInfo();
//        info.load(new File("D:\\有没有人告诉你.mp3"));
        info.load(new URL("http://zhengfu.dx.comenic.com/mzju-gov-b/admingly/movie/2007103014045.mp3"));
        Class<? extends MpegInfo> c = info.getClass();
        Method[] ms = c.getMethods();
        for (Method m : ms) {
            if (m.getName().startsWith("get")) {
                Object obj = m.invoke(info);
                System.out.println(m.getName() + ":" + obj);
            }
        }
    }
}

