/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.audiotag.tag.ape;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 内部私有类,它代表了一个APE标签的内容
 * @author hadeslee
 */
public class TagBody {

    private byte[] data;//标签的数据
    private List<TagItem> items;//所有的项
    public TagBody(byte[] data) {
        this.data = data;
        items = new ArrayList<TagItem>();
        parseData();
    }

    public TagBody() {
        items = new ArrayList<TagItem>();
    }

    public List<TagItem> getItems() {
        return items;
    }

    public byte[] getBytes() throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for(TagItem item:items){
            bout.write(item.getRawContent());
        }
        bout.flush();
        return bout.toByteArray();
    }

    public void addTagItem(TagItem item) {
        items.add(item);
    }

    private void parseData() {
        int count = 0;
        byte[] temp = new byte[data.length];
        System.arraycopy(data, 0, temp, 0, data.length);
        while (count < data.length) {
            TagItem item = new TagItem(temp, count);
            if (item.isValid()) {
                count += item.getSize();
                items.add(item);
            } else {
                return;
            }
        }
    }
    }
