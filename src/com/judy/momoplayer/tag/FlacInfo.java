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

import com.judy.audiotag.audio.exceptions.CannotReadException;
import com.judy.audiotag.audio.flac.FlacInfoReader;
import com.judy.audiotag.audio.flac.FlacTagReader;
import com.judy.audiotag.audio.generic.GenericAudioHeader;
import com.judy.audiotag.tag.flac.FlacTag;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class gives information (audio format and comments) about Flac file or URL.
 */
@SuppressWarnings("unchecked")
public class FlacInfo implements TagInfo {

    private static final long serialVersionUID = 20071213L;
    private static Logger log = Logger.getLogger(FlacInfo.class.getName());
    protected int channels = -1;
    protected int bitspersample = -1;
    protected int samplerate = -1;
    protected long size = 0;
    private long length = -1;
    private int bitrate = -1;
    protected String location = null;
    private String title = "";
    private String album = "";
    private String artist = "";
    private String comment = "";
    private String genre = "";
    private String year = "";
    private String track = "";
    private final String type = "flac";
    private transient FlacTag tag;
    private transient GenericAudioHeader header;

    /**
     * Constructor.
     */
    public FlacInfo() {
        super();
    }

    /**
     * Load and parse Flac info from File.
     *
     * @param input
     * @throws IOException
     */
    public void load(File input) throws IOException, UnsupportedAudioFileException {
        RandomAccessFile raf = null;
        try {
            size = input.length();
            location = input.getPath();
            loadInfo(input);
            FlacTagReader reader = new FlacTagReader();
            raf = new RandomAccessFile(input, "rw");
            tag = reader.read(raf);
            header = new FlacInfoReader().read(raf);
            readTag();
        } catch (CannotReadException ex) {
            throw new UnsupportedAudioFileException(ex.toString());
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    private void readTag() {
        this.album = tag.getFirstAlbum();
        this.artist = tag.getFirstArtist();
        this.comment = tag.getFirstComment();
        this.genre = tag.getFirstGenre();
        this.title = tag.getFirstTitle();
        this.track = tag.getFirstTrack();
        this.year = tag.getFirstYear();
        this.length = header.getTrackLength();
        this.bitrate = (int) header.getBitRateAsNumber() * 1000;
    }

    /**
     * Load and parse Flac info from URL.
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
     * Load and parse Flac info from InputStream.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void load(InputStream input) throws IOException, UnsupportedAudioFileException {
        loadInfo(input);
    }

    /**
     * Load Flac info from input stream.
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
     * Load Flac info from file.
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
     * Load Flac info from AudioFileFormat.
     *
     * @param aff
     */
    protected void loadInfo(AudioFileFormat aff) throws UnsupportedAudioFileException {
        String ty = aff.getType().toString();
        if (!ty.equalsIgnoreCase("flac")) {
            throw new UnsupportedAudioFileException("Not Flac audio format");
        }
        AudioFormat af = aff.getFormat();
        channels = af.getChannels();
        samplerate = (int) af.getSampleRate();
        bitspersample = af.getSampleSizeInBits();
    }

    /**
     * Load Flac info from URL.
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

    public int getChannels() {
        return channels;
    }

    public int getSamplingRate() {
        return samplerate;
    }

    public int getBitsPerSample() {
        return bitspersample;
    }

    public Vector getComment() {
        Vector v = new Vector();
        v.add(comment);
        return v;
    }

    public String getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getTrack() {
        return track;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public long getPlayTime() {
        return length;
    }

    public int getBitRate() {
        return bitrate;
    }

    public String getType() {
        return type;
    }
}
