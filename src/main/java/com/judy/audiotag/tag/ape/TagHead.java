/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.audiotag.tag.ape;

import com.judy.momoplayer.util.Util;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 内部的私有类,它代表了一个APE标签的头部
 * 
 * @author judy
 */
public class TagHead {

	private static Logger log = Logger.getLogger(TagHead.class.getName());
	private byte[] data;// 头部的数据
	private boolean valid;// 是否是合法的头部
	private int version = 2000;// 版本,默认是2000
	private int tagSize;// 标签的长度,包括尾标签以及所有的项目,不包括头标签
	private int itemCount;// 项目的数量
	private int flag = FOOT;// 标签的其它标志,指示它是头部还是尾部
	public static final int HEAD = 0xA0000000;
	public static final int FOOT = 0x80000000;
	public static final int V1 = 1000;// 表示APE的版本号
	public static final int V2 = 2000;
	private int index;// 这个标头的起始点的位置,相对于文件

	public TagHead(byte[] data) {
		this.data = data;
		parseData();
	}

	public TagHead() {
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public byte[] getBytes() {
		// 标头总共是32个字节
		byte[] head = new byte[32];
		byte[] temp = { (byte) 'A', (byte) 'P', (byte) 'E', (byte) 'T', (byte) 'A', (byte) 'G', (byte) 'E',
				(byte) 'X' };
		// 先把头的标量填进去,8字节
		System.arraycopy(temp, 0, head, 0, 8);
		temp = Util.getBytesFromInt(version);
		// 再把版本号写进去,4字节
		System.arraycopy(temp, 0, head, 8, 4);
		temp = Util.getBytesFromInt(tagSize);
		log.log(Level.SEVERE, "TAGSIZE=" + tagSize);
		// 再把标签的长度写进去,4字节
		System.arraycopy(temp, 0, head, 12, 4);
		temp = Util.getBytesFromInt(itemCount);
		// 再把标签的数量写进去,4字节
		System.arraycopy(temp, 0, head, 16, 4);
		temp = Util.getBytesFromInt(flag);
		// 再把标志写进去,表示是标签头部还是尾部
		System.arraycopy(temp, 0, head, 20, 4);
		// 把标志空的8个字节进去,因为默认就是空的,所以不用写了
		// 头部或者尾部的数据块已经构造好了
		return head;
	}

	private void parseData() {
		try {
			checkHead();
			checkVersion();
			checkTagSize();
			checkItemCount();
			checkFlag();
			valid = true;
		} catch (Exception e) {
			//e.printStackTrace();
			log.log(Level.SEVERE, "分析标签异常!");
			valid = false;
		}
	}

	/**
	 * 这个标签是否有头标签,因为一般读都是从尾部读过去的
	 * 
	 * @return 是否有头标签,重写的时候有用
	 */
	public boolean hasHeader() {
		return ((1 << 31) & flag) != 0;
	}

	/**
	 * 检查头部八个字节的的数据是否一样
	 */
	private void checkHead() {
		byte[] temp = new byte[8];
		byte[] head = { (byte) 'A', (byte) 'P', (byte) 'E', (byte) 'T', (byte) 'A', (byte) 'G', (byte) 'E',
				(byte) 'X' };
		System.arraycopy(data, 0, temp, 0, 8);
		// 比较两个头部的数据是否一样,这是第一要素
		if (!Arrays.equals(head, temp)) {
			throw new RuntimeException("头部数据不一样!");
		}
	}

	/**
	 * 检查版本号是否合法,必须是1000或者2000
	 */
	private void checkVersion() {
		byte[] temp = new byte[4];
		System.arraycopy(data, 8, temp, 0, 4);
		int v = Util.getInt(temp);
		if (v == 2000 || v == 1000) {
			version = v;
			log.log(Level.INFO, "版本号是:" + v);
		} else {
			throw new RuntimeException("版本号不合法!!");
		}
	}

	private void checkTagSize() {
		byte[] temp = new byte[4];
		System.arraycopy(data, 12, temp, 0, 4);
		tagSize = Util.getInt(temp);
		log.log(Level.INFO, "标签大小:" + tagSize);
	}

	private void checkItemCount() {
		byte[] temp = new byte[4];
		System.arraycopy(data, 16, temp, 0, 4);
		itemCount = Util.getInt(temp);
		log.log(Level.INFO, "标签项目数:" + itemCount);
	}

	private void checkFlag() {
		byte[] temp = new byte[4];
		System.arraycopy(data, 20, temp, 0, 4);
		flag = Util.getInt(temp);
		log.log(Level.INFO, "标志:" + flag);
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public void setTagSize(int tagSize) {
		this.tagSize = tagSize;
	}

	public void setVersion(int version) {
		if (!(version == V1 || version == V2)) {
			throw new RuntimeException("非法的版本号,只能是V2或者V1.");
		}
		this.version = version;
	}

	public int getFlag() {
		return flag;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getTagSize() {
		return tagSize;
	}

	public boolean isValid() {
		return valid;
	}

	public int getVersion() {
		return version;
	}

	public static void main(String[] args) {
	}
}
