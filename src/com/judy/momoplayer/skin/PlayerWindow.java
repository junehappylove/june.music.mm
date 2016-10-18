/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.skin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

/**
 * 一个代表播放器窗口的皮肤对象
 * 此对象里面封装了播放器窗口所需要的一切信息
 * 包括位置以及各个小部件的图片等等
 * @author binfeng.li
 */
public class PlayerWindow {

    private Image windowImage;//窗口的底图
    private Position playPosition;
    private Image playImage;
    private Position pausePosition;
    private Image pauseImage;
    private Position stopPosition;
    private Image stopImage;
    private Position prevPosition;
    private Image prveImage;
    private Position nextPosition;
    private Image nextImage;
    private Position mutePosition;
    private Image muteImage;
    private Position openPosition;
    private Image openImage;
    private Position lyricPosition;
    private Image lyricImage;
    private Position equalizerPosition;
    private Image equalizerImage;
    private Position playlistPosition;
    private Image playlistImage;
    private Position minimizePosition;
    private Image minimizeImage;
    private Position minimodePosition;
    private Image minimodeImage;
    private Position exitPosition;
    private Image exitImage;
    private Position progressPosition;
    private Image progressBarImage;
    private Image progressThumbImage;
    private Image progressFillImage;
    private Position volumePosition;
    private boolean volumeVertical;
    private Image volumeBarImage;
    private Image volumeThumbImage;
    private Image volumeFillImage;
    private Position visualPosition;
    private Image iconImage;
    private Position iconPosition;
    private Position infoPosition;
    private Color infoColor;
    private Font infoFont;
    private Position ledPosition;
    private Image ledImage;
    private String ledAlign;
    private Position stereoPosition;
    private Color stereoColor;
    private Font stereoFont;
    private String stereoAlign;
    private Position statusPosition;
    private Color statusColor;
    private Font statusFont;
    private String statusAlign;

    public Image getEqualizerImage() {
        return equalizerImage;
    }

    public Image getIconImage() {
        return iconImage;
    }

    public void setIconImage(Image iconImage) {
        this.iconImage = iconImage;
    }

    public void setEqualizerImage(Image equalizerImage) {
        this.equalizerImage = equalizerImage;
    }

    public Position getEqualizerPosition() {
        return equalizerPosition;
    }

    public void setEqualizerPosition(Position equalizerPosition) {
        this.equalizerPosition = equalizerPosition;
    }

    public Image getExitImage() {
        return exitImage;
    }

    public void setExitImage(Image exitImage) {
        this.exitImage = exitImage;
    }

    public Position getExitPosition() {
        return exitPosition;
    }

    public void setExitPosition(Position exitPosition) {
        this.exitPosition = exitPosition;
    }

    public Position getIconPosition() {
        return iconPosition;
    }

    public void setIconPosition(Position iconPosition) {
        this.iconPosition = iconPosition;
    }

    public Color getInfoColor() {
        return infoColor;
    }

    public void setInfoColor(Color infoColor) {
        this.infoColor = infoColor;
    }

    public Font getInfoFont() {
        return infoFont;
    }

    public void setInfoFont(Font infoFont) {
        this.infoFont = infoFont;
    }

    public Position getInfoPosition() {
        return infoPosition;
    }

    public void setInfoPosition(Position infoPosition) {
        this.infoPosition = infoPosition;
    }

    public String getLedAlign() {
        return ledAlign;
    }

    public void setLedAlign(String ledAlign) {
        this.ledAlign = ledAlign;
    }

    public Image getLedImage() {
        return ledImage;
    }

    public void setLedImage(Image ledImage) {
        this.ledImage = ledImage;
    }

    public Position getLedPosition() {
        return ledPosition;
    }

    public void setLedPosition(Position ledPosition) {
        this.ledPosition = ledPosition;
    }

    public Image getLyricImage() {
        return lyricImage;
    }

    public void setLyricImage(Image lyricImage) {
        this.lyricImage = lyricImage;
    }

    public Position getLyricPosition() {
        return lyricPosition;
    }

    public void setLyricPosition(Position lyricPosition) {
        this.lyricPosition = lyricPosition;
    }

    public Image getMinimizeImage() {
        return minimizeImage;
    }

    public void setMinimizeImage(Image minimizeImage) {
        this.minimizeImage = minimizeImage;
    }

    public Position getMinimizePosition() {
        return minimizePosition;
    }

    public void setMinimizePosition(Position minimizePosition) {
        this.minimizePosition = minimizePosition;
    }

    public Image getMinimodeImage() {
        return minimodeImage;
    }

    public void setMinimodeImage(Image minimodeImage) {
        this.minimodeImage = minimodeImage;
    }

    public Position getMinimodePosition() {
        return minimodePosition;
    }

    public void setMinimodePosition(Position minimodePosition) {
        this.minimodePosition = minimodePosition;
    }

    public Image getMuteImage() {
        return muteImage;
    }

    public void setMuteImage(Image muteImage) {
        this.muteImage = muteImage;
    }

    public Position getMutePosition() {
        return mutePosition;
    }

    public void setMutePosition(Position mutePosition) {
        this.mutePosition = mutePosition;
    }

    public Image getNextImage() {
        return nextImage;
    }

    public void setNextImage(Image nextImage) {
        this.nextImage = nextImage;
    }

    public Position getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(Position nextPosition) {
        this.nextPosition = nextPosition;
    }

    public Image getOpenImage() {
        return openImage;
    }

    public void setOpenImage(Image openImage) {
        this.openImage = openImage;
    }

    public Position getOpenPosition() {
        return openPosition;
    }

    public void setOpenPosition(Position openPosition) {
        this.openPosition = openPosition;
    }

    public Image getPauseImage() {
        return pauseImage;
    }

    public void setPauseImage(Image pauseImage) {
        this.pauseImage = pauseImage;
    }

    public Position getPausePosition() {
        return pausePosition;
    }

    public void setPausePosition(Position pausePosition) {
        this.pausePosition = pausePosition;
    }

    public Image getPlayImage() {
        return playImage;
    }

    public void setPlayImage(Image playImage) {
        this.playImage = playImage;
    }

    public Position getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(Position playPosition) {
        this.playPosition = playPosition;
    }

    public Image getPlaylistImage() {
        return playlistImage;
    }

    public void setPlaylistImage(Image playlistImage) {
        this.playlistImage = playlistImage;
    }

    public Position getPlaylistPosition() {
        return playlistPosition;
    }

    public void setPlaylistPosition(Position playlistPosition) {
        this.playlistPosition = playlistPosition;
    }

    public Position getPrevPosition() {
        return prevPosition;
    }

    public void setPrevPosition(Position prevPosition) {
        this.prevPosition = prevPosition;
    }

    public Image getProgressBarImage() {
        return progressBarImage;
    }

    public void setProgressBarImage(Image progressBarImage) {
        this.progressBarImage = progressBarImage;
    }

    public Image getProgressFillImage() {
        return progressFillImage;
    }

    public void setProgressFillImage(Image progressFillImage) {
        this.progressFillImage = progressFillImage;
    }

    public Position getProgressPosition() {
        return progressPosition;
    }

    public void setProgressPosition(Position progressPosition) {
        this.progressPosition = progressPosition;
    }

    public Image getProgressThumbImage() {
        return progressThumbImage;
    }

    public void setProgressThumbImage(Image progressThumbImage) {
        this.progressThumbImage = progressThumbImage;
    }

    public Image getPrveImage() {
        return prveImage;
    }

    public void setPrveImage(Image prveImage) {
        this.prveImage = prveImage;
    }

    public String getStatusAlign() {
        return statusAlign;
    }

    public void setStatusAlign(String statusAlign) {
        this.statusAlign = statusAlign;
    }

    public Color getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(Color statusColor) {
        this.statusColor = statusColor;
    }

    public Font getStatusFont() {
        return statusFont;
    }

    public void setStatusFont(Font statusFont) {
        this.statusFont = statusFont;
    }

    public Position getStatusPosition() {
        return statusPosition;
    }

    public void setStatusPosition(Position statusPosition) {
        this.statusPosition = statusPosition;
    }

    public String getStereoAlign() {
        return stereoAlign;
    }

    public void setStereoAlign(String stereoAlign) {
        this.stereoAlign = stereoAlign;
    }

    public Color getStereoColor() {
        return stereoColor;
    }

    public void setStereoColor(Color stereoColor) {
        this.stereoColor = stereoColor;
    }

    public Font getStereoFont() {
        return stereoFont;
    }

    public void setStereoFont(Font stereoFont) {
        this.stereoFont = stereoFont;
    }

    public Position getStereoPosition() {
        return stereoPosition;
    }

    public void setStereoPosition(Position stereoPosition) {
        this.stereoPosition = stereoPosition;
    }

    public Image getStopImage() {
        return stopImage;
    }

    public void setStopImage(Image stopImage) {
        this.stopImage = stopImage;
    }

    public Position getStopPosition() {
        return stopPosition;
    }

    public void setStopPosition(Position stopPosition) {
        this.stopPosition = stopPosition;
    }

    public Position getVisualPosition() {
        return visualPosition;
    }

    public void setVisualPosition(Position visualPosition) {
        this.visualPosition = visualPosition;
    }

    public Image getVolumeBarImage() {
        return volumeBarImage;
    }

    public void setVolumeBarImage(Image volumeBarImage) {
        this.volumeBarImage = volumeBarImage;
    }

    public Image getVolumeFillImage() {
        return volumeFillImage;
    }

    public void setVolumeFillImage(Image volumeFillImage) {
        this.volumeFillImage = volumeFillImage;
    }

    public Position getVolumePosition() {
        return volumePosition;
    }

    public void setVolumePosition(Position volumePosition) {
        this.volumePosition = volumePosition;
    }

    public Image getVolumeThumbImage() {
        return volumeThumbImage;
    }

    public void setVolumeThumbImage(Image volumeThumbImage) {
        this.volumeThumbImage = volumeThumbImage;
    }

    public boolean isVolumeVertical() {
        return volumeVertical;
    }

    public void setVolumeVertical(boolean volumeVertical) {
        this.volumeVertical = volumeVertical;
    }

    public Image getWindowImage() {
        return windowImage;
    }

    public void setWindowImage(Image windowImage) {
        this.windowImage = windowImage;
    }
}
