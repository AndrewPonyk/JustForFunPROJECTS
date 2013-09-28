package com.ap.logic.Classification;

import java.util.LinkedHashMap;

/**
 *
 * @author olia
 */
public class Category  extends  ClassificationItem{
    
    
    private String fileName=null;

    //private String questionClass;
    
    
    public Category(){    
    }

    @Override
    public String toString() {

        if(this.fileName!=null)
            return this.getName()+"("+this.id+")"+"[questions :"+this.nOfQuestions+" ; file:"+this.fileName+"]";

            return this.getName()+"("+this.id+")"+"[questions :"+this.nOfQuestions+"]";
    }










    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
