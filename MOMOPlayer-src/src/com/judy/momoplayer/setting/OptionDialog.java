/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.setting;

/**
 *
 * @author Administrator
 */
import com.judy.momoplayer.util.Config;
import java.awt.Component;
import java.util.logging.Logger;
import javax.swing.JDialog;

public class OptionDialog extends JDialog {

    private static Logger log = Logger.getLogger(OptionDialog.class.getName());
    private ListBar bar;

    /** Creates new form OptionDialog */
    public OptionDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

    }

    public OptionDialog(java.awt.Dialog parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void setSelected(String name) {
        bar.setSelectedComponent(name);
    }

    public void setVisible(boolean b) {
        if (b) {//在其设为可见的时候，更新一下配置
            int count = bar.getBarComponentCount();
            for (int i = 0; i < count; i++) {
                Component com = bar.getBarComponent(i);
                if (com instanceof Initable) {
                    ((Initable) com).init();
                }
            }
        }
        super.setVisible(b);
    }

    private void initComponents() {
        bar = new ListBar();
        bar.addComponent(Config.getResource("OptionDialog.about"), new AboutPanel());
        bar.addComponent(Config.getResource("OptionDialog.view"), new AudioChartPanel());
        bar.addComponent(Config.getResource("OptionDialog.lrc"), new LyricSettingPanel());
        bar.addComponent(Config.getResource("OptionDialog.pl"), new PlayListPanel());
        bar.addComponent(Config.getResource("OptionDialog.playSetting"), new PlayPanel());
        bar.addComponent(Config.getResource("OptionDialog.lyricSearching"), new SearchLyricPanel());
        bar.addComponent(Config.getResource("OptionDialog.normalSetting"), new SettingPanel());
        bar.addComponent(Config.getResource("OptionDialog.webConnection"), new WebConnectPanel());
        bar.setSelectedComponent(Config.getResource("OptionDialog.about"));
        this.add(bar);
        pack();
    }
}
