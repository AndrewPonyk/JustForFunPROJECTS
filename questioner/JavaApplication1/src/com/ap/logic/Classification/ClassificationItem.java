
package com.ap.logic.Classification;

import java.util.LinkedHashMap;

/**
 *
 * @author andrew
 */
public class ClassificationItem {
        protected String name;
        protected   String id;
        protected int nOfQuestions;
        protected int nOfSubcategories;
        private LinkedHashMap<String, Category> categories=new LinkedHashMap<String, Category>();
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
     * @return the nOfQuestions
     */
    public int getnOfQuestions() {
        return nOfQuestions;
    }

    /**
     * @param nOfQuestions the nOfQuestions to set
     */
    public void setnOfQuestions(int nOfQuestions) {
        this.nOfQuestions = nOfQuestions;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the nOfSubcategories
     */
    public int getnOfSubcategories() {
        return nOfSubcategories;
    }

    /**
     * @param nOfSubcategories the nOfSubcategories to set
     */
    public void setnOfSubcategories(int nOfSubcategories) {
        this.nOfSubcategories = nOfSubcategories;
    }

    /**
     * @return the categories
     */
    public LinkedHashMap<String, Category> getCategories() {
        return categories;
    }

    /**
     * @param categories the categories to set
     */
    public void setCategories(LinkedHashMap<String, Category> categories) {
        this.categories = categories;
    }
}
