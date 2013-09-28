package com.ap.logic.QuizClasses;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author andrew
 */
public class QuizResult {
    private Quiz quiz;
    private int totalQuestions=0;
    private int correctAnswers=0;
    private int correctAnswersPercent=0;
    private LinkedHashMap<String,Question> correctQuestions=new LinkedHashMap<String, Question>();
    private LinkedHashMap<String,Question> wrongQuestions=new LinkedHashMap<String, Question>();



    ///log
    public void checkQuiz(){
            LinkedHashMap<String,Question> questions=quiz.getQuestions();
            LinkedHashMap<String,String> answers = quiz.getAnswers();        // user answers

            Set<String> questionsIds=questions.keySet();

            for(String item : questionsIds){
                    if(questions.get(item).getType().equals("0") && answers.containsKey(item)  ){
                            if(questions.get(item).getQuestionAnswer().startsWith(answers.get(item)) ){
                                this.correctAnswers++;
                                this.correctQuestions.put(questions.get(item).getId(), questions.get(item));
                            }else{
                                this.wrongQuestions.put(questions.get(item).getId(), questions.get(item));
                            }
                    }

                    if(questions.get(item).getType().equals("1") && answers.containsKey(item)  ){
                            if(questions.get(item).getQuestionAnswer().startsWith(answers.get(item)) ){
                                 this.correctAnswers++;
                                this.correctQuestions.put(questions.get(item).getId(), questions.get(item));
                            }else{
                                this.wrongQuestions.put(questions.get(item).getId(), questions.get(item));
                            }
                    }
                    
            }

            

        
    }

    ///





    /**
     * @return the quiz
     */
    public Quiz getQuiz() {
        return quiz;
    }

    /**
     * @param quiz the quiz to set
     */
    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        this.totalQuestions=quiz.getQuestions().size();
    }

    /**
     * @return the totalQuestions
     */
    public int getTotalQuestions() {
        return totalQuestions;
    }

    /**
     * @param totalQuestions the totalQuestions to set
     */
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    /**
     * @return the correctAnswers
     */
    public int getCorrectAnswers() {
        return correctAnswers;
    }

    /**
     * @param correctAnswers the correctAnswers to set
     */
    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    /**
     * @return the correctAnswersPercent
     */
    public int getCorrectAnswersPercent() {
        return correctAnswersPercent;
    }

    /**
     * @param correctAnswersPercent the correctAnswersPercent to set
     */
    public void setCorrectAnswersPercent(int correctAnswersPercent) {
        this.correctAnswersPercent = correctAnswersPercent;
    }

    /**
     * @return the correctQuestions
     */
    public LinkedHashMap<String, Question> getCorrectQuestions() {
        return correctQuestions;
    }

    /**
     * @param correctQuestions the correctQuestions to set
     */
    public void setCorrectQuestions(LinkedHashMap<String, Question> correctQuestions) {
        this.correctQuestions = correctQuestions;
    }

    /**
     * @return the wrongQuestions
     */
    public LinkedHashMap<String, Question> getWrongQuestions() {
        return wrongQuestions;
    }

    /**
     * @param wrongQuestions the wrongQuestions to set
     */
    public void setWrongQuestions(LinkedHashMap<String, Question> wrongQuestions) {
        this.wrongQuestions = wrongQuestions;
    }

}
