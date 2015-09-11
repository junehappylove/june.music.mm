/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.skin;

import java.awt.Color;
import java.awt.Image;

/**
 *
 * @author binfeng.li
 */
public class PlaylistWindow {

    private Position windowPosition;
    private Position resizeRect;
    private Image windowImage;
    private Color hilightColor;
    private Position titlePosition;
    private Image titleImage;
    private String titleAlign;
    private Position closePosition;
    private Image closeImage;
    private String closeAlign;
    private Position toolbarPosition;
    private Image toolbarImage;
    private Image toolbarHotImage;
    private Image scrollbarButtonsImage;
    private Image scrollbarThumbImage;
    private Image scrollbarBarImage;
    private Position playlistPosition;

    public PlaylistWindow() {
    }

    public Color getHilightColor() {
        return hilightColor;
    }

    public void setHilightColor(Color hilightColor) {
        this.hilightColor = hilightColor;
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

    public Position getPlaylistPosition() {
        return playlistPosition;
    }

    public void setPlaylistPosition(Position playlistPosition) {
        this.playlistPosition = playlistPosition;
    }

    public Position getResizeRect() {
        return resizeRect;
    }

    public void setResizeRect(Position resizeRect) {
        this.resizeRect = resizeRect;
    }

    public Image getScrollbarBarImage() {
        return scrollbarBarImage;
    }

    public void setScrollbarBarImage(Image scrollbarBarImage) {
        this.scrollbarBarImage = scrollbarBarImage;
    }

    public Image getScrollbarButtonsImage() {
        return scrollbarButtonsImage;
    }

    public void setScrollbarButtonsImage(Image scrollbarButtonsImage) {
        this.scrollbarButtonsImage = scrollbarButtonsImage;
    }

    public Image getScrollbarThumbImage() {
        return scrollbarThumbImage;
    }

    public void setScrollbarThumbImage(Image scrollbarThumbImage) {
        this.scrollbarThumbImage = scrollbarThumbImage;
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

    public Image getToolbarHotImage() {
        return toolbarHotImage;
    }

    public void setToolbarHotImage(Image toolbarHotImage) {
        this.toolbarHotImage = toolbarHotImage;
    }

    public Image getToolbarImage() {
        return toolbarImage;
    }

    public void setToolbarImage(Image toolbarImage) {
        this.toolbarImage = toolbarImage;
    }

    public Position getToolbarPosition() {
        return toolbarPosition;
    }

    public void setToolbarPosition(Position toolbarPosition) {
        this.toolbarPosition = toolbarPosition;
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
