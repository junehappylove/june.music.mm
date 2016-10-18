/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.player.ui;

import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;
import com.judy.momoplayer.util.Version;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 检测软件更新类
 *
 * @author June
 */
class CheckThread extends Thread {

    private static final Logger log = Logger.getLogger(CheckThread.class.getName());

    /**
     * 检查软件更新
     */
    private void checkUpdate() {
        String s = Config.getConfig().getCheckUpdateStrategy();//获取跟新策略，每天、每周，每月，不检查等
        Date date = Config.getConfig().getLastCheckUpdate();//上次检查日期
        if (date == null) {
            date = new Date();
        }
        Calendar last = Calendar.getInstance();
        last.setTime(date);
        if (s.equals(Config.CHECK_DAY)) {
            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.YEAR) == last.get(Calendar.YEAR)
                    && now.get(Calendar.MONTH) == last.get(Calendar.MONTH)
                    && now.get(Calendar.DAY_OF_MONTH) == last.get(Calendar.DAY_OF_MONTH)) {
                //如果年月日都相等,则不要比较了,今天已经比过了
            } else {
                Version ver = null;//Util.getRemoteVersion();//获取远程版本信息，这里已经不维护了
                log.log(Level.INFO, "每天检查版本信息");
                if (ver != null) {
                    Config.getConfig().setLastCheckUpdate(new Date());
                    Util.checkUpdate(ver, true);
                }
            }
        } else if (s.equals(Config.CHECK_MONTH)) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MONTH, 1);
            if (now.before(last)) {
                Version ver = null;//Util.getRemoteVersion();//获取远程版本信息，这里已经不维护了
                log.log(Level.INFO, "每月检查版本信息");
                if (ver != null) {
                    Config.getConfig().setLastCheckUpdate(new Date());
                    Util.checkUpdate(ver, true);
                }
            }
        } else if (s.equals(Config.CHECK_WEEK)) {
            Calendar now = Calendar.getInstance();
            now.add(Calendar.WEEK_OF_YEAR, 1);
            if (now.before(last)) {
                Version ver = null;//Util.getRemoteVersion();//获取远程版本信息，这里已经不维护了
                log.log(Level.INFO, "每周检查版本信息");
                if (ver != null) {
                    Config.getConfig().setLastCheckUpdate(new Date());
                    Util.checkUpdate(ver, true);
                }
            }
        } else if (s.equals(Config.CHECK_NONE)) {
            //什么都不做
            log.log(Level.INFO, "从不检查版本信息");
        }
    }

    @Override
    public void run() {
        long last = System.currentTimeMillis();
        if (!Util.voteOpen()) {
            int count = Config.getConfig().voteOpenCount++;
            System.out.println("voteOpentCount00：" + count);
        }
        while (true) {
            try {
                Thread.sleep(600000); //线程睡眠10min
                if (Config.getConfig().voteOpenCount > 0 && Util.voteOpen()) {
                    int count = Config.getConfig().voteOpenCount--;
                    System.out.println("voteOpentCount11：" + count);
                }
                if (Config.getConfig().voteOneHourCount > 0 && Util.voteOneHour()) {
                    int count = Config.getConfig().voteOneHourCount--;
                    System.out.println("voteOneHourCount11：" + count);
                }
                if (System.currentTimeMillis() - last > 3600000) {
                    last = System.currentTimeMillis();
                    if (!Util.voteOneHour()) {
                        int count = Config.getConfig().voteOneHourCount++;
                        System.out.println("voteOneHourCount00：" + count);
                    }
                }
                checkUpdate();
            } catch (InterruptedException ex) {
                Logger.getLogger(CheckThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
