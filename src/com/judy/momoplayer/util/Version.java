/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

/**
 * 一个抽象版本的对象
 * @author judy
 */
public class Version {

    //private static Logger log = Logger.getLogger(Version.class.getName());
    private String version;
    private String url;
    private String description;

    public Version(String version, String url, String description) {
        this.version = version;
        this.url = url;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String toString() {
        return "version:" + version + ",url:" + url + ",description:" + description;
    }
}
