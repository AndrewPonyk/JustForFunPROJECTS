package com.ap.highlight;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class JavaHighlighter {

    public void highlight(JTextArea textArea) {
        StyledDocument doc = textArea.getStyledDocument();

        // Define a default style
        Style defaultStyle = doc.addStyle("Default Style", null);
        StyleConstants.setForeground(defaultStyle, Color.BLACK);

        // Define a keyword style
        Style keywordStyle = doc.addStyle("Keyword Style", null);
        StyleConstants.setForeground(keywordStyle, Color.BLUE);

        // Define a comment style
        Style commentStyle = doc.addStyle("Comment Style", null);
        StyleConstants.setForeground(commentStyle, Color.GRAY);

        // Split the text into words
        String[] words = textArea.getText().split("\\W");

        // Iterate over the words and apply the appropriate style
        for (String word : words) {
            if (isKeyword(word)) {
                applyStyle(doc, word, keywordStyle);
            } else if (isComment(word)) {
                applyStyle(doc, word, commentStyle);
            } else {
                applyStyle(doc, word, defaultStyle);
            }
        }
    }

    private boolean isKeyword(String word) {
        // This is a very basic check. You might want to check against a list of all Java keywords.
        return word.equals("public") || word.equals("class") || word.equals("void");
    }

    private boolean isComment(String word) {
        // This is a very basic check. You might want to improve this to handle different types of comments.
        return word.startsWith("//");
    }

    private void applyStyle(StyledDocument doc, String word, Style style) {
        try {
            String text = doc.getText(0, doc.getLength());
            int pos = 0;
            while ((pos = text.indexOf(word, pos)) >= 0) {
                doc.setCharacterAttributes(pos, word.length(), style, false);
                pos += word.length();
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}