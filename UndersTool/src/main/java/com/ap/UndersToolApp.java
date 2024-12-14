package com.ap;

import com.ap.highlight.JavaHighlighter;
import lombok.Data;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


@Data
public class UndersToolApp extends JFrame {

    public static void main(String[] args) {
        System.out.println("Running UndersToolApp");
        SwingUtilities.invokeLater(() -> {
            try {
                new UndersToolApp().setVisible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public UndersToolApp() throws RuntimeException, Exception {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        createLayout(this);
        addMainMenuActions();
        createToolbarFunctionality();
        loadCache();
        createFileSearcher();
        new AutoSave(editor, currentFilePath, updatesLabel).startAutoSave();
    }

    private void createFileSearcher() {
        fileSearcher = new FileSearcher(editor, projectRoot, indexedFiles, currentFilePath);

        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        keyboardFocusManager.addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_N) {
                        fileSearcher.openSearchDialog();

                        return true; // This means the event is handled and should not be passed to other handlers
                    }
                }
                return false; // This means the event is not handled and should be passed to other handlers
            }
        });

        setFocusable(true);
    }

    private void loadCache() {
        Map<String, String> cache = JsonUtils.loadJson(tempFile);
        if (cache != null && cache.get("recentProjectRoot") != null) {
            projectRoot = cache.get("recentProjectRoot");
            loadFilesToFileExplorer(new File(projectRoot));

        }
    }

    private void createLayout(UndersToolApp undersToolApp) {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(createMainMenuBar());
        // Create toolbar
        undersToolApp.toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(getWidth(), 40));
        add(toolBar, BorderLayout.NORTH);

        // Create main split pane
        mainSplitPane = new JSplitPane();
        mainSplitPane.setOneTouchExpandable(true);
        add(mainSplitPane, BorderLayout.CENTER);

        // Create left scroll pane
        leftScrollPane = new JScrollPane();
        mainSplitPane.setLeftComponent(leftScrollPane);

        // Create right split pane
        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setRightComponent(rightSplitPane);

        // Create top split pane
        topSplitPane = new JSplitPane();
        rightSplitPane.setTopComponent(topSplitPane);

        // Create top left and top right panels
        JPanel topLeftPanel = new JPanel();
        topLeftPanel.setBackground(Color.RED);
        JPanel topRightPanel = new JPanel();
        topRightPanel.setBackground(Color.GREEN);
        topSplitPane.setLeftComponent(topLeftPanel);
        topSplitPane.setRightComponent(topRightPanel);

        // Create bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.BLUE);

        //add text area to bottom panel
        JTextArea lineNumberBar = new JTextArea("1");
        lineNumberBar.setBackground(Color.LIGHT_GRAY);
        lineNumberBar.setEditable(false);
        lineNumberBar.setFocusable(false);
        lineNumberBar.setFont(editor.getFont());
        editor.getDocument().addDocumentListener(new DocumentListener() {
            public String getText() {
                int caretPosition = editor.getDocument().getLength();
                Element root = editor.getDocument().getDefaultRootElement();
                String text = "1" + System.getProperty("line.separator");
                for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                    text += i + System.getProperty("line.separator");
                }
                return text;
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        lineNumberBar.setText(getText());
                        return null;
                    }
                };
                worker.execute();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        lineNumberBar.setText(getText());
                        return null;
                    }
                };
                worker.execute();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        lineNumberBar.setText(getText());
                        return null;
                    }
                };
                worker.execute();
            }
        });
        bottomPanel.add(editor, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.setRowHeaderView(lineNumberBar);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        Font currentFont = editor.getFont();
        Font editorFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() + 2);
        editor.setFont(editorFont); lineNumberBar.setFont(editorFont);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        ctrlFDialog = new CtrlFDialog(editor);
        editor.getInputMap().put(KeyStroke.getKeyStroke("control F"), "find");
        editor.getActionMap().put("find", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ctrlFDialog.show();
            }
        });

        // Add a JLabel at the bottom for updates and messages
        updatesLabel = new JLabel();
        updatesLabel.setPreferredSize(new Dimension(getWidth(), 15)); // 10px height
        updatesLabel.setBackground(Color.GRAY); // Grey background
        updatesLabel.setOpaque(true); // Make sure the background color is visible
        bottomPanel.add(updatesLabel, BorderLayout.SOUTH); // Add to the bottom of the panel
        updatesLabel.setText("Your update message goes here");

        rightSplitPane.setBottomComponent(bottomPanel);


        // Add component listener to set divider locations after frame is visible
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                mainSplitPane.setDividerLocation(getWidth() / 5);
                rightSplitPane.setDividerLocation(getHeight() / 10);
                topSplitPane.setDividerLocation(getWidth() * 30 / 100);

                mainSplitPane.setDividerSize(2);
                rightSplitPane.setDividerSize(2);
                topSplitPane.setDividerSize(2);
            }
        });

//        for (int i = 0; i < 20; i++) {
//            String word = generateRandomWord(); // This method should return a random short word
//            JButton button = new JButton(word);
//            topLeftPanel.add(button);
//            infoPanelButtons.put(word, button);
//        }
        JButton button = new JButton("Show files types and count");
        button.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
        topLeftPanel.add(button);
        infoPanelButtons.put("Show files types and count", button);


        JButton button2 = new JButton("Describe file");
        button2.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
        topLeftPanel.add(button2);
        infoPanelButtons.put("Describe file", button2);

        JButton button3 = new JButton("Show files types and count");
        button3.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
        topLeftPanel.add(button3);
        infoPanelButtons.put("Show files types and count", button3);

        JButton button4 = new JButton("Recteate project from scratch");
        button4.setFont(new Font("Sans-Serif", Font.PLAIN, 10));
        topLeftPanel.add(button4);
        infoPanelButtons.put("Recteate project from scratch", button3);
    }

    private void createToolbarFunctionality() {
        String[] buttonNames = {
                "Run", "Debug", "Configure SDK", "Build", "Test", "Commit",
                "Push", "Pull", "Sync", "Branch", "Merge", "Rebase",
                "Stash", "Fetch", "Terminal", "TODO", "Find", "Replace",
                "Cut", "Copy", "Paste", "Undo", "Redo", "Save", "Help"
        };

        String[] buttonIcons = {
                "▶", "🐞", "⚙", "🏗", "🧪", "💾",
                "⬆", "⬇", "🔄", "🌳", "🔀", "↩",
                "📦", "📥", "🖥", "📝", "🔍", "🔄",
                "✂", "📋", "📄", "↩", "↪", "💾", "❓"
        };

        int count = 0;
        for (int i = 0; i < buttonNames.length; i++) {
            JButton jButton = new JButton(buttonNames[i]);
            jButton.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
            jButton.setText(buttonIcons[i] + " " + buttonNames[i]);
            toolBar.add(jButton);
            toolBarButtons.put(buttonNames[i], jButton);

            jButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Button clicked: " + jButton.getText());
            });

            count++;
            if (count % 5 == 0) { // Change this number to adjust the number of buttons per group
                toolBar.addSeparator();
            }
        }
    }

    private void addMainMenuActions() {
        menuItems.get(0).get(1).addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                projectRoot = selectedFile.getAbsolutePath();

                loadFilesToFileExplorer(selectedFile);
            }
        });
    }

    private void loadFilesToFileExplorer(File selectedFile) {
        // Create file tree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(selectedFile);
        createNodes(root, selectedFile);
        JTree tree = new JTree(new DefaultTreeModel(root));
        //set smaller font
        Font currentFont = tree.getFont(); Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), currentFont.getSize() );tree.setFont(newFont);

        leftScrollPane.setViewportView(tree);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (selectedNode == null) {
                        return;
                    }
                    File selectedFile =  ((Utils.FileNode)selectedNode).getFile();
                    if (selectedFile.isFile()) {

                        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                            @Override
                            protected Void doInBackground() throws Exception {
                                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                                currentFilePath.set(selectedFile.getAbsolutePath());
                                editor.setText(content);
                                JavaHighlighter highlighter = new JavaHighlighter();
                                //highlighter.highlight(editor);
                                return null;
                            }
                        };
                        worker.execute();
                    }
                }
            }
        });
    }

    public JMenuBar createMainMenuBar() {
        mainMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menuItems.add(new ArrayList<>());
        String[] fileItems = {"New", "Open", "Save", "Save As", "Close", "Exit"};
        for (String item : fileItems) {
            JMenuItem jMenuItem = new JMenuItem(item);
            menuItems.get(0).add(jMenuItem);
            fileMenu.add(jMenuItem);
        }
        mainMenuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        menuItems.add(new ArrayList<>());
        String[] editItems = {"Undo", "Redo", "Cut", "Copy", "Paste", "Find", "Replace"};
        for (String item : editItems) {
            JMenuItem jMenuItem = new JMenuItem(item);
            menuItems.get(1).add(jMenuItem);
            editMenu.add(jMenuItem);
        }
        mainMenuBar.add(editMenu);

        JMenu viewMenu = new JMenu("View");
        menuItems.add(new ArrayList<>());
        String[] viewItems = {"Zoom In", "Zoom Out", "Reset Zoom", "Toggle Fullscreen"};
        for (String item : viewItems) {
            JMenuItem jMenuItem = new JMenuItem(item);
            menuItems.get(2).add(jMenuItem);
            viewMenu.add(jMenuItem);
        }
        mainMenuBar.add(viewMenu);

        JMenu navigateMenu = new JMenu("Navigate");
        menuItems.add(new ArrayList<>());
        String[] navigateItems = {"Go To", "Back", "Forward", "Last Edit Location"};
        for (String item : navigateItems) {
            JMenuItem jMenuItem = new JMenuItem(item);
            menuItems.get(3).add(jMenuItem);
            navigateMenu.add(jMenuItem);
        }
        mainMenuBar.add(navigateMenu);

        JMenu codeMenu = new JMenu("Code");
        menuItems.add(new ArrayList<>());
        String[] codeItems = {"Reformat Code", "Optimize Imports", "Code Cleanup", "Inspect Code"};
        for (String item : codeItems) {
            JMenuItem jMenuItem = new JMenuItem(item);
            menuItems.get(4).add(jMenuItem);
            codeMenu.add(jMenuItem);
        }
        mainMenuBar.add(codeMenu);

        JMenu buildMenu = new JMenu("Build");
        menuItems.add(new ArrayList<>());
        String[] buildItems = {"Build Project", "Rebuild Project", "Clean Project"};
        for (String item : buildItems) {
            JMenuItem jMenuItem = new JMenuItem(item);
            menuItems.get(5).add(jMenuItem);
            buildMenu.add(jMenuItem);
        }
        mainMenuBar.add(buildMenu);

        JMenu runMenu = new JMenu("Run");
        menuItems.add(new ArrayList<>());
        String[] runItems = {"Run", "Debug", "Stop", "Run Configurations"};
        for (String item : runItems) {
            JMenuItem jMenuItem = new JMenuItem(item);
            menuItems.get(6).add(jMenuItem);
            runMenu.add(jMenuItem);
        }
        mainMenuBar.add(runMenu);

        JMenu helpMenu = new JMenu("Help");
        menuItems.add(new ArrayList<>());
        String[] helpItems = {"User Guide", "Check for Updates", "About"};
        for (String item : helpItems) {
            JMenuItem jMenuItem = new JMenuItem(item);
            menuItems.get(7).add(jMenuItem);
            helpMenu.add(jMenuItem);
        }
        mainMenuBar.add(helpMenu);
        return mainMenuBar;
    }


    private void createNodes(DefaultMutableTreeNode node, File file) {
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                Utils.FileNode childNode = new Utils.FileNode(childFile);
                node.add(childNode);
                createNodes(childNode, childFile);
            }
        } else {
            indexedFiles.add(file.getAbsolutePath());
        }
    }

    //////////////////////////////////////////////////Data
    private String projectRoot = "";
    private String tempFile = System.getProperty("user.home") + "\\Understool_temp.json";
    private Set<String> indexedFiles = new HashSet<String>();
    private AtomicReference<String> currentFilePath = new AtomicReference<>("");
    ////////////////////////////////////////////////// Swing components
    JSplitPane mainSplitPane;
    JSplitPane rightSplitPane;
    JSplitPane topSplitPane;

    private FileSearcher fileSearcher;
    private JScrollPane leftScrollPane;
    JMenuBar mainMenuBar;
    JMenu[] menus = new JMenu[10];
    ArrayList<ArrayList<JMenuItem>> menuItems = new ArrayList<>();
    JMenuItem openMenuItem;
    private Map<String, JButton> toolBarButtons = new HashMap<>();
    private  JToolBar toolBar;
    JTextPane editor = new JTextPane() {
        @Override
        public void setText(String t) {
            super.setText(t);
            JTextPane jTextPane = this;
            SwingUtilities.invokeLater(() -> {
                try {
                    JavaHighlighter highlighter = new JavaHighlighter();
                    highlighter.highlight(jTextPane);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    };
    private Map<String, JButton> infoPanelButtons = new HashMap<>();
    private JLabel updatesLabel;
    private CtrlFDialog ctrlFDialog;
}