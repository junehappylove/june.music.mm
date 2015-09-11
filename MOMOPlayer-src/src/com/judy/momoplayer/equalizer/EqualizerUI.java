/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.judy.momoplayer.equalizer;

import com.judy.momoplayer.player.ui.ImageBorder;
import com.judy.momoplayer.player.ui.PlayerUI;
import com.judy.momoplayer.util.Config;
import com.judy.momoplayer.util.Util;
import com.judy.momoplayer.util.MOMOSlider;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 均衡器UI
 * @author june
 */
public class EqualizerUI extends JPanel implements ActionListener, ChangeListener {

    private static final long serialVersionUID = 20071214L;
    private static final Logger log = Logger.getLogger(EqualizerUI.class.getName());
    private final int minGain = 0;
    private final int maxGain = 100;
    private final int[] gainValue = {50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50};
    private static final int[] PRESET_NORMAL = {50, 50, 50, 50, 50, 50, 50, 50, 50, 50};
    private static final int[] PRESET_CLASSICAL = {50, 50, 50, 50, 50, 50, 70, 70, 70, 76};
    private static final int[] PRESET_CLUB = {50, 50, 42, 34, 34, 34, 42, 50, 50, 50};
    private static final int[] PRESET_DANCE = {26, 34, 46, 50, 50, 66, 70, 70, 50, 50};
    private static final int[] PRESET_FULLBASS = {26, 26, 26, 36, 46, 62, 76, 78, 78, 78};
    private static final int[] PRESET_FULLBASSTREBLE = {34, 34, 50, 68, 62, 46, 28, 22, 18, 18};
    private static final int[] PRESET_FULLTREBLE = {78, 78, 78, 62, 42, 24, 8, 8, 8, 8};
    private static final int[] PRESET_LAPTOP = {38, 22, 36, 60, 58, 46, 38, 24, 16, 14};
    private static final int[] PRESET_LIVE = {66, 50, 40, 36, 34, 34, 40, 42, 42, 42};
    private static final int[] PRESET_PARTY = {32, 32, 50, 50, 50, 50, 50, 50, 32, 32};
    private static final int[] PRESET_POP = {56, 38, 32, 30, 38, 54, 56, 56, 54, 54};
    private static final int[] PRESET_REGGAE = {48, 48, 50, 66, 48, 34, 34, 48, 48, 48};
    private static final int[] PRESET_ROCK = {32, 38, 64, 72, 56, 40, 28, 24, 24, 24};
    private static final int[] PRESET_TECHNO = {30, 34, 48, 66, 64, 48, 30, 24, 24, 28};
    private Config config = null;
    private PlayerUI player = null;
    private JPopupMenu mainpopup = null;
    public static final int LINEARDIST = 1;
    public static final int OVERDIST = 2;
    private float[] bands = null;
    private int[] eqgains = null;
    private int eqdist = OVERDIST;
    private JToggleButton onoff, auto;
    private JButton preset;
    private final MOMOSlider[] sliders;

    private boolean initDone;//是否初始化完成了
    public static String[] presets = {"Normal", "Classical", "Club", "Dance", 
        "Full Bass", "Full Bass & Treble", "Full Treble", "Laptop", "Live",
        "Party", "Pop", "Reggae", "Rock", "Techno"};

    public EqualizerUI() {
        super(null);
        setDoubleBuffered(true);
        this.setPreferredSize(new Dimension(285, 155));
        config = Config.getInstance();
        eqgains = new int[10];
        sliders = new MOMOSlider[11];
        int[] vals = config.getLastEqualizer();
        if (vals != null) {
            log.log(Level.INFO, "均衡器不为NULL");

//            for (int h = 0; h < vals.length; h++) {
//                gainValue[h] = vals[h];
//            }
            System.arraycopy(vals, 0, gainValue, 0, vals.length);

        } else {
            log.log(Level.INFO, "均衡器是空的！！");
        }

    }

    /**
     * Set parent player.
     *
     * @param mp
     */
    public void setPlayer(PlayerUI mp) {
        player = mp;
    }

    public JToggleButton getAutoButton() {
        return auto;
    }

    public JToggleButton getOnOffButton() {
        return onoff;
    }

    public void loadUI() {
        removeAll();
        // Background
        ImageBorder border = new ImageBorder();
        border.setImage(Util.getImage("equalizer/eqbg.png"));
        setBorder(border);
        onoff = Util.createJToggleButton("equalizer/on", Config.EQ_ENABLE, this, config.isEqualizerOn());
        auto = Util.createJToggleButton("equalizer/auto", Config.EQ_AUTO_ENABLE, this, config.isEqualizerAuto());
        preset = Util.createJButton("equalizer/presets", Config.EQ_PRESET, this);
//        close = Util.createJButton("player/close", Config.CLOSE, this);

        onoff.setBounds(11, 13, 47, 24);
        auto.setBounds(66, 13, 47, 24);
        preset.setBounds(151, 13, 75, 25);
//        close.setBounds(253, 13, 15, 15);

        this.add(onoff);
        this.add(auto);
        this.add(preset);
//        this.add(close);

        Image ball = Util.getImage("equalizer/ball.png");
        Image bg1 = Util.getImage("equalizer/bg1.png");
        Image bg2 = Util.getImage("equalizer/bg2.png");
        sliders[0] = Util.createSlider(0, 100, 0, ball, null, null,
                bg1, bg2, this, SwingConstants.VERTICAL);
        sliders[0].setBounds(14, 40, 7, 86);
        sliders[0].setValue(maxGain - gainValue[0]);
        this.add(sliders[0]);
        for (int i = 1; i < sliders.length; i++) {
            sliders[i] = Util.createSlider(0, 100, 0, ball, null, null,
                    bg1, bg2, this, SwingConstants.VERTICAL);
            sliders[i].setValue(maxGain - gainValue[i]);
            sliders[i].setBounds((68 + (i - 1) * 21), 40, 7, 86);
            this.add(sliders[i]);
        }

        // Popup menu on TitleBar
        mainpopup = new JPopupMenu();
        JMenuItem mi;
        for (String preset1 : presets) {
            mi = new JMenuItem(Config.getResource(preset1));
            mi.addActionListener(this);
            mi.setActionCommand(preset1);
            mainpopup.add(mi);
        }

        validate();
        initDone = true;
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        if (initDone) {
            for (int i = 0; i < sliders.length; i++) {
                gainValue[i] = maxGain - sliders[i].getValue();
            }
            // Apply equalizer values.
            synchronizeEqualizer();
        }

    }

    /**
     * Set bands array for equalizer.
     *
     * @param bands
     */
    public void setBands(float[] bands) {
        if (this.bands != bands) {
            this.bands = bands;
            synchronizeEqualizer();
        }

    }

    /**
     * Apply equalizer function.
     *
     * @param gains
     * @param min
     * @param max
     */
    public void updateBands(int[] gains, int min, int max) {
        if ((gains != null) && (bands != null)) {
            int j = 0;
            float gvalj = (gains[j] * 2.0f / (max - min) * 1.0f) - 1.0f;
            float gvalj1 = (gains[j + 1] * 2.0f / (max - min) * 1.0f) - 1.0f;
            // Linear distribution : 10 values => 32 values.
            if (eqdist == LINEARDIST) {
                float a = (gvalj1 - gvalj) * 1.0f;
                float b = gvalj * 1.0f - (gvalj1 - gvalj) * j;
                // x=s*x'
                float s = (gains.length - 1) * 1.0f / (bands.length - 1) * 1.0f;
                for (int i = 0; i < bands.length; i++) {
                    float ind = s * i;
                    if (ind > (j + 1)) {
                        j++;
                        gvalj = (gains[j] * 2.0f / (max - min) * 1.0f) - 1.0f;
                        gvalj1 = (gains[j + 1] * 2.0f / (max - min) * 1.0f) - 1.0f;
                        a = (gvalj1 - gvalj) * 1.0f;
                        b = gvalj * 1.0f - (gvalj1 - gvalj) * j;
                    }
                    // a*x+b
                    bands[i] = a * i * 1.0f * s + b;
                }
            } // Over distribution : 10 values => 10 first value of 32 values.
            else if (eqdist == OVERDIST) {
                for (int i = 0; i < gains.length; i++) {
                    bands[i] = (gains[i] * 2.0f / (max - min) * 1.0f) - 1.0f;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        // On/Off
        if (cmd.equals(Config.EQ_ENABLE)) {
            if (onoff.isSelected()) {
                config.setEqualizerOn(true);
            } else {
                config.setEqualizerOn(false);
            }
            synchronizeEqualizer();
        } // Auto
        else if (cmd.equals(Config.EQ_AUTO_ENABLE)) {
            if (auto.isSelected()) {
                config.setEqualizerAuto(true);
            } else {
                config.setEqualizerAuto(false);
            }
        } // Presets
        else if (cmd.equals(Config.EQ_PRESET)) {
            mainpopup.show(this, preset.getLocation().x, preset.getLocation().y);
        } else if (cmd.equals(Config.CLOSE)) {
            player.pressEq();
        } else if (cmd.equals("Normal")) {
            updateSliders(PRESET_NORMAL);
            synchronizeEqualizer();
        } else if (cmd.equals("Classical")) {
            updateSliders(PRESET_CLASSICAL);
            synchronizeEqualizer();
        } else if (cmd.equals("Club")) {
            updateSliders(PRESET_CLUB);
            synchronizeEqualizer();
        } else if (cmd.equals("Dance")) {
            updateSliders(PRESET_DANCE);
            synchronizeEqualizer();
        } else if (cmd.equals("Full Bass")) {
            updateSliders(PRESET_FULLBASS);
            synchronizeEqualizer();
        } else if (cmd.equals("Full Bass & Treble")) {
            updateSliders(PRESET_FULLBASSTREBLE);
            synchronizeEqualizer();
        } else if (cmd.equals("Full Treble")) {
            updateSliders(PRESET_FULLTREBLE);
            synchronizeEqualizer();
        } else if (cmd.equals("Laptop")) {
            updateSliders(PRESET_LAPTOP);
            synchronizeEqualizer();
        } else if (cmd.equals("Live")) {
            updateSliders(PRESET_LIVE);
            synchronizeEqualizer();
        } else if (cmd.equals("Party")) {
            updateSliders(PRESET_PARTY);
            synchronizeEqualizer();
        } else if (cmd.equals("Pop")) {
            updateSliders(PRESET_POP);
            synchronizeEqualizer();
        } else if (cmd.equals("Reggae")) {
            updateSliders(PRESET_REGGAE);
            synchronizeEqualizer();
        } else if (cmd.equals("Rock")) {
            updateSliders(PRESET_ROCK);
            synchronizeEqualizer();
        } else if (cmd.equals("Techno")) {
            updateSliders(PRESET_TECHNO);
            synchronizeEqualizer();
        }
    }

    /**
     * Update sliders from gains array.
     *
     * @param gains
     */
    public void updateSliders(int[] gains) {
        if (gains != null) {
            for (int i = 0; i < gains.length; i++) {
                gainValue[i + 1] = gains[i];
                sliders[i + 1].setValue(maxGain - gainValue[i + 1]);
            }
        }
    }

    /**
     * Apply equalizer values.
     */
    public void synchronizeEqualizer() {
        config.setLastEqualizer(gainValue);
        if (config.isEqualizerOn()) {
            for (int j = 0; j < eqgains.length; j++) {
                eqgains[j] = -gainValue[j + 1] + maxGain;
            }
            updateBands(eqgains, minGain, maxGain);
        } else {
            for (int j = 0; j < eqgains.length; j++) {
                eqgains[j] = (maxGain - minGain) / 2;
            }
            updateBands(eqgains, minGain, maxGain);
        }
    }

    /**
     * Return equalizer bands distribution.
     *
     * @return
     */
    public int getEqdist() {
        return eqdist;
    }

    /**
     * Set equalizer bands distribution.
     *
     * @param i
     */
    public void setEqdist(int i) {
        eqdist = i;
    }

    /**
     * Simulates "On/Off" selection.
     */
    public void pressOnOff() {
        onoff.doClick();
    }

    /**
     * Simulates "Auto" selection.
     */
    public void pressAuto() {
        auto.doClick();
    }
}
