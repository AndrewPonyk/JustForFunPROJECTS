package com.ap.logic.Classification;

/**
 *
 * @author olia
 */
public class Class  extends  ClassificationItem{

    public Class() {
    }

    @Override
    public String toString() {
        return this.name+"("+this.id+")"+"[QUESTIONS:"+this.nOfQuestions+"]";
    }
}
