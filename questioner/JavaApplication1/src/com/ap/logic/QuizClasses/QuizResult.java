package com.ap.logic.QuizClasses;

import java.util.LinkedHashMap;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 *
 * @author andrew
 */
public class QuizResult {
    private Quiz quiz;
    private int totalQuestions=0;
    private int detailedAnswerQuestions=0;
    private int correctAnswers=0;
    private int correctAnswersPercent=0;
    private LinkedHashMap<String,Question> correctQuestions=new LinkedHashMap<String, Question>();
    private LinkedHashMap<String,Question> wrongQuestions=new LinkedHashMap<String, Question>();
    private LinkedHashMap<String,Question> detailedAnserQuestions=new LinkedHashMap<String, Question>();// these questions cant be cheched automatically
    private LinkedHashMap<String, Question> unasweredQuestions=new LinkedHashMap<String, Question>();

    ///log
    public void checkQuiz(){
        LinkedHashMap<String,Question> questions=quiz.getQuestions();
        LinkedHashMap<String,String> answers = quiz.getAnswers();        // user answers

        Set<String> questionsIds=questions.keySet();

        for(String item : questionsIds){
            String answer=questions.get(item).getQuestionAnswer();
            answer=answer.replaceAll("&lt;","").
                replaceAll("\n", "").
                replaceAll("&nbsp;", "").
                replaceAll("<br>", "");

            System.err.println(questions.get(item).getType());
            System.err.println("************************");
            if(questions.get(item).getType().equals("0") && answers.containsKey(item)  ){
                    if(answer.startsWith(answers.get(item)) ){
                        this.correctAnswers++;
                        this.correctQuestions.put(questions.get(item).getId(), questions.get(item));
                    }else{
                        this.wrongQuestions.put(questions.get(item).getId(), questions.get(item));
                    }
            }

            if(questions.get(item).getType().equals("1") && answers.containsKey(item)  ){
                    if(answer.startsWith(answers.get(item)) ){
                         this.correctAnswers++;
                        this.correctQuestions.put(questions.get(item).getId(), questions.get(item));
                    }else{
                        this.wrongQuestions.put(questions.get(item).getId(), questions.get(item));
                    }
            }

            if( (questions.get(item).getType().equals("0") || questions.get(item).getType().equals("1") )
                    && (!answers.containsKey(item) ) ){
                this.getUnasweredQuestions().put(questions.get(item).getId(), questions.get(item));

            }

            if(questions.get(item).getType().equals("2")   ){
                    this.detailedAnswerQuestions++;
                    this.detailedAnserQuestions.put(questions.get(item).getId(),questions.get(item));
            }
        }
    }

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

    /**
     * @return the detailedAnswerQuestions
     */
    public int getDetailedAnswerQuestions() {
        return detailedAnswerQuestions;
    }

    /**
     * @param detailedAnswerQuestions the detailedAnswerQuestions to set
     */
    public void setDetailedAnswerQuestions(int detailedAnswerQuestions) {
        this.detailedAnswerQuestions = detailedAnswerQuestions;
    }

    /**
     * @return the detailedAnserQuestions
     */
    public LinkedHashMap<String, Question> getDetailedAnserQuestions() {
        return detailedAnserQuestions;
    }

    /**
     * @param detailedAnserQuestions the detailedAnserQuestions to set
     */
    public void setDetailedAnserQuestions(LinkedHashMap<String, Question> detailedAnserQuestions) {
        this.detailedAnserQuestions = detailedAnserQuestions;
    }

    /**
     * @return the unasweredQuestions
     */
    public LinkedHashMap<String, Question> getUnasweredQuestions() {
        return unasweredQuestions;
    }

    /**
     * @param unasweredQuestions the unasweredQuestions to set
     */
    public void setUnasweredQuestions(LinkedHashMap<String, Question> unasweredQuestions) {
        this.unasweredQuestions = unasweredQuestions;
    }

}
