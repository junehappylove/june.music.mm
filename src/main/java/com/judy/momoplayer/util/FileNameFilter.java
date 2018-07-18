/*
 * FileNameFilter.
 *
 * JavaZOOM : jlgui@javazoom.net
 *            http://www.javazoom.net
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package com.judy.momoplayer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * FileName filter that works for both javax.swing.filechooser and java.io.
 */
public class FileNameFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

    protected ArrayList<String> extensions = new ArrayList<String>();
    protected String default_extension = null;
    protected String description;
    protected boolean allowDir = true;

    /**
     * Constructs the list of extensions out of a string of comma-separated
     * elements, each of which represents one extension.
     *
     * @param ext the list of comma-separated extensions
     */
    public FileNameFilter(String ext, String description) {
        this(ext, description, true);
    }

    public FileNameFilter(String ext, String description, boolean allowDir) {
        this.description = description;
        this.allowDir = allowDir;
        StringTokenizer st = new StringTokenizer(ext, ",");
        String extension;
        while (st.hasMoreTokens()) {
            extension = st.nextToken();
            extensions.add(extension.trim());
            if (default_extension == null) {
                default_extension = extension;
            }
        }
    }

    /**
     * determines if the filename is an acceptable one. If a
     * filename ends with one of the extensions the filter was
     * initialized with, then the function returns true. if not,
     * the function returns false.
     *
     * @param dir the directory the file is in
     * @return true if the filename has a valid extension, false otherwise
     */
    public boolean accept(File dir) {
        for (int i = 0; i < extensions.size(); i++) {//System.out.println("." + (String) extensions.get(i));
            if (allowDir) {
                if (dir.isDirectory() || dir.getName().toLowerCase().endsWith("." + (String) extensions.get(i))) {
                    return true;
                }
            } else {
                if (dir.isFile()&&dir.getName().toLowerCase().endsWith("." + (String) extensions.get(i))) {
                    return true;
                }
            }
        }
        return extensions.size() == 0;
    }

    /**
     * Returns the default extension.
     *
     * @return the default extension
     */
    public String getDefaultExtension() {
        return default_extension;
    }

    public void setDefaultExtension(String ext) {
        default_extension = ext;
    }

    public String getDescription() {
        return description;
    }
}
