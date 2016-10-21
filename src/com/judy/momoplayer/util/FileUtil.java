/*
 * FileUtil.
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
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Scott Pennell
 */
public class FileUtil {

    private static List<String> supportedExtensions = null;

    public static File[] findFilesRecursively(File directory) {
        if (directory.isFile()) {
            File[] f = new File[1];
            f[0] = directory;
            return f;
        }
        List<File> list = new ArrayList<File>();
        addSongsRecursive(list, directory);
        return ((File[]) list.toArray(new File[list.size()]));
    }

    private static void addSongsRecursive(List<File> found, File rootDir) {
        if (rootDir == null) {
            return;
        } // we do not want waste time
        File[] files = rootDir.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(rootDir, files[i].getName());
            if (file.isDirectory()) {
                addSongsRecursive(found, file);
            } else {
                if (isMusicFile(files[i])) {
                    found.add(file);
                }
            }
        }
    }

    public static boolean isMusicFile(File f) {
        List<String> exts = getSupportedExtensions();
        int sz = exts.size();
        String ext;
        String name = f.getName();
        for (int i = 0; i < sz; i++) {
            ext = (String) exts.get(i);
            if (ext.equals(".wsz") || ext.equals(".m3u")) {
                continue;
            }
            if (name.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getSupportedExtensions() {
        if (supportedExtensions == null) {
            String ext = Config.getConfig().getExtensions();
            StringTokenizer st = new StringTokenizer(ext, ",");
            supportedExtensions = new ArrayList<String>();
            while (st.hasMoreTokens()) {
                supportedExtensions.add("." + st.nextElement());
            }
        }
        return (supportedExtensions);
    }

    public static String getSupprtedExtensions() {
        List<String> exts = getSupportedExtensions();
        StringBuffer s = new StringBuffer();
        int sz = exts.size();
        String ext;
        for (int i = 0; i < sz; i++) {
            ext = (String) exts.get(i);
            if (ext.equals(".wsz") || ext.equals(".m3u")) {
                continue;
            }
            if (i == 0) {
                s.append(ext);
            } else {
                s.append(";").append(ext);
            }
        }
        return s.toString();
    }

    public static String padString(String s, int length) {
        return padString(s, ' ', length);
    }

    public static String padString(String s, char padChar, int length) {
        int slen, numPads = 0;
        if (s == null) {
            s = "";
            numPads = length;
        } else if ((slen = s.length()) > length) {
            s = s.substring(0, length);
        } else if (slen < length) {
            numPads = length - slen;
        }
        if (numPads == 0) {
            return s;
        }
        char[] c = new char[numPads];
        Arrays.fill(c, padChar);
        return s + new String(c);
    }

    public static String rightPadString(String s, int length) {
        return (rightPadString(s, ' ', length));
    }

    public static String rightPadString(String s, char padChar, int length) {
        int slen, numPads = 0;
        if (s == null) {
            s = "";
            numPads = length;
        } else if ((slen = s.length()) > length) {
            s = s.substring(length);
        } else if (slen < length) {
            numPads = length - slen;
        }
        if (numPads == 0) {
            return (s);
        }
        char[] c = new char[numPads];
        Arrays.fill(c, padChar);
        return new String(c) + s;
    }
}
