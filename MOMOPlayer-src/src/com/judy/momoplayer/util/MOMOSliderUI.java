/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 *
 * @author hadeslee
 */
public class MOMOSliderUI extends BasicSliderUI {

    private Image thumbImage = null;//普通按的图片
    private Image thumbOverImage;//鼠标在上面的图片
    private Image thumbPressedImage = null;//按下面的图片
    private Image backgroundImage = null;//普通背景图片
    private Image activeBackImage;//激活的背景图片
    private JSlider parentSlider = null;
    private Dimension thumbDim = null;
    private int newThumbHeight = -1;
    private int thumbXOffset = 0;
    private int thumbYOffset = 0;
    private boolean hideThumb = false;

    public MOMOSliderUI(JSlider slider) {
        super(slider);
        parentSlider = slider;
    }

    public void setThumbImage(Image img) {
        thumbImage = img;
        thumbDim = new Dimension(thumbImage.getWidth(null), thumbImage.getHeight(null));
    }

    public void setThumbPressedImage(Image img) {
        thumbPressedImage = img;
    }

    public void setActiveBackImage(Image activeBackImage) {
        this.activeBackImage = activeBackImage;
    }

    public void setThumbOverImage(Image thumbOverImage) {
        this.thumbOverImage = thumbOverImage;
    }

    @Override
    protected Dimension getThumbSize() {
        return thumbDim;
    }

    public void forceThumbHeight(int h) {
        newThumbHeight = h;
    }

    public void setThumbXOffset(int x) {
        thumbXOffset = x;
    }

    public void setThumbYOffset(int y) {
        thumbYOffset = y;
    }

    public void setHideThumb(boolean hide) {
        hideThumb = hide;
    }

    public void setBackgroundImages(Image img) {
        backgroundImage = img;
    }

    @Override
    public void paintFocus(Graphics g) {
    }

    @Override
    public void paintThumb(Graphics g) {
        if (hideThumb == true) {
            return;
        }
        Image img = thumbImage;
        if (img != null) {
            if (thumbPressedImage != null) {
                if (parentSlider.getValueIsAdjusting()) {
                    img = thumbPressedImage;
                }
            }
            if (newThumbHeight >= 0) {
                if (parentSlider.getOrientation() == JSlider.HORIZONTAL) {
                    g.drawImage(img, thumbRect.x + thumbXOffset, (parentSlider.getHeight() - img.getHeight(slider)) / 2, img.getWidth(null), newThumbHeight, null);
                } else {
                    g.drawImage(img, (parentSlider.getWidth() - img.getWidth(slider)) / 2, thumbRect.y + thumbYOffset, img.getWidth(null), newThumbHeight, null);
                }
            } else {
                if (parentSlider.getOrientation() == JSlider.HORIZONTAL) {
                    g.drawImage(img, thumbRect.x + thumbXOffset, (parentSlider.getHeight() - img.getHeight(slider)) / 2, img.getWidth(null), img.getHeight(null), null);
                } else {
                    g.drawImage(img, (parentSlider.getWidth() - img.getWidth(slider)) / 2, thumbRect.y + thumbYOffset, img.getWidth(null), img.getHeight(null), null);
                }
            }
        }
    }

    @Override
    public void paintTrack(Graphics g) {
        int width = parentSlider.getWidth();
        int height = parentSlider.getHeight();
        int all = parentSlider.getMaximum() - parentSlider.getMinimum();
        int value = parentSlider.getValue();
        if (backgroundImage != null) {
            if (parentSlider.getOrientation() == JSlider.HORIZONTAL) {
                g.drawImage(backgroundImage, 0, (height - backgroundImage.getHeight(slider)) / 2, parentSlider);
            } else {
                g.drawImage(backgroundImage, (width - backgroundImage.getWidth(parentSlider)) / 2, 0, parentSlider);
            }
        }
        if (activeBackImage != null) {
            if (parentSlider.getOrientation() == JSlider.HORIZONTAL) {
                g.drawImage(activeBackImage, 0, (height - activeBackImage.getHeight(slider)) / 2, width * value / all, activeBackImage.getHeight(slider), 0, 0, width * value / all, activeBackImage.getHeight(slider), slider);
            } else {
                int sx = (width - activeBackImage.getWidth(parentSlider)) / 2;
                g.drawImage(activeBackImage, sx, height - (height * value / all), sx + activeBackImage.getWidth(slider), height, 0, height - (height * value / all), activeBackImage.getWidth(slider), height, slider);
            }
        }
    }

    @Override
    public void setThumbLocation(int x, int y) {
        super.setThumbLocation(x, y);
        parentSlider.repaint();
    }
}
