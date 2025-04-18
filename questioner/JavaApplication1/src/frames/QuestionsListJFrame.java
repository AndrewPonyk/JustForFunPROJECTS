/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package frames;

import com.ap.logic.Classification.Category;
import com.ap.logic.Classification.ClassificationItem;
import com.ap.logic.QuizClasses.Question;
import com.ap.logic.QuizClasses.Quiz;
import com.ap.logic.xml.ReadWriteClassificationXML;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.*;
/**
 *
 * @author olia
 */
public class QuestionsListJFrame extends javax.swing.JFrame {

    /**
     * Creates new form QuestionsListJFrame
     */
    private ClassificationItem categoryOrClass;
    private String [] questionsModel;
    private Question [] questionsList;
    
    // before 1.0  make this methods static , because it is fucking bad =)
    private ReadWriteClassificationXML reader =new ReadWriteClassificationXML();
    private Quiz quiz = new Quiz();
    
    public QuestionerJFrame parentFrame ;
    public boolean  questionsListChanged = false;
    
    private KeyListener clipboardKeyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                  
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                        try {
                            Toolkit toolkit = Toolkit.getDefaultToolkit();
                            Clipboard clipboard = toolkit.getSystemClipboard();
                            String result = (String) clipboard.getData(DataFlavor.stringFlavor);
                            
                            result = result.replaceAll(" ", "\r\n");
                            result = result.replace((char)160, ' ');
                            
                            setStringToClipboard(result);
                        } catch (UnsupportedFlavorException ex) {
                            Logger.getLogger(QuestionsListJFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(QuestionsListJFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
            }
            
              // This method writes a string to the clipboard.
            public void setStringToClipboard(String stringContent) {
                StringSelection stringSelection = new StringSelection(stringContent);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            }
        };
    
    public QuestionsListJFrame(){
        initComponents();     
    }
    
    public void buildQuestionsModel(){
        
        quiz.getCategories().put(categoryOrClass.getId(),
                (Category)categoryOrClass);
        quiz.setMinutes(20);
        quiz.setNofquestions(0);
        quiz.setAllQuestions(true);
        quiz.setIncludeDetailedAnswersQuestions(true);
        quiz.generateQuestions(false);   
        
        this.questionsModel = new String[quiz.getQuestions().size()];
        this.questionsList = new Question[quiz.getQuestions().size()];
        int counter = 0;
        
        Set<String> questionsIds = quiz.getQuestions().keySet();
        
        // populate JList model
        for(String id : questionsIds){
            String questionText = quiz.getQuestions().get(id).toString();
            
            questionsModel[counter] = " - " + (counter+1) + "  " +
                questionText.substring(0, questionText.length() );
            
            questionsList[counter] = quiz.getQuestions().get(id);
            
            counter++;
        }

        this.questionsListJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = questionsModel;
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
    }

    public void showQuestion(int selectedIndex){
        JTextPane questionContent = new JTextPane();
        JScrollPane scroll = new JScrollPane();

        // !!! bug , if i copy answer text into some editor it has bad format !!!
        //questionAnswer = questionAnswer.replaceAll("&#160;", " ");
        //questionAnswer = questionAnswer.replaceAll("&nbsp;&nbsp;&nbsp;", "\t");
        //questionAnswer = questionAnswer.replaceAll("&nbsp;", " ");
        // RESOLVED IN "clipboardKeyListener"
        questionContent.addKeyListener(clipboardKeyListener);
        
        String questionText = this.questionsList[selectedIndex].getQuestionText();
        String questionAnswer = this.questionsList[selectedIndex].getQuestionAnswer();
        
        questionContent.setContentType("text/html");
        questionContent.setText( questionText +
                "<br>-----------------------------<br> Answer : <br> -----<br>"+
                questionAnswer);
        
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setViewportView(questionContent);
        scroll.setPreferredSize(new Dimension(1000,625));
       
        JOptionPane.showMessageDialog(this, scroll, "Question content", 0);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        questionsListPopupMenu = new javax.swing.JPopupMenu();
        showQuestionjMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        removeQuestionMenuItem1 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        questionsListJList = new javax.swing.JList();
        categoryQuestionsListTitle = new javax.swing.JLabel();
        categoryTitleLabel = new javax.swing.JLabel();
        findQuestionTextTextBox = new javax.swing.JTextField();
        findQuestionButton = new javax.swing.JButton();

        showQuestionjMenuItem.setText("Show Question");
        showQuestionjMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showQuestionjMenuItemActionPerformed(evt);
            }
        });
        questionsListPopupMenu.add(showQuestionjMenuItem);
        questionsListPopupMenu.add(jSeparator1);

        removeQuestionMenuItem1.setText("Remove Question");
        removeQuestionMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeQuestionMenuItem1ActionPerformed(evt);
            }
        });
        questionsListPopupMenu.add(removeQuestionMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("List of questions");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        questionsListJList.setBackground(new java.awt.Color(204, 255, 204));
        questionsListJList.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        questionsListJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        questionsListJList.setFixedCellHeight(22);
        questionsListJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                questionsListJListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(questionsListJList);

        categoryQuestionsListTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        categoryQuestionsListTitle.setText("Questions in category  :");

        categoryTitleLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        findQuestionTextTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                findQuestionTextTextBoxKeyPressed(evt);
            }
        });

        findQuestionButton.setText("Find");
        findQuestionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findQuestionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(categoryQuestionsListTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(categoryTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 158, Short.MAX_VALUE)
                .addComponent(findQuestionTextTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findQuestionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryQuestionsListTitle)
                    .addComponent(categoryTitleLabel)
                    .addComponent(findQuestionTextTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(findQuestionButton))
                .addGap(37, 37, 37)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void questionsListJListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_questionsListJListMouseClicked
        int selectedIndex = this.questionsListJList.getSelectedIndex();
            
        if(evt.getClickCount() == 2){
            showQuestion(selectedIndex);
        }
        
        if(SwingUtilities.isRightMouseButton(evt)){
            questionsListJList.setSelectedIndex(questionsListJList.locationToIndex(evt.getPoint()));
            questionsListPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        
    }//GEN-LAST:event_questionsListJListMouseClicked

    private void findQuestionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findQuestionButtonActionPerformed
        findQuestions();
    }//GEN-LAST:event_findQuestionButtonActionPerformed

    private void showQuestionjMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showQuestionjMenuItemActionPerformed
        int selectedIndex = this.questionsListJList.getSelectedIndex();
        showQuestion(selectedIndex);
    }//GEN-LAST:event_showQuestionjMenuItemActionPerformed

    private void removeQuestionMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeQuestionMenuItem1ActionPerformed
        int result = JOptionPane.showConfirmDialog(this, "Remove selected question ?", "Confirm removing", JOptionPane.YES_NO_OPTION);
        if(result != JOptionPane.YES_OPTION){
            return;
        }
        
        reader.removeQuestion((Category)this.getCategoryOrClass(),
                questionsList[questionsListJList.getSelectedIndex()]);
        
        this.quiz.getQuestions().remove(questionsList[questionsListJList.getSelectedIndex()].getId());
        this.quiz.getAnswers().remove(questionsList[questionsListJList.getSelectedIndex()].getId());
        this.questionsListChanged = true;
        this.buildQuestionsModel(); 
    }//GEN-LAST:event_removeQuestionMenuItem1ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // parent tree 'refresh' , we only need to 'refresh' it , if we change someting (remove , modify ...)
        if(this.questionsListChanged){
            parentFrame.createCategoriesTree();
        }
    }//GEN-LAST:event_formWindowClosed

    private void findQuestionTextTextBoxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_findQuestionTextTextBoxKeyPressed
        // TODO add your handling code here:

        if(evt.getKeyCode() == 10){
            findQuestions();
        }
    }//GEN-LAST:event_findQuestionTextTextBoxKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QuestionsListJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QuestionsListJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QuestionsListJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QuestionsListJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new QuestionsListJFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel categoryQuestionsListTitle;
    private javax.swing.JLabel categoryTitleLabel;
    private javax.swing.JButton findQuestionButton;
    private javax.swing.JTextField findQuestionTextTextBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JList questionsListJList;
    private javax.swing.JPopupMenu questionsListPopupMenu;
    private javax.swing.JMenuItem removeQuestionMenuItem1;
    private javax.swing.JMenuItem showQuestionjMenuItem;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the categoryOrClass
     */
    public ClassificationItem getCategoryOrClass() {
        return categoryOrClass;
    }

    /**
     * @param categoryOrClass the categoryOrClass to set
     */
    public void setCategoryOrClass(ClassificationItem categoryOrClass) {
        this.categoryOrClass = categoryOrClass;
        this.categoryTitleLabel.setText(categoryOrClass.toString());
        this.buildQuestionsModel(); ///
        
    }
    
    
    private void findQuestions(){
        ArrayList<Integer> findedQuestions = new ArrayList<Integer>();
        
        for(int i=0; i<this.questionsModel.length; i++){
            if(this.questionsModel[i].toLowerCase().contains(findQuestionTextTextBox.getText().toLowerCase())){
                findedQuestions.add(i);
            };
        }

        //mark finded questions 
        int [] selectedIndixes = new int[findedQuestions.size()];
        for(int i=0;i<selectedIndixes.length;i++){
            selectedIndixes[i] = findedQuestions.get(i);
        }
        this.questionsListJList.setSelectedIndices(selectedIndixes) ;
       
    }
}
