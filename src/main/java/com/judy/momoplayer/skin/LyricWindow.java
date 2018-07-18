/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.judy.momoplayer.skin;

import java.awt.Color;
import java.awt.Image;

/**
 * 歌词窗品的信息对象
 * @author binfeng.li
 */
public class LyricWindow {
    private Position windowPosition;
    private Position resizeRect;
    private Image windowImage;
    private Color miniBorderLeftTopColor;
    private Color miniBorderRightBottomColor;
    private Position lyricPosition;
    private Position titlePosition;
    private Image titleImage;
    private String titleAlign;
    private Position closePosition;
    private Image closeImage;
    private String closeAlign;
    private Position ontopPosition;
    private Image ontopImage;
    private String ontopAlign;

    public LyricWindow() {
    }

    public String getCloseAlign() {
        return closeAlign;
    }

    public void setCloseAlign(String closeAlign) {
        this.closeAlign = closeAlign;
    }

    public Image getCloseImage() {
        return closeImage;
    }

    public void setCloseImage(Image closeImage) {
        this.closeImage = closeImage;
    }

    public Position getClosePosition() {
        return closePosition;
    }

    public void setClosePosition(Position closePosition) {
        this.closePosition = closePosition;
    }

    public Position getLyricPosition() {
        return lyricPosition;
    }

    public void setLyricPosition(Position lyricPosition) {
        this.lyricPosition = lyricPosition;
    }

    public Color getMiniBorderLeftTopColor() {
        return miniBorderLeftTopColor;
    }

    public void setMiniBorderLeftTopColor(Color miniBorderLeftTopColor) {
        this.miniBorderLeftTopColor = miniBorderLeftTopColor;
    }

    public Color getMiniBorderRightBottomColor() {
        return miniBorderRightBottomColor;
    }

    public void setMiniBorderRightBottomColor(Color miniBorderRightBottomColor) {
        this.miniBorderRightBottomColor = miniBorderRightBottomColor;
    }

    public String getOntopAlign() {
        return ontopAlign;
    }

    public void setOntopAlign(String ontopAlign) {
        this.ontopAlign = ontopAlign;
    }

    public Image getOntopImage() {
        return ontopImage;
    }

    public void setOntopImage(Image ontopImage) {
        this.ontopImage = ontopImage;
    }

    public Position getOntopPosition() {
        return ontopPosition;
    }

    public void setOntopPosition(Position ontopPosition) {
        this.ontopPosition = ontopPosition;
    }

    public Position getResizeRect() {
        return resizeRect;
    }

    public void setResizeRect(Position resizeRect) {
        this.resizeRect = resizeRect;
    }

    public String getTitleAlign() {
        return titleAlign;
    }

    public void setTitleAlign(String titleAlign) {
        this.titleAlign = titleAlign;
    }

    public Image getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(Image titleImage) {
        this.titleImage = titleImage;
    }

    public Position getTitlePosition() {
        return titlePosition;
    }

    public void setTitlePosition(Position titlePosition) {
        this.titlePosition = titlePosition;
    }

    public Image getWindowImage() {
        return windowImage;
    }

    public void setWindowImage(Image windowImage) {
        this.windowImage = windowImage;
    }

    public Position getWindowPosition() {
        return windowPosition;
    }

    public void setWindowPosition(Position windowPosition) {
        this.windowPosition = windowPosition;
    }
    
}
