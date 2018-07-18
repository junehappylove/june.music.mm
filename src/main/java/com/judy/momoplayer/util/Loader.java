/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

import java.awt.Point;
import javax.swing.JDialog;

/**
 *
 * @author judy
 */
public interface Loader {

    public JDialog changeLrcDialog();

    public void setTitle(String title);

    public void reRange();

    public void loaded();

    public void close();

    public void minimize();

    public Point getLocation();

    public void togglePlaylist(boolean enabled);

    public void toggleEqualizer(boolean enabled);

    public void toggleLyricWindow(boolean enabled);
}
