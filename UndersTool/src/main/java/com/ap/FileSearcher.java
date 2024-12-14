package com.ap;

import com.ap.highlight.JavaHighlighter;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.text.JTextComponent;

public class FileSearcher {
    private final AtomicReference<String> currentFilePath;
    private JTextPane editor;
    private Set<String> indexedFiles;

    private JDialog dialog; // Add this line

    private String previousInput = "";

    public FileSearcher(JTextPane editor, String projectRoot, Set<String> indexedFiles, AtomicReference<String> currentFilePath) {
        this.editor = editor;
        this.indexedFiles = indexedFiles;
        this.currentFilePath = currentFilePath;
    }

    public void openSearchDialog() {
        JComboBox<String> searchField = new JComboBox<>(indexedFiles.toArray(new String[0]));
        searchField.setEditable(true);
        AutoCompleteDecorator.decorate(searchField);
        searchField.setSelectedItem(null);

        JTextComponent textComponent = (JTextComponent) searchField.getEditor().getEditorComponent();
        Document document = textComponent.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("Insert update");
                String input = textComponent.getText();
                if (input.contains(":\\")) {
                    return;
                }
                if (!input.equals(searchField.getSelectedItem())) {
                    updateSearchResults(searchField, input);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//                System.out.println("Remove update");
//                String input = textComponent.getText();
//                if(input.contains(":\\")){
//                    return;
//                }
//                if (!input.equals(searchField.getSelectedItem())) {
//                    updateSearchResults(searchField, input);
//                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                System.out.println("Changed update");
                System.out.println("Insert update");
                String input = textComponent.getText();
                if (input.contains(":\\")) {
                    return;
                }
                if (!input.equals(searchField.getSelectedItem())) {
                    updateSearchResults(searchField, input);
                }
            }
        });


        searchField.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String selectedFilePath = (String) searchField.getSelectedItem();
                    if (selectedFilePath != null && !selectedFilePath.isEmpty()) {
                        loadFileToEditor(new File(selectedFilePath));
                    }
                    searchField.hidePopup();
                }
            }
        });

        JOptionPane optionPane = new JOptionPane(searchField, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        dialog = optionPane.createDialog("Enter search query");
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> {
                    searchField.requestFocusInWindow();
                });
            }
            @Override
            public void windowClosed(WindowEvent e) {
                editor.requestFocusInWindow();
            }
        });
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue != null && (int) selectedValue == JOptionPane.OK_OPTION) {
            String selectedFilePath = (String) searchField.getSelectedItem();
            if (selectedFilePath != null && !selectedFilePath.isEmpty()) {
                loadFileToEditor(new File(selectedFilePath));
            }
            dialog.dispose(); // Add this line
        }
    }


    private void loadFileToEditor(File file) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws IOException {
                String content = new String(Files.readAllBytes(file.toPath()));
                editor.setText(content);
                currentFilePath.set(file.getAbsolutePath());
                JavaHighlighter highlighter = new JavaHighlighter();
                //highlighter.highlight(editor); //todo hightligh
                return null;
            }
        };
        worker.execute();
    }

    private void updateSearchResults(JComboBox<String> searchField, String input) {
        if (input.length() > 2 && !input.equals(previousInput)) {
            // Store the current caret position and selection
            JTextComponent editorComponent = (JTextComponent) searchField.getEditor().getEditorComponent();
            Object selectedItem = searchField.getSelectedItem();
            String currentText = editorComponent.getText();
            previousInput = input; // Store the current input

            // Update the model
            SwingUtilities.invokeLater(() -> {
                // Set the caret position before updating the model
                SwingUtilities.invokeLater(() -> {
                    int caretPosition = Math.min(input.length(), editorComponent.getText().length());
                    editorComponent.setCaretPosition(caretPosition);
                });

                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>() {
                    @Override
                    public void setSelectedItem(Object anObject) {
                        if (anObject != null) {
                            super.setSelectedItem(anObject);
                        }
                    }
                };
                for (String filePath : indexedFiles) {
                    if (filePath.toLowerCase().contains(input.toLowerCase()) ||
                            filePath.toLowerCase().matches(".*" + (input.replaceAll("\\*", ".*") + ".*").toLowerCase())) {
                        model.addElement(filePath);
                    }
                }
                System.out.println("Input:" + input + " Set model with " + model.getSize() + " elements");
                // Show the dropdown
                if (!searchField.isPopupVisible()) {
                    searchField.showPopup();
                }
                searchField.setModel(model);

                // Restore the selection
                //searchField.setSelectedItem(selectedItem);
                editorComponent.setText(currentText);

                // Show the dropdown
                if (!searchField.isPopupVisible()) {
                    searchField.showPopup();
                }
            });
        }
    }

    public JDialog getDialog() {
        return dialog;
    }

    private static List<String> skipExtensions = List.of(".class"); //todo implement skip
}