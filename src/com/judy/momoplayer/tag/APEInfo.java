/*
 *  21.04.2004 Original verion. davagin@udm.ru.
 *-----------------------------------------------------------------------
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package com.judy.momoplayer.tag;

import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

/**
 * This class gives information (audio format and comments) about APE file or URL.
 */
public class APEInfo implements TagInfo {

    private static final long serialVersionUID = 20071213L;
    protected int channels = -1;
    protected int bitspersample = -1;
    protected int samplerate = -1;
    protected int bitrate = -1;
    protected int version = -1;
    protected String compressionlevel = null;
    protected int totalframes = -1;
    protected int blocksperframe = -1;
    protected int finalframeblocks = -1;
    protected int totalblocks = -1;
    protected int peaklevel = -1;
    protected long duration = -1;
    protected String author = null;
    protected String title = null;
    protected String copyright = null;
    protected Date date = null;
    protected String comment = null;
    protected String track = null;
    protected String genre = null;
    protected String album = null;
    protected long size = 0;
    protected String location = null;
    private final String type="ape";
    /**
     * Constructor.
     */
    public APEInfo() {
        super();
    }

    /**
     * Load and parse APE info from File.
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
     * Load and parse APE info from URL.
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
     * Load and parse APE info from InputStream.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void load(InputStream input) throws IOException, UnsupportedAudioFileException {
        loadInfo(input);
    }

    /**
     * Load APE info from input stream.
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
     * Load APE info from file.
     *
     * @param file
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected void loadInfo(File file) throws IOException, UnsupportedAudioFileException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(file);
        loadInfo(aff);
    }

    /**
     * Load APE info from AudioFileFormat.
     *
     * @param aff
     */
    protected void loadInfo(AudioFileFormat aff) throws UnsupportedAudioFileException {
        String ty = aff.getType().toString();
        if (!ty.equalsIgnoreCase("Monkey's Audio (ape)") && !ty.equalsIgnoreCase("Monkey's Audio (mac)")) {
            throw new UnsupportedAudioFileException("Not APE audio format");
        }
        if (aff instanceof TAudioFileFormat) {
            Map props = ((TAudioFileFormat) aff).properties();
            if (props.containsKey("duration")) {
                duration = ((Long) props.get("duration")).longValue();
            }
            if (props.containsKey("author")) {
                author = (String) props.get("author");
            }
            if (props.containsKey("title")) {
                title = (String) props.get("title");
            }
            if (props.containsKey("copyright")) {
                copyright = (String) props.get("copyright");
            }
            if (props.containsKey("date")) {
                date = (Date) props.get("date");
            }
            if (props.containsKey("comment")) {
                comment = (String) props.get("comment");
            }
            if (props.containsKey("album")) {
                album = (String) props.get("album");
            }
            if (props.containsKey("track")) {
                track = (String) props.get("track");
            }
            if (props.containsKey("genre")) {
                genre = (String) props.get("genre");
            }
            AudioFormat af = aff.getFormat();
            channels = af.getChannels();
            samplerate = (int) af.getSampleRate();
            bitspersample = af.getSampleSizeInBits();
            if (af instanceof TAudioFormat) {
                props = ((TAudioFormat) af).properties();
                if (props.containsKey("bitrate")) {
                    bitrate = ((Integer) props.get("bitrate")).intValue();
                }
                if (props.containsKey("ape.version")) {
                    version = ((Integer) props.get("ape.version")).intValue();
                }
                if (props.containsKey("ape.compressionlevel")) {
                    int cl = ((Integer) props.get("ape.compressionlevel")).intValue();
                    switch (cl) {
                        case 1000:
                            compressionlevel = "Fast";
                            break;
                        case 2000:
                            compressionlevel = "Normal";
                            break;
                        case 3000:
                            compressionlevel = "High";
                            break;
                        case 4000:
                            compressionlevel = "Extra High";
                            break;
                        case 5000:
                            compressionlevel = "Insane";
                            break;
                    }
                }
                if (props.containsKey("ape.totalframes")) {
                    totalframes = ((Integer) props.get("ape.totalframes")).intValue();
                }
                if (props.containsKey("ape.blocksperframe")) {
                    totalframes = ((Integer) props.get("ape.blocksperframe")).intValue();
                }
                if (props.containsKey("ape.finalframeblocks")) {
                    finalframeblocks = ((Integer) props.get("ape.finalframeblocks")).intValue();
                }
                if (props.containsKey("ape.totalblocks")) {
                    totalblocks = ((Integer) props.get("ape.totalblocks")).intValue();
                }
                if (props.containsKey("ape.peaklevel")) {
                    peaklevel = ((Integer) props.get("ape.peaklevel")).intValue();
                }
            }
        }
    }

    /**
     * Load APE info from URL.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected void loadInfo(URL input) throws IOException, UnsupportedAudioFileException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
        loadInfo(aff);
    }

    public long getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }

    public int getVersion() {
        return version;
    }

    public String getCompressionlevel() {
        return compressionlevel;
    }

    public int getTotalframes() {
        return totalframes;
    }

    public int getBlocksperframe() {
        return blocksperframe;
    }

    public int getFinalframeblocks() {
        return finalframeblocks;
    }

    public int getChannels() {
        return channels;
    }

    public int getSamplingRate() {
        return samplerate;
    }

    public int getBitsPerSample() {
        return bitspersample;
    }

    public int getTotalblocks() {
        return totalblocks;
    }

    public long getPlayTime() {
        return duration / 1000;
    }

    public int getBitRate() {
        return bitrate * 1000;
    }

    public int getPeaklevel() {
        return peaklevel;
    }

    public String getTrack() {
        return track;
    }

    public String getYear() {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            return String.valueOf(c.get(Calendar.YEAR));
        }
        return null;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return author;
    }

    public String getAlbum() {
        return album;
    }

    public Vector getComment() {
        if (comment != null) {
            Vector c = new Vector();
            c.add(comment);
            return c;
        }
        return null;
    }

    public String getCopyright() {
        return copyright;
    }
    public String getType(){
        return type;
    }
    public static void main(String[] args) throws Exception {
        APEInfo info = new APEInfo();
        info.load(new File("D:\\有没有人告诉你.mp3"));
        Class c = info.getClass();
        Method[] ms = c.getMethods();
        for (Method m : ms) {
            if (m.getName().startsWith("get")) {
                Object obj = m.invoke(info);
                System.out.println(m.getName() + ":" + obj);
            }
        }

    }
}
