/**
 * 
 */
package com.june.lrc.agc;

import com.june.lrc.ILrcDownload;
import com.june.lrc.bean.Lyrics;

/**
 * 爱歌词<br>
 * http://www.22lrc.com/<br>
 * 楼主观察好久啊，这个从10年就成立了，现18年了，8年过去了还在用<br>
 * 
 * @author junehappylove
 *
 */
public class LoveLRC implements ILrcDownload {

	/* (non-Javadoc)
	 * @see com.june.lrc.ILrcDownload#download(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean download(String title, String artist) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.june.lrc.ILrcDownload#getLrcContent(java.lang.String, java.lang.String)
	 */
	@Override
	public String getLrcContent(String title, String artist) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.june.lrc.ILrcDownload#getLyrics(java.lang.String, java.lang.String)
	 */
	@Override
	public Lyrics getLyrics(String title, String artist) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
