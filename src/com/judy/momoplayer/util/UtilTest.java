/**
 * 
 */
package com.judy.momoplayer.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.judy.momoplayer.playlist.PlayListItem;

/**
 * @author junehappylove
 *
 */
public class UtilTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.judy.momoplayer.util.Util#getLyric(com.judy.momoplayer.playlist.PlayListItem)}.
	 */
	@Test
	public void testGetLyric() {
		PlayListItem info = null;
		info = new PlayListItem("那一年", ".MOMOPlayer//", 0, false);
		String content = null;
		try {
			content = Util.getLyric(info);
			//System.out.println(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue(!"".equals(content));
	}

}
