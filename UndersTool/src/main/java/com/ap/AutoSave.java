package com.ap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class AutoSave {
    private final JLabel updatesLabel;
    private JTextPane editor;
    private AtomicReference<String> filePath;

    public AutoSave(JTextPane editor, AtomicReference<String> filePath, JLabel updatesLabel) {
        this.editor = editor;
        this.filePath = filePath;
        this.updatesLabel = updatesLabel;
    }

    public void startAutoSave() {
        int delay = 2500; //milliseconds
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if(filePath.get() != null && !filePath.get().isEmpty()) {
                    //saveFile();
                }
                updatesLabel.setText("Current file name is :" + filePath.get());
            }
        };
        new Timer(delay, taskPerformer).start();
    }

    private void saveFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.get()))) {
            writer.write(editor.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}