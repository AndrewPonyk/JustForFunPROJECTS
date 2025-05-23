package com.ap.logic.QuizClasses;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 *
 * @author olia
 */
public class Question {
    private String id; // resources are archives with source code or smth like this
    private String type;
    private String questionText;
    private String questionAnswer;
    private ArrayList<String> images = new ArrayList<String>();
    private ArrayList<String> resources = new ArrayList<String>();
    private TreeSet<String> tags; // will be in 1.0 version

    public Question() {
    }

    public String toString(){
        return questionText.replace("&nbsp;", " "); // dont replace <br> , or '<' , or '>'  
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the questionText
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * @param questionText the questionText to set
     */
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    /**
     * @return the questionAnswer
     */
    public String getQuestionAnswer() {
        return questionAnswer;
    }

    /**
     * @param questionAnswer the questionAnswer to set
     */
    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    /**
     * @return the images
     */
    public ArrayList<String> getImages() {
        return images;
    }

    /**
     * @param images the images to set
     */
    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    /**
     * @return the resources
     */
    public ArrayList<String> getResources() {
        return resources;
    }

    /**
     * @param resources the resources to set
     */
    public void setResources(ArrayList<String> resources) {
        this.resources = resources;
    }

    /**
     * @return the tags
     */
    public TreeSet<String> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(TreeSet<String> tags) {
        this.tags = tags;
    }
}