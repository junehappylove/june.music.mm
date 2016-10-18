/*
 * FontChooser.java
 *
 * Created on 2007年8月30日, 上午10:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.judy.momoplayer.util;

/**
 *
 * @author hadeslee
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import javax.swing.event.*;

public class FontChooser extends JPanel implements ActionListener, ListSelectionListener {
     private static final long serialVersionUID=20071214L;
    private JDialog jd;//用于显示模态的窗体
    private JComboBox families;//用于选择字体的下拉框
    private JList style,  size;//用于选择字形和字号的列表
    private JTextField sizeJT;//用于显示选中的字形和字号
    private JButton ok,  cancel;//表示选中和取消的按钮
    private Font current;//表示当然选中的字体
    private GraphicsEnvironment ge;//表示当前的图形环境
    private JLabel demo;//表示预览的label
    private String fontName;
    private int fontStyle,fontSize ;
    private Hashtable<String, Integer> ht;//名字到大小的映射
    /** Creates a new instance of JFontChooser */
    private FontChooser() {

    }

    private void initOther(Font init) {
        if (init == null) {
            current = new Font(fontName, fontStyle, fontSize);
        } else {
            current = init;
        }
        ht = new Hashtable<String, Integer>();
        sizeJT = new JTextField();
        sizeJT.setEditable(false);
        sizeJT.setBounds(260, 40, 50, 20);
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] family = ge.getAvailableFontFamilyNames();
        families = new JComboBox(family);
        families.setEditable(false);
        families.setMaximumRowCount(5);
        demo = new JLabel(Config.getResource("FontChooser.demo"), JLabel.CENTER);
        demo.setFont(current);
        String[] styleString = {Config.getResource("FontChooser.normal"), Config.getResource("FontChooser.bold"), Config.getResource("FontChooser.italic"), Config.getResource("FontChooser.IandB")};
        String[] sizeString = {Config.getResource("FontChooser.chuhao"), Config.getResource("FontChooser.xiaochu"), Config.getResource("FontChooser.yihao"), Config.getResource("FontChooser.xiaoyi"), Config.getResource("FontChooser.erhao"), Config.getResource("FontChooser.xiaoer"),
            Config.getResource("FontChooser.sanhao"), Config.getResource("FontChooser.xiaosan"), Config.getResource("FontChooser.sihao"), Config.getResource("FontChooser.xiaosi"), Config.getResource("FontChooser.wuhao"), Config.getResource("FontChooser.xiaowu"), Config.getResource("FontChooser.liuhao"), Config.getResource("FontChooser.xiaoliu"), Config.getResource("FontChooser.qihao"),
            Config.getResource("FontChooser.bahao"), "5", "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24",
            "26", "28", "36", "48", "72"
        };
        int[] sizeValue = {42, 36, 26, 24, 22, 18, 16, 15, 14, 12, 11, 9, 7, 6, 5, 4, 5, 8, 9, 10, 11, 12, 14, 16, 18, 20,
            22, 24, 26, 28, 36, 48, 72
        };
        for (int i = 0; i < sizeString.length; i++) {
            ht.put(sizeString[i], sizeValue[i]);
        }
        fontName=current.getFamily();
        fontStyle=current.getStyle();
        fontSize=current.getSize();
        style = new JList(styleString);
        size = new JList(sizeString);
        style.setSelectedIndex(current.getStyle());
        int index=0;
        for(int i=0;i<sizeValue.length;i++){
            if(sizeValue[i]==current.getSize()){
                index=i;
                break;
            }
        }
        sizeJT.setText(""+current.getSize());
        size.setSelectedIndex(index);
        families.setSelectedItem(current.getFamily());
        style.setVisibleRowCount(4);
        size.setVisibleRowCount(4);
        ok = new JButton(Config.getResource("FontChooser.ok"));
        cancel = new JButton(Config.getResource("FontChooser.cancel"));
        ok.addActionListener(this);
        cancel.addActionListener(this);
        families.addActionListener(this);
        style.addListSelectionListener(this);
        size.addListSelectionListener(this);
    }

    private void initWindow(Frame par, String title, Font init) {
        this.setLayout(new BorderLayout());
        JLabel fontLabel = new JLabel(Config.getResource("FontChooser.fontStyle"));
        JLabel faceLabel = new JLabel(Config.getResource("fontf"));
        JLabel sizeLabel = new JLabel(Config.getResource("fontSize"));
        fontLabel.setForeground(Color.RED);
        faceLabel.setForeground(Color.RED);
        sizeLabel.setForeground(Color.RED);
        fontLabel.setBounds(20, 20, 100, 20);
        faceLabel.setBounds(180, 20, 80, 20);
        sizeLabel.setBounds(260, 20, 50, 20);
        families.setBounds(10, 40, 127, 21);
        JScrollPane faceScroll = new JScrollPane(style);
        JScrollPane sizeScroll = new JScrollPane(size);
        faceScroll.setBounds(180, 40, 65, 100);
        sizeScroll.setBounds(260, 60, 50, 80);
        JPanel up = new JPanel(null);
        JPanel center = new JPanel(new BorderLayout());
        JPanel bottom = new JPanel();
        up.setPreferredSize(new Dimension(345, 160));
        up.add(fontLabel);
        up.add(faceLabel);
        up.add(sizeLabel);
        up.add(families);
        up.add(faceScroll);
        up.add(sizeScroll);
        up.add(sizeJT);
        up.setBorder(BorderFactory.createTitledBorder(Config.getResource("FontChooser.selectArea")));
        center.add(demo, BorderLayout.CENTER);
        center.setBorder(BorderFactory.createTitledBorder(Config.getResource("FontChooser.preview")));
        bottom.add(ok);
        bottom.add(cancel);
        this.add(up, BorderLayout.NORTH);
        this.add(center, BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);
        jd = new JDialog(par, title, true);
        jd.getContentPane().add(this, BorderLayout.CENTER);
        jd.setSize(360, 360);
        jd.setResizable(false);
        jd.setLocationRelativeTo(par);
        jd.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we) {
                current = null;
                jd.dispose();
            }
        });
        jd.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == families) {
            fontName = (String) families.getSelectedItem();
            current = new Font(fontName, fontStyle, fontSize);
            demo.setFont(current);
            this.repaint();
        } else if (ae.getSource() == ok) {
            jd.dispose();
        } else if (ae.getSource() == cancel) {
            current = null;
            jd.dispose();
        }
    }

    public void valueChanged(ListSelectionEvent le) {
        if (le.getSource() == style) {
            String value = (String) style.getSelectedValue();
            if (value.equals(Config.getResource("FontChooser.normal"))) {
                fontStyle = Font.PLAIN;
            } else if (value.equals(Config.getResource("FontChooser.bold"))) {
                fontStyle = Font.BOLD;
            } else if (value.equals(Config.getResource("FontChooser.italic"))) {
                fontStyle = Font.ITALIC;
            } else if (value.equals(Config.getResource("FontChooser.IandB"))) {
                fontStyle = Font.ITALIC | Font.BOLD;
            }
            current = new Font(fontName, fontStyle, fontSize);
            demo.setFont(current);
            this.repaint();
        } else if (le.getSource() == size) {
            String sizeName = (String) size.getSelectedValue();
            sizeJT.setText(sizeName);
            fontSize = ht.get(sizeName);
            current = new Font(fontName, fontStyle, fontSize);
            demo.setFont(current);
            this.repaint();
        }
    }

    public static Font showDialog(Frame owner, String title, Font init) {
        FontChooser jf = new FontChooser();
        jf.initOther(init);
        jf.initWindow(owner, title, init);
        return jf.current;
    }

    public static void main(String[] args) {
        FontChooser.showDialog(new JFrame(), Config.getResource("FontChooser.pleaseSelectFont"), new Font(Config.getResource("FontChooser.defaultFont"), Font.BOLD, 22));
    }
}
