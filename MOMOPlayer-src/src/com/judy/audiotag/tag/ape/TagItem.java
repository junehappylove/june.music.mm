/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.audiotag.tag.ape;

import com.judy.audiotag.tag.TagField;
import com.judy.audiotag.tag.TagTextField;
import com.judy.momoplayer.util.Util;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 *
 * @author hadeslee
 */
public class TagItem implements TagTextField {

    private static Logger log = Logger.getLogger(TagItem.class.getName());
    private boolean common;
    private String id;
    private String content;
    private boolean valid;//是否合法
    private int length;//该项的内容的长度
    private int flag;//该项的标志,表明内容是什么,可能是UTF-8字符串也可能是二进制数据
    private int size;//这个项用了多少个字节
    private byte[] raw;

    public TagItem(String id, String content) {
        this.id = id;
        this.content = content;
        valid = true;
        checkCommon();
    }

    public TagItem(byte[] raw, int offset) {
        parseData(raw, offset);
    }

    public boolean isValid() {
        return valid;
    }

    public int getSize() {
        return size;
    }

    private void parseData(byte[] data, int offset) {
        try {
            byte[] temp = new byte[4];
            System.arraycopy(data, offset, temp, 0, 4);
            length = Util.getInt(temp);
            System.arraycopy(data, offset + 4, temp, 0, 4);
            flag = Util.getInt(temp);

            int count = 0;
            size += 8;
            size += length;
            for (int i = 8 + offset; i < data.length; i++) {
//                if(data[i]>=0x20&&data[i]<=0x7E){}
                //只要数据不是0,就一直到后面去
                if (data[i] == 0x00) {
                    break;
                } else {
                    count++;
                }
            }
            id = new String(data, 8 + offset, count, "UTF-8");
            //加上一个空白
            count++;
            size += count;
            content = new String(data, 8 + count + offset, length, "UTF-8");
            valid = true;
            checkCommon();
        } catch (Exception ex) {
            valid = false;
        }
    }

    private void checkCommon() {
        this.common = id.equals(APEv2FieldKey.Title.name()) ||
                id.equals(APEv2FieldKey.Album.name()) ||
                id.equals(APEv2FieldKey.Artist.name()) ||
                id.equals(APEv2FieldKey.Genre.name()) ||
                id.equals(APEv2FieldKey.Year.name()) ||
                id.equals(APEv2FieldKey.Comment.name()) ||
                id.equals(APEv2FieldKey.Track.name());
    }

    public String getContent() {
        return this.content;
    }

    public String getEncoding() {
        return "UTF-8";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setEncoding(String encoding) {
    //什么都不做，因为APE的标签必须是UTF－8编码
    }

    public void copyContent(TagField field) {
        if (field instanceof TagTextField) {
            this.content = ((TagTextField) field).getContent();
        }
    }

    public String getId() {
        return id;
    }

    public byte[] getRawContent() throws UnsupportedEncodingException {
        int index = 0;
        byte[] idData = id.getBytes("UTF-8");
        byte[] contentData = content.getBytes("UTF-8");
        raw = new byte[9 + idData.length + contentData.length];
        byte[] temp = Util.getBytesFromInt(contentData.length);
        //本项目数据部份的长度,4字节
        System.arraycopy(temp, 0, raw, index, 4);
        index += 4;
        temp = new byte[4];
        //中间4个字节留空
        System.arraycopy(temp, 0, raw, index, 4);
        index += 4;
        //项目的键值的字节数组
        System.arraycopy(idData, 0, raw, index, idData.length);
        index += idData.length;
        //一个固定的空白分隔符,跳过一个字节,因为默认就是空白的
        index += 1;
        //项目的内容
        System.arraycopy(contentData, 0, raw, index, contentData.length);
        return raw;
    }

    public boolean isBinary() {
        return false;
    }

    public void isBinary(boolean b) {

    }

    public boolean isCommon() {
        return common;
    }

    public boolean isEmpty() {
        return content == null || content.equals("");
    }

    public String toString() {
        return id + ":" + content;
    }
}
