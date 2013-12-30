/**
 *
 * @author andrew
 */
package org.ap.croodle;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class CroodleWindow {
  public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception evt) {}
  
    JLabel l;
    JTextField t;
    JButton b;
    JFrame f = new JFrame("Croodle");
    Container cp = f.getContentPane();
    cp.setLayout(new GridBagLayout());
    cp.setBackground(UIManager.getColor("control"));
    GridBagConstraints c = new GridBagConstraints();

    c.gridx = 0;
    c.gridy = GridBagConstraints.RELATIVE;
    c.gridwidth = 1;
    c.gridheight = 1;
    c.insets = new Insets(2, 2, 2, 2);
    c.anchor = GridBagConstraints.EAST;


    cp.add(l = new JLabel("Moodle URL:", SwingConstants.RIGHT), c);
    l.setDisplayedMnemonic('n');
    cp.add(l = new JLabel("Login:", SwingConstants.RIGHT), c);
    l.setDisplayedMnemonic('h');
    cp.add(l = new JLabel("Password:", SwingConstants.RIGHT), c);
    l.setDisplayedMnemonic('c');
    cp.add(l = new JLabel("TestURL:", SwingConstants.RIGHT), c);
    l.setDisplayedMnemonic('s');
    cp.add(l = new JLabel("Correct Ans Percent:", SwingConstants.RIGHT), c);
    l.setDisplayedMnemonic('z');
    cp.add(loadQuestions, c);
    l.setDisplayedMnemonic('z');
    cp.add(clear, c);
    clear.setMnemonic('l');

    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.CENTER;
    
 // loginUrlTextField, usernameTextField, passwordField ,quizUrlTextField, correctQuestionsPercentTextField 
    cp.add(loginUrlTextField, c);
    loginUrlTextField.setFocusAccelerator('n');
    c.gridx = 1;
    c.gridy = GridBagConstraints.RELATIVE;
    cp.add(usernameTextField, c);
    usernameTextField.setFocusAccelerator('h');
    cp.add(passwordField, c);
    passwordField.setFocusAccelerator('c');
    cp.add(quizUrlTextField, c);
    quizUrlTextField.setFocusAccelerator('s');
    cp.add(correctQuestionsPercentTextField, c);
    correctQuestionsPercentTextField.setFocusAccelerator('z');
    cp.add(questionsFileTextField, c);
    correctQuestionsPercentTextField.setFocusAccelerator('z');
    

    c.weightx = 0.0;
    c.fill = GridBagConstraints.NONE;
    cp.add(go, c);
    go.setMnemonic('o');

    
    loadQuestions.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {

            final JFileChooser fc = new JFileChooser();
            int resultVal = fc.showOpenDialog(null);
            if(resultVal == JFileChooser.APPROVE_OPTION){
                questionsFileTextField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        }
    });
    
    go.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            CroodleBrowser browser = new CroodleBrowser();
            try{
                
                browser.setLoginUrl(loginUrlTextField.getText());
                browser.setUsername(usernameTextField.getText());
                browser.setPassword(passwordField.getText());
                browser.setQuizUrl(quizUrlTextField.getText());
                browser.setCorrectQuestionsPercent(Double.parseDouble(correctQuestionsPercentTextField.getText()) );
                browser.setQuestionsFile(questionsFileTextField.getText());
            }
            catch(NumberFormatException ex){
                browser.setCorrectQuestionsPercent(100D);
            }
            browser.goAndPassTest();
        }
    });
    
    clear.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          for(int i=0; i < textFields.length; i++){
              textFields[i].setText("");
          }
        }
    });
    
    f.pack();
    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        System.exit(0);
      }
    });
    f.setVisible(true);
    f.setResizable(false);
    
  }

  public static JTextField loginUrlTextField = new JTextField("http://www.lifeintheuk.org/",35);
  public static JTextField usernameTextField = new JTextField("andrew9999",35);
  public static JTextField passwordField = new JTextField("Aa123456",35);
  public static JTextField quizUrlTextField = new JTextField("http://www.lifeintheuk.org/mod/quiz/view.php?id=3", 35);
  public static JTextField correctQuestionsPercentTextField = new JTextField("100",35);
  public static JButton go = new JButton("Go");
  public static JButton clear = new JButton("Clear");
  public static JButton loadQuestions = new JButton("Load Questions");
  public static JTextField questionsFileTextField = new JTextField(35);
  
  
  public static JTextField [] textFields = new JTextField[]{loginUrlTextField,usernameTextField,passwordField,quizUrlTextField,correctQuestionsPercentTextField};
}