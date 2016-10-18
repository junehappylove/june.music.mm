/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.player.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.border.Border;

/**
 *
 * @author hadeslee
 */
public class ImageBorder implements Border {

    private final Insets insets;
    private Image image = null;

    public ImageBorder() {
        super();
        this.insets = new Insets(0, 0, 0, 0);
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (image != null) {
            int x0 = x + (width - image.getWidth(null)) / 2;
            int y0 = y + (height - image.getHeight(null)) / 2;
            g.drawImage(image, x0, y0, null);
        }
    }

    public Insets getBorderInsets(Component c) {
        return insets;
    }
}
