/**
 *
 * @author olia
 */

package com.ap.logic.QuizClasses;

import com.ap.logic.Classification.Category;
import com.ap.logic.Classification.ClassificationItem;
import com.ap.configuration.Config;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Attribute;



public class Quiz {

    // default values for fields
    private int nofquestions=10;
    private int minutes=20;
    private Boolean includeDetailedAnswersQuestions =true;
   // private Boolean includeTips = false; // tip and detailed answer are almost the same

    // here will be categories with files , so if we add class we get all categories with file from class
    // if we add category without file , we will add all subcategories with files
    private LinkedHashMap<String,Category> categories=new LinkedHashMap<String, Category>();
    private LinkedHashMap<String,Integer>   questionsCountByCategory=new LinkedHashMap<String, Integer>();


    // ready questions for quiz
    private LinkedHashMap<String,Question> questions=new LinkedHashMap<String, Question>();
    private LinkedHashMap<String,String> answers=new LinkedHashMap<String, String>();

    private Integer currentQuestionIndex=0;

    private Random random=new Random(new Date().getTime());


    ///log

    public void generateQuestions(){

        int countByCategory=this.nofquestions/categories.size();
        int  ostatok=this.nofquestions - (categories.size()*countByCategory);

        Set<String> categoriesKeys=categories.keySet();

        for(String item:categoriesKeys  ){
            questionsCountByCategory.put(item,countByCategory);

            if(ostatok>0 && categories.get(item).getnOfQuestions()>countByCategory){
                questionsCountByCategory.put(item,countByCategory+1);
                ostatok--;
            }
        }

        for(String item:categoriesKeys  ){
            System.out.println( item+" "+ questionsCountByCategory.get(item) +" "+categories.get(item) );
        }
        
        fillQuestions();
    }

    public void  fillQuestions(){
        
        this.random=new Random();
        
        Set<String> categoriesKeys=this.categories.keySet();

        for(String item:categoriesKeys){

            Category category=categories.get(item);
            Set<Integer>  randoms=new LinkedHashSet<Integer>();
            Integer requiredCountQuestions=questionsCountByCategory.get(item);

            while( randoms.size()!=requiredCountQuestions ){
                Integer randomInt=random.nextInt(category.getnOfQuestions()); //
                
                if(!randoms.contains(randomInt))
                randoms.add(randomInt);
            }
            
             System.out.println("getting  "+questionsCountByCategory.get(item) +" from "+categories.get(item).getFileName());
                for(Integer i:randoms){
                    System.out.print(i+" ");
                }
            System.out.println("<- randoms----------------------");
            
            LinkedHashMap<String,Question> generatedItems= this.getCategoryRandomizedQuestionsFromXML(categories.get(item), randoms);
            JOptionPane.showMessageDialog(null, generatedItems.size());
            
            //if not enouth , we will try to regenerate , is is stupid , but ...
            if(generatedItems.size()<questionsCountByCategory.get(item))
            {
                randoms=new LinkedHashSet<Integer>();
                requiredCountQuestions=questionsCountByCategory.get(item);

            while( randoms.size()!=requiredCountQuestions ){
                Integer randomInt=random.nextInt(category.getnOfQuestions()); //
                
                if(!randoms.contains(randomInt))
                randoms.add(randomInt);
                }
            
                 System.out.println("getting  "+questionsCountByCategory.get(item) +" from "+categories.get(item).getFileName());
                for(Integer i:randoms){
                    System.out.print(i+" ");
                }
                System.out.println("<- randoms----------------------");
                
                generatedItems= this.getCategoryRandomizedQuestionsFromXML(categories.get(item), randoms);
                JOptionPane.showMessageDialog(null, generatedItems.size());
            }
            this.questions.putAll( generatedItems);
            

        }

        // shuffle questions
        final List<Question> vs = new ArrayList<Question>(this.questions.values());        
        Collections.shuffle(vs);
        this.questions.clear();

            final Iterator<Question> vIter = vs.iterator();
            while(vIter.hasNext()){
                Question next=vIter.next();
                this.questions.put(next.getId(), next);
            }

        //set current and next questions
        this.setCurrentQuestionIndex((Integer) 0);
    }

    public LinkedHashMap<String,Question>  getCategoryRandomizedQuestionsFromXML(Category category, Set<Integer> randoms) {
        System.out.println("getting questions from xml file");
        LinkedHashMap<String,Question> result=new LinkedHashMap<String, Question>();
        Question question=new Question();

        Integer counter=0;
        Boolean currentQuestion=false;
        
        InputStream in =null;
        
        try{
		XMLInputFactory inputFactory=XMLInputFactory.newInstance();
		in =new FileInputStream(Config.questionsPath+category.getFileName());
                
		XMLEventReader eventReader=inputFactory.createXMLEventReader(in);

                while (eventReader.hasNext()) {
			XMLEvent event=eventReader.nextEvent();

                        if(event.isStartElement()){
				StartElement startElement=event.asStartElement();

				if(  startElement.getName().getLocalPart().equals("question")  ){
                                        if( randoms.contains(counter)){
                                            
                                            System.err.println("type: "+startElement.getAttributeByName(new QName("type")).getValue());
                                            
                                            if(!startElement.getAttributeByName(new QName("type")).getValue().equals("2")||
                                                    this.includeDetailedAnswersQuestions)
                                            {
                                            question.setId(startElement.getAttributeByName(new QName("id")).getValue());
                                            question.setType(startElement.getAttributeByName(new QName("type")).getValue());
                                            currentQuestion=true;
                                            }
                                        }

                                        if(startElement.getAttributeByName(new QName("type")).getValue().equals("2") &&
                                        !this.includeDetailedAnswersQuestions){
                                        counter=counter;
                                        }else{
                                            counter++;
                                        }
				}

                                if(  startElement.getName().getLocalPart().equals("questiontext")  &&  currentQuestion ){
                                    String questionText="";
                                    event = eventReader.nextEvent();
                                    questionText+=event.asCharacters().getData();

                                    event = eventReader.nextEvent();
                                    while(!event.isEndElement()  ){
                                        questionText+=event.asCharacters().getData();
                                        event = eventReader.nextEvent();
                                    }
                                    question.setQuestionText(questionText);                                    
                                }

                                if(  startElement.getName().getLocalPart().equals("questionanswer")  &&  currentQuestion ){
                                        String questionAnswer="";
                                        event = eventReader.nextEvent();
                                        questionAnswer+=event.asCharacters().getData();

                                        event = eventReader.nextEvent();
                                        while(!event.isEndElement()  ){
                                            questionAnswer+=event.asCharacters().getData();
                                            event = eventReader.nextEvent();
                                        }
                                        question.setQuestionAnswer(questionAnswer);

                                        System.err.println(question.getId());
                                        System.err.println(question.getType());
                                        System.err.println(question.getQuestionText());
                                        System.out.println(question.getQuestionAnswer());
                                        System.err.println("--end question --");
                                        System.err.println();

                                        result.put(question.getId(), question);

                                        // default values for next questions
                                        question=new Question();
                                        currentQuestion=false;
                                }
			}
            }
                
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
           
        }
        catch(XMLStreamException e){
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(Quiz.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    ////////////////////////////////////////////////////////////

    /**
     * @return the nofquestions
     */
    public int getNofquestions() {
        return nofquestions;
    }

    /**
     * @param nofquestions the nofquestions to set
     */
    public void setNofquestions(int nofquestions) {
        this.nofquestions = nofquestions;
    }

    /**
     * @return the minutes
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * @param minutes the minutes to set
     */
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * @return the includeDetailedAnswersQuestions
     */
    public Boolean getIncludeDetailedAnswersQuestions() {
        return includeDetailedAnswersQuestions;
    }

    /**
     * @param includeDetailedAnswersQuestions the includeDetailedAnswersQuestions to set
     */
    public void setIncludeDetailedAnswersQuestions(Boolean includeDetailedAnswersQuestions) {
        this.includeDetailedAnswersQuestions = includeDetailedAnswersQuestions;
    }

    /**
     * @return the categories
     */
    public LinkedHashMap<String, Category> getCategories() {
        return categories;
    }

    /**
     * @return the questions
     */
    public LinkedHashMap<String, Question> getQuestions() {
        return questions;
    }

    /**
     * @param questions the questions to set
     */
    public void setQuestions(LinkedHashMap<String, Question> questions) {
        this.questions = questions;
    }

    /**
     * @return the answers
     */
    public LinkedHashMap<String, String> getAnswers() {
        return answers;
    }

    /**
     * @param answers the answers to set
     */
    public void setAnswers(LinkedHashMap<String, String> answers) {
        this.answers = answers;
    }

    /**
     * @return the currentQuestionIndex
     */
    public Integer getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    /**
     * @param currentQuestionIndex the currentQuestionIndex to set
     */
    public void setCurrentQuestionIndex(Integer currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }


    public Boolean hasPreviousQuestion(){
           return this.getCurrentQuestionIndex()>0;

    }

    public Boolean hasNextQuestion(){
        return (this.getCurrentQuestionIndex()+1)<this.questions.size();
    }

    public Question getNextQuestion(){

        if(this.hasNextQuestion()){
                this.setCurrentQuestionIndex((Integer) (this.getCurrentQuestionIndex() + 1));
                return this.getNthQuestion(this.getCurrentQuestionIndex());
                
        }
        return null;
    }

    public Question getPreviousQuestion(){
        

        if(this.hasPreviousQuestion()){
            this.setCurrentQuestionIndex((Integer) (this.getCurrentQuestionIndex() - 1));
            return this.getNthQuestion(this.getCurrentQuestionIndex());
        }

        
            return null;
    }

    

    public Question getCurrentQuestion(){
        return this.getNthQuestion(this.getCurrentQuestionIndex());
    }

    public Question getNthQuestion( int n){
     // n - can be 0,1,2,3...
        int counter=0;
        for(String item :this.questions.keySet()){
            if(counter==n)
                return this.questions.get(item);
                
                counter++;
        }
        return null;
    }



}
