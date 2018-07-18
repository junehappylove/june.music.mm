/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphal Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.judy.audiotag.audio.flac;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.judy.audiotag.audio.exceptions.CannotReadException;
import com.judy.audiotag.audio.generic.AudioFileReader;
import com.judy.audiotag.audio.generic.GenericAudioHeader;
import com.judy.audiotag.tag.Tag;

/**
 * Read encoding and tag info for Flac file (opensource lossless encoding)
 */
public class FlacFileReader extends AudioFileReader {

	private FlacInfoReader ir = new FlacInfoReader();
	private FlacTagReader tr = new FlacTagReader();

	protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf) throws CannotReadException, IOException {
		return ir.read(raf);
	}

	protected Tag getTag(RandomAccessFile raf) throws CannotReadException, IOException {
		return tr.read(raf);
	}

	public static void main(String[] args) throws Exception {
		// RandomAccessFile rf=new RandomAccessFile("D:\\执着.flac", "rw");
		// RandomAccessFile temp=new RandomAccessFile("D:\\temp.tmp","rw");
		// FlacTagReader reader=new FlacTagReader();
		// String s=reader.read(rf).getFirstTitle();
		// System.out.println(s);
		// FlacTagWriter w=new FlacTagWriter();
		// VorbisCommentTag vt=new VorbisCommentTag();
		// vt.addAlbum("专辑名");
		// vt.add(vt.createTitleField("这个标题可以吧"));
		// vt.addArtist("艺术字");
		// FlacTag tag=new FlacTag(vt, new
		// ArrayList<MetadataBlockDataPicture>());
		// w.write(tag, rf, temp);
		// w.write(tag, rf, rf)
		// System.out.println(head.getBitRate());
		// Tag tag=ff.getTag(rf);
		// Iterator it=tag.getFields();
		// while(it.hasNext()){
		// System.out.println("it="+it.next());
		// }
		// System.out.println(tag.getFirstArtist());
		// System.out.println(tag.getFirstTitle());
		// System.out.println(tag.getFirstAlbum());
	}

}
