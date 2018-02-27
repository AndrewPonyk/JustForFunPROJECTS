package com.ap.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MonitorApp {
    private JButton button1;
    private JPanel panel1;

    public MonitorApp() {
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("clicked");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MonitorApp");
        frame.setContentPane(new MonitorApp().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
