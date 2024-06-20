package com.ap;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CtrlFDialog {
    private JDialog dialog;
    private JTextField searchField;
    private JButton nextButton;
    private JButton prevButton;
    private JButton countButton;
    private JTextPane editor;
    private JLabel countLabel;

    public CtrlFDialog(JTextPane editor) {
        this.editor = editor;
        createDialog();
    }

    private void createDialog() {
        dialog = new JDialog();
        dialog.setTitle("Find");
        dialog.setSize(300, 120);
        dialog.setLayout(new FlowLayout());

        searchField = new JTextField(20);
        nextButton = new JButton("Next");
        prevButton = new JButton("Previous");
        countButton = new JButton("Count");

        nextButton.addActionListener(e -> findNext());
        prevButton.addActionListener(e -> findPrev());
        countButton.addActionListener(e -> countMatches());

        dialog.add(new JLabel("Find:"));
        dialog.add(searchField);
        dialog.add(nextButton);
        dialog.add(prevButton);
        dialog.add(countButton);
        countLabel = new JLabel();
        dialog.add(countLabel);

        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
        dialog.getRootPane().getActionMap().put("Cancel", new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                int caretPosition = editor.getCaretPosition();
                editor.requestFocusInWindow();
                editor.setCaretPosition(caretPosition);
            }
        });
    }

    public void show() {
        dialog.setVisible(true);
        editor.requestFocusInWindow();
    }


    private void findNext() {
        String find = searchField.getText();
        String text = editor.getText();
        int start = editor.getCaretPosition();
        int end = text.length();
        for (int i = start; i <= end - find.length(); i++) {
            String substring = text.substring(i, i + find.length());
            if (find.equals(substring)) {
                editor.select(i, i + find.length());
                return;
            }
        }
    }

    private void findPrev() {
        String find = searchField.getText();
        String text = editor.getText();
        int start = editor.getCaretPosition();
        Highlighter highlighter = editor.getHighlighter();
        highlighter.removeAllHighlights(); // Remove any existing highlights

        for (int i = start - find.length(); i >= 0; i--) {
            String substring = text.substring(i, i + find.length());
            if (find.equals(substring)) {
                try {
                    highlighter.addHighlight(i, i + find.length(), DefaultHighlighter.DefaultPainter);
                    editor.setCaretPosition(i);
                    editor.requestFocusInWindow();
                    return;
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void countMatches() {
        String find = searchField.getText();
        int start = 0;
        int end = editor.getDocument().getLength();
        int count = 0;
        try {
            while (start <= end) {
                if (editor.getText(start, find.length()).equals(find)) {
                    count++;
                    start += find.length(); // Move start to the position after the found match
                } else {
                    start++;
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        System.out.println("Ctrl+f count = " + count);
        countLabel.setText("Matches: " + count);
    }
}
