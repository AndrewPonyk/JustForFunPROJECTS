package com.ap;


import javax.swing.*;

public class NoterApp {
    private JPanel panelMain;
    private JButton searchNoteButton;
    private JTextField textField1;
    private JTextArea textArea1;

    public static void main(String[] args) {
        JFrame jrame = new JFrame("App");
        jrame.setContentPane(new NoterApp().panelMain);
        jrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jrame.pack();
        jrame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
