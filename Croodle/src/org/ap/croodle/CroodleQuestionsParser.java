package org.ap.croodle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

public class CroodleQuestionsParser {
    private File questionsFile;
    private String questionsFileContent;
    
    public int[] tryToAnswer (String question, ArrayList<String> variants){
        System.err.println("FIle content");
        System.out.println(this.questionsFileContent);
        return new int[2];
    }
    
    public void setQuestionFile(File questionsFile){
        this.questionsFile = questionsFile;
        try {
            this.questionsFileContent = IOUtils.toString( new FileInputStream(questionsFile));
        } catch (IOException ex) {
            Logger.getLogger(CroodleQuestionsParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
