package com.itlgl.swing.gobang;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessBoard extends JFrame implements ActionListener {
    private CBoardPanel cbBoard = new CBoardPanel(CBoardPanel.HUMAN_FIRST, CBoardPanel.EASY, this);

    private JMenu menu1 = new JMenu("游戏");
    private JMenu menu2 = new JMenu("设置");
    private JMenu menu3 = new JMenu("帮助");
    private JMenu menu1_1 = new JMenu("新游戏");
    private JMenu menu2_1 = new JMenu("等级");
    private JMenuItem item1_1_1 = new JMenuItem("玩家先");
    private JMenuItem item1_1_2 = new JMenuItem("电脑先");
    private JMenuItem item1_2 = new JMenuItem("悔棋");
    private JMenuItem item1_3 = new JMenuItem("退出");
    private JMenuItem item1_4 = new JMenuItem("电脑荐棋");
    private JRadioButtonMenuItem item2_1_1 = new JRadioButtonMenuItem("低级");
    private JRadioButtonMenuItem item2_1_2 = new JRadioButtonMenuItem("高级");
    private JMenuItem item3_1 = new JMenuItem("关于...");
    private JMenuBar bar = new JMenuBar();
    private ButtonGroup group = new ButtonGroup();

    public ChessBoard() {
        super("五子棋");

        item1_1_1.addActionListener(this);
        item1_1_2.addActionListener(this);
        item1_2.addActionListener(this);
        item1_3.addActionListener(this);
        item1_4.addActionListener(this);
        item2_1_1.addActionListener(this);
        item2_1_1.addActionListener(this);
        item3_1.addActionListener(this);
        item2_1_1.setSelected(true);
        group.add(item2_1_1);
        group.add(item2_1_2);
        menu1.add(menu1_1);
        menu1.add(item1_2);
        menu1.add(item1_4);
        menu1.add(item1_3);
        menu2.add(menu2_1);
        menu3.add(item3_1);
        menu1_1.add(item1_1_1);
        menu1_1.add(item1_1_2);
        menu2_1.add(item2_1_1);
        menu2_1.add(item2_1_2);
        bar.add(menu1);
        bar.add(menu2);
        bar.add(menu3);
        this.setJMenuBar(bar);

        add(cbBoard); // 加入棋盘面板

        this.setBounds(0, 0, 538, 585);
        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == item1_1_1) {
            cbBoard.restart(CBoardPanel.HUMAN_FIRST, CBoardPanel.EASY, this);
        } else if (e.getSource() == item1_1_2) {
            cbBoard.restart(CBoardPanel.AI_FIRST, CBoardPanel.EASY, this);
        } else if (e.getSource() == item1_2) {
            cbBoard.back();
        } else if (e.getSource() == item1_4) {
            cbBoard.dianNaoJianQi();
        } else if (e.getSource() == item1_3) {
            System.exit(0);
        } else if (e.getSource() == item2_1_1) {
            cbBoard.setLevel(CBoardPanel.EASY);
        } else if (e.getSource() == item2_1_2) {
            cbBoard.setLevel(CBoardPanel.HARD);
        } else if (e.getSource() == item3_1) {
            JOptionPane.showMessageDialog(this,
                    "李冠良\nmail:itlgl@outlook.com",
                    "关于",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
