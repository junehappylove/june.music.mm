/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.playlist;

import javax.swing.JLabel;
import javax.swing.plaf.LabelUI;

/**
 *
 * @author hadeslee
 */
public class MOMOLabel extends JLabel {

    private static final long serialVersionUID = 20071214L;
    private PlayListItem item;//要画的项目 
    private int index;//要画的下标
    private boolean isSelected;//是否被选中了
    private boolean hasFocus;//是否有了焦点
    private boolean uiseted;//UI是否已经设了
    private int itemCount;//要画的项目的总数
    public MOMOLabel() {
        this.setUI(new MOMOLabelUI(this));
        uiseted = true;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public void setHasFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
    
    @Override
    public void setUI(LabelUI ui) {
        if (!uiseted) {
            super.setUI(new MOMOLabelUI(this));
        }
    }

    public void setPlayListItem(PlayListItem item) {
        this.item = item;
    }

    public PlayListItem getPlayListItem() {
        return item;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean hasFocus) {
        this.isSelected = hasFocus;
    }
}
