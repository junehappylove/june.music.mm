package com.judy.audiotag.audio.mp3;

import com.judy.audiotag.audio.generic.GenericAudioHeader;
import com.judy.audiotag.audio.generic.AudioFileReader;
import com.judy.audiotag.audio.exceptions.CannotReadException;
import com.judy.audiotag.audio.exceptions.ReadOnlyFileException;
import com.judy.audiotag.audio.exceptions.InvalidAudioFrameException;
import com.judy.audiotag.audio.AudioFile;
import com.judy.audiotag.tag.Tag;
import com.judy.audiotag.tag.TagException;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;

/**
 * Read Mp3 Info (retrofitted to entagged ,done differently to entagged which is why some methods throw RuntimeException)
 * because done elsewhere
 */
public class MP3FileReader extends AudioFileReader
{
    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException
    {
        throw new RuntimeException("MP3FileReader.getEncodingInfo should be called");
    }

    protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException
    {
        throw new RuntimeException("MP3FileReader.getEncodingInfo should be called");
    }

    /**
     *
     * @param f
     * @return
     */
    //Override because we read mp3s differently to the entagged code
    public AudioFile read(File f)
        throws IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        MP3File mp3File = new MP3File(f, MP3File.LOAD_IDV1TAG | MP3File.LOAD_IDV2TAG);
        return mp3File;
    }
}
