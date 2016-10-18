/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.playlist;

import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * 一个播放列表的基本实现
 * @author hadeslee
 */
public class BasicPlayList implements PlayList {

    private static final long serialVersionUID = 20071214L;
    protected Vector<PlayListItem> playList;
    protected int currentIndex = -1;
    protected boolean isModified;
    protected String M3UHome;//MP3U格式列表的位置
    protected String PLSHome;//PLS格式列表的位置
    private String name;//表示此播放列表的名字
    private Config config;//全局的配置对象
    private PlayListItem playing;//正在播放的项
    public BasicPlayList(Config config) {
        this.config = config;
        playList = new Vector<PlayListItem>();
    }

    public boolean load(String filename) {
        setModified(true);
        boolean loaded = false;
        if ((filename != null) && (filename.toLowerCase().endsWith(".m3u"))) {
            loaded = loadM3U(filename);
        } else if ((filename != null) && (filename.toLowerCase().endsWith(".pls"))) {
            loaded = loadPLS(filename);
        }
        return loaded;
    }

    protected boolean loadM3U(String filename) {
        boolean loaded = false;
        BufferedReader br = null;
        try {
            // Playlist from URL ? (http:, ftp:, file: ....)
            if (Config.startWithProtocol(filename)) {
                br = new BufferedReader(new InputStreamReader((new URL(filename)).openStream()));
            } else {
                br = new BufferedReader(new FileReader(filename));
            }
            String line = null;
            String songName = null;
            String songFile = null;
            String songLength = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (line.startsWith("#")) {
                    if (line.toUpperCase().startsWith("#EXTINF")) {
                        int indA = line.indexOf(",", 0);
                        if (indA != -1) {
                            songName = line.substring(indA + 1, line.length());
                        }
                        int indB = line.indexOf(":", 0);
                        if (indB != -1) {
                            if (indB < indA) {
                                songLength = (line.substring(indB + 1, indA)).trim();
                            }
                        }
                    }
                } else {
                    songFile = line;
                    if (songName == null) {
                        songName = songFile;
                    }
                    if (songLength == null) {
                        songLength = "-1";
                    }
                    PlayListItem pli = null;
                    if (Config.startWithProtocol(songFile)) {
                        // URL.
                        pli = new PlayListItem(songName, songFile, Long.parseLong(songLength), false);
                    } else {
                        // File.
                        File f = new File(songFile);
                        if (f.exists()) {
                            pli = new PlayListItem(songName, songFile, Long.parseLong(songLength), true);
                        } else {
                            // Try relative path.
                            f = new File(config.getLastDir() + songFile);
                            if (f.exists()) {
                                pli = new PlayListItem(songName, config.getLastDir() + songFile, Long.parseLong(songLength), true);
                            } else {
                                // Try optional M3U home.
                                if (M3UHome != null) {
                                    if (Config.startWithProtocol(M3UHome)) {
                                        pli = new PlayListItem(songName, M3UHome + songFile, Long.parseLong(songLength), false);
                                    } else {
                                        pli = new PlayListItem(songName, M3UHome + songFile, Long.parseLong(songLength), true);
                                    }
                                }
                            }
                        }
                    }
                    if (pli != null) {
                        this.appendItem(pli);
                    }
                    songFile = null;
                    songName = null;
                    songLength = null;
                }
            }
            loaded = true;
        } catch (Exception e) {
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ioe) {
            }
        }
        name = Util.getSongName(new File(filename));
        return loaded;
    }

    protected boolean loadPLS(String filename) {
        boolean loaded = false;
        BufferedReader br = null;
        try {
            // Playlist from URL ? (http:, ftp:, file: ....)
            if (Config.startWithProtocol(filename)) {
                br = new BufferedReader(new InputStreamReader((new URL(filename)).openStream()));
            } else {
                br = new BufferedReader(new FileReader(filename));
            }
            String line = null;
            String songName = null;
            String songFile = null;
            String songLength = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if ((line.toLowerCase().startsWith("file"))) {
                    StringTokenizer st = new StringTokenizer(line, "=");
                    st.nextToken();
                    songFile = st.nextToken().trim();
                } else if ((line.toLowerCase().startsWith("title"))) {
                    StringTokenizer st = new StringTokenizer(line, "=");
                    st.nextToken();
                    songName = st.nextToken().trim();
                } else if ((line.toLowerCase().startsWith("length"))) {
                    StringTokenizer st = new StringTokenizer(line, "=");
                    st.nextToken();
                    songLength = st.nextToken().trim();
                }
                // New entry ?
                if (songFile != null) {
                    PlayListItem pli = null;
                    if (songName == null) {
                        songName = songFile;
                    }
                    if (songLength == null) {
                        songLength = "-1";
                    }
                    if (Config.startWithProtocol(songFile)) {
                        // URL.
                        pli = new PlayListItem(songName, songFile, Long.parseLong(songLength), false);
                    } else {
                        // File.
                        File f = new File(songFile);
                        if (f.exists()) {
                            pli = new PlayListItem(songName, songFile, Long.parseLong(songLength), true);
                        } else {
                            // Try relative path.
                            f = new File(config.getLastDir() + songFile);
                            if (f.exists()) {
                                pli = new PlayListItem(songName, config.getLastDir() + songFile, Long.parseLong(songLength), true);
                            } else {
                                // Try optional PLS home.
                                if (PLSHome != null) {
                                    if (Config.startWithProtocol(PLSHome)) {
                                        pli = new PlayListItem(songName, PLSHome + songFile, Long.parseLong(songLength), false);
                                    } else {
                                        pli = new PlayListItem(songName, PLSHome + songFile, Long.parseLong(songLength), true);
                                    }
                                }
                            }
                        }
                    }
                    if (pli != null) {
                        this.appendItem(pli);
                    }
                    songName = null;
                    songFile = null;
                    songLength = null;
                }
            }
            loaded = true;
        } catch (Exception e) {
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception ioe) {
            }
        }
        name = Util.getSongName(new File(filename));
        return loaded;
    }

    public boolean save(String filename) {
        // Implemented by C.K
        if (playList != null) {
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(filename));
                bw.write("#EXTM3U");
                bw.newLine();
                Iterator<PlayListItem> it = playList.iterator();
                while (it.hasNext()) {
                    PlayListItem pli = it.next();
                    bw.write("#EXTINF:" + pli.getM3UExtInf());
                    bw.newLine();
                    bw.write(pli.getLocation());
                    bw.newLine();
                }
                return true;
            } catch (IOException e) {
            } finally {
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException ioe) {
                }
            }
        }
        return false;
    }

    public void addItemAt(PlayListItem pli, int pos) {
        playList.add(pos, pli);
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).isSelected()) {
                currentIndex = i;
            }
        }
        setModified(true);
        if (Config.getConfig().getReadTagInfoStrategy().equals(Config.READ_WHEN_ADD)) {
            pli.getTagInfo();
        }
    }

    public void removeItem(PlayListItem pli) {
        playList.remove(pli);
        setModified(true);
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).isSelected()) {
                currentIndex = i;
            }
        }
    }

    public void removeItemAt(int pos) {
        playList.remove(pos);
        setModified(true);
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).isSelected()) {
                currentIndex = i;
            }
        }
    }

    public void removeAllItems() {
        playList.clear();
        currentIndex = -1;
        setModified(true);
    }

    public void appendItem(PlayListItem pli) {
        playList.add(pli);
        setModified(true);
        if (Config.getConfig().getReadTagInfoStrategy().equals(Config.READ_WHEN_ADD)) {
            pli.getTagInfo();
        }
    }

    public void sortItems(int sortmode) {
    }

    public PlayListItem getItemAt(int pos) {
        if (pos < playList.size() && pos > -1) {
            return playList.get(pos);
        }
        return null;
    }

    public Vector<PlayListItem> getAllItems() {
        return playList;
    }

    public int getPlaylistSize() {
        return playList.size();
    }

    public void shuffle() {
        int size = playList.size();
        if (size < 2) {
            return;
        }
        List<PlayListItem> v = playList;
        playList = new Vector<PlayListItem>(size);
        while ((size = v.size()) > 0) {
            playList.add(v.remove((int) (Math.random() * size)));
        }
        begin();
        //此次可能不一定需要这个方法,因为本身此方法就是打
        //乱播放列表的顺序,所以此时再点击下一首不一定就是
        //当前播放的歌曲的下一首
        for (int i = 0; i < playList.size(); i++) {
            if (playList.get(i).isSelected()) {
                currentIndex = i;
            }
        }
    }

    public PlayListItem getCursor() {
        if ((currentIndex < 0) || (currentIndex >= playList.size())) {
            return null;
        }
        return getItemAt(currentIndex);
    }

    public void begin() {
        currentIndex = -1;
        if (getPlaylistSize() > 0) {
            currentIndex = 0;
        }
        setModified(true);
    }

    public int getSelectedIndex() {
        return currentIndex;
    }

    public int getIndex(PlayListItem pli) {
        return playList.indexOf(pli);
    }

    public void nextCursor() {
        //如果是随机播放,则随机取一个下标
        if (config.getPlayStrategy() == Config.RANDOM_PLAY) {
            currentIndex = (int) (Math.random() * playList.size());
        //否则就按顺序  
        } else if (config.getPlayStrategy() == Config.ORDER_PLAY) {
            currentIndex++;
        }
        if (config.isRepeatEnabled() && currentIndex >= playList.size()) {
            currentIndex = 0;
        }

    }

    public void previousCursor() {
        //如果是随机播放,则随机取一个下标
        if (config.getPlayStrategy() == Config.RANDOM_PLAY) {
            currentIndex = (int) (Math.random() * playList.size());
        //否则就按顺序  
        } else if (config.getPlayStrategy() == Config.ORDER_PLAY) {
            currentIndex--;
        }
        if (config.isRepeatEnabled() && currentIndex < 0) {
            currentIndex = playList.size() - 1;
        }
    }

    public boolean setModified(boolean set) {
        return isModified = set;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setCursor(int index) {
        currentIndex = index;
        playing = playList.get(index);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Get M3U home for relative playlist.
     *
     * @return
     */
    public String getM3UHome() {
        return M3UHome;
    }

    /**
     * Set optional M3U home for relative playlist.
     *
     * @param string
     */
    public void setM3UHome(String string) {
        M3UHome = string;
    }

    /**
     * Get PLS home for relative playlist.
     *
     * @return
     */
    public String getPLSHome() {
        return PLSHome;
    }

    /**
     * Set optional PLS home for relative playlist.
     *
     * @param string
     */
    public void setPLSHome(String string) {
        PLSHome = string;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setItemSelected(PlayListItem pl, int index) {
        if (pl == null) {
            return;
        }
//        for (PlayListItem p : playList) {
//            p.setSelected(false);
//        }
//        pl.setSelected(true);
        this.currentIndex = index;
    }
}
