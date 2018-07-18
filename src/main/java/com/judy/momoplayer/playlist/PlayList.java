/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.playlist;

import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author judy
 */
/**
 * 一个播放列表的接口,实现者必须所有方法的实现
 * 这些方法都是可以被PlayListUI来调用的,以保证
 * 视图和模型层的分离
 */
public interface PlayList extends Serializable {

    /**
     * Loads playlist.
     */
    public boolean load(String filename);

    /**
     * Saves playlist.
     */
    public boolean save(String filename);

    /**
     * Adds item at a given position in the playlist.
     */
    public void addItemAt(PlayListItem pli, int pos);

    /**
     * Searchs and removes item from the playlist.
     */
    public void removeItem(PlayListItem pli);

    /**
     * Removes item at a given position from the playlist.
     */
    public void removeItemAt(int pos);

    /**
     * Removes all items in the playlist.
     */
    public void removeAllItems();

    /**
     * Append item at the end of the playlist.
     */
    public void appendItem(PlayListItem pli);

    /**
     * Sorts items of the playlist.
     */
    public void sortItems(int sortmode);

    /**
     * Returns item at a given position from the playlist.
     */
    public PlayListItem getItemAt(int pos);

    /**
     * Returns a collection of playlist items.
     */
    public Vector<PlayListItem> getAllItems();

    /**
     * Returns then number of items in the playlist.
     */
    public int getPlaylistSize();

    // Next methods will be used by the Player
    /**
     * Randomly re-arranges the playlist.
     */
    public void shuffle();

    /**
     * Returns item matching to the cursor.
     */
    public PlayListItem getCursor();

    /**
     * Moves the cursor at the begining of the Playlist.
     */
    public void begin();

    /**
     * Returns item matching to the cursor.
     */
    public int getSelectedIndex();

    /**
     * Returns index of playlist item.
     */
    public int getIndex(PlayListItem pli);

    /**
     * Computes cursor position (next).
     */
    public void nextCursor();

    /**
     * Computes cursor position (previous).
     */
    public void previousCursor();

    /**
     * Set the modification flag for the playlist
     */
    boolean setModified(boolean set);

    /**
     * Checks the modification flag
     */
    public boolean isModified();

    /**
     * 设置当前正在播放的位置
     * @param index 位置
     */
    public void setCursor(int index);

    /**
     * 设置这个播放列表的名字
     * @param name 名字
     */
    public void setName(String name);

    /**
     * 得到这个播放列表的名字
     * @return
     */
    public String getName();

    /**
     * 设置某个选项为选中
     * @param pl
     */
    public void setItemSelected(PlayListItem pl,int index);
}

