package com.ap;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.Random;

public class Utils {
    // Add this method to the UndersToolApp class
    public static String generateRandomWord() {
        // This is a simple example of generating a random word. You can modify this method to suit your needs.
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = random.nextInt(5) + 1; // Generate a random length between 1 and 5
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            char randomChar = alphabet.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }


    public static class FileNode extends DefaultMutableTreeNode {
        private File file;

        public FileNode(File file) {
            this.file = file;
        }

        @Override
        public String toString() {
            return file.getName();
        }

        public File getFile() {
            return file;
        }
    }
}
