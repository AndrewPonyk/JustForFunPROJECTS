package com.ap.logic.xml;

/**
 *
 * @author olia
 */
import com.ap.logic.Classification.Category;
import com.ap.logic.Classification.Classification;
import com.ap.logic.Classification.Class;
import com.ap.logic.QuizClasses.Question;
import com.ap.configuration.Config;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;
import org.jdom2.output.Format;

public class ReadWriteClassificationXML {

    public Classification getClassification(String path) {
        System.out.println("Getting classification ");

        String xmlFileName = "";
        if (path != null) {
            xmlFileName = path;
        } else {
            xmlFileName = ReadWriteClassificationXML.class.getResource("classification.xml").getPath();
        }

        Classification classification = new Classification();


        SAXBuilder builder = new SAXBuilder();
        try {
            File xmlFile = new File(xmlFileName);
            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            List<Element> list = rootNode.getChildren();

            for (int i = 0; i < list.size(); i++) {

                Element elem = list.get(i);
                com.ap.logic.Classification.Class curClass = new com.ap.logic.Classification.Class();

                curClass.setId(elem.getAttributeValue("id"));
                curClass.setName(elem.getAttributeValue("name"));
                curClass.setnOfQuestions(Integer.parseInt(elem.getAttributeValue("nofquestions")));
                curClass.setnOfSubcategories(Integer.parseInt(elem.getAttributeValue("nofsubcategories")));



                //Main part get categories tree , with recursion
                        /*            
                 */
                LinkedHashMap<String, Category> categories = new LinkedHashMap<String, Category>();
                List<Element> subcategories = elem.getChildren();
                for (Element item : subcategories) {
                    Category currCategory = new Category();
                    currCategory.setName(item.getAttributeValue("name"));
                    currCategory.setId(item.getAttributeValue("id"));
                    currCategory.setnOfQuestions(Integer.parseInt(item.getAttributeValue("nofquestions")));
                    currCategory.setnOfSubcategories(Integer.parseInt(item.getAttributeValue("nofsubcategories")));
                    currCategory.setFileName(item.getAttributeValue("filename"));
                    //and here a piece of recursion
                    fillCategory(item, currCategory);

                    //add category

                    categories.put(currCategory.getId(), currCategory);
                }
                curClass.setCategories(categories);
                classification.getClasses().put(curClass.getId(), curClass);

            }
        } catch (IOException e) {
        } catch (JDOMException e) {
            e.printStackTrace();

        }


        return classification;
    }

    
    
    public void removeCategoryFromXML(Category category, String xmlFilePath,String parentID){

            try {
            System.out.println("removing category FROM XML !! and remove files with questions ");
            SAXBuilder builder = new SAXBuilder();
            File xmlFile = new File(xmlFilePath);
            Document document = (Document) builder.build(xmlFile);
            }catch (JDOMException ex) {
            Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return ;
    }
    
    public void removeFilesWithQuestions(Category categoryToRemove){
        
        if(categoryToRemove.getFileName()!=null){
            System.out.println("removing " + categoryToRemove.getFileName());
            
        }
        if(categoryToRemove.getCategories().size()>0){
            for(Map.Entry<String,Category> item : categoryToRemove.getCategories().entrySet()){
                removeFilesWithQuestions(item.getValue());
            }
        }
        return;
    }
    
    public void addCategoryToXML(Category category, String xmlFilePath, String parentID) {

        Element categoryElement = new Element("category");
        categoryElement.setAttribute("id", category.getId());
        categoryElement.setAttribute("name", category.getName());
        categoryElement.setAttribute("nofquestions", "0");
        categoryElement.setAttribute("nofsubcategories", "0");
        if(category.getFileName()!=null){
            categoryElement.setAttribute("filename",category.getFileName());
        }

        try {
            System.out.println("adddding category to XML !!!!!!!!!!!! ");
            SAXBuilder builder = new SAXBuilder();
            File xmlFile = new File(xmlFilePath);
            Document document = (Document) builder.build(xmlFile);


            Element rootNode = document.getRootElement();

            //Build the xpath expression
            XPath xpathExpression = XPath.newInstance("//*[@id=$parentID]");

            xpathExpression.setVariable("parentID", parentID);

            ArrayList<Element> userIds = (ArrayList<Element>) xpathExpression.selectNodes(document);
            for (int i = 0; i < userIds.size(); i++) {
                System.out.println(userIds.get(i).getAttributeValue("name"));
                userIds.get(i).addContent(categoryElement);
            }
            Element parentCategory = userIds.get(0);


            if (!parentCategory.getName().equals("classification")) {
                parentCategory.setAttribute("nofsubcategories",
                        Integer.parseInt(parentCategory.getAttributeValue("nofsubcategories")) + 1 + "");
            }

            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new FileWriter(xmlFilePath));

        } catch (JDOMException ex) {
            Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addQuestionsFile(String questionsFileName,Category category){
        try {
            Element questions = new Element("questions");
            questions.setAttribute("id", category.getId());
            questions.setAttribute("name", category.getName());
            questions.setAttribute("nofquestions", category.getnOfQuestions() + "");
            questions.setAttribute("nofsubcategories", category.getnOfSubcategories() + "");
            questions.setAttribute("filename", category.getFileName());
            Document doc = new Document(questions);
 
            XMLOutputter xmlOutput = new XMLOutputter();
            // display nice
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(questionsFileName));
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addQuestionToXML(Question question,Category category){
        try {

            System.out.println("adddding question");
            SAXBuilder builder = new SAXBuilder();
//            File xmlFile = new File("D:\\questioner\\data\\questions\\"+category.getFileName());
            File xmlFile = new File(Config.questionsPath+category.getFileName());
            Document document = (Document) builder.build(xmlFile);


            Element rootNode = document.getRootElement();

            question.setId(category.getId()+":"+rootNode.getChildren().size()); // generate id , as  categoryid:indexofquestion

            Element questionElement=new Element("question");
            questionElement.setAttribute("id", question.getId());
            questionElement.setAttribute("type", question.getType()+"");
            questionElement.addContent(new Element("questiontext").setText(question.getQuestionText())   );
            questionElement.addContent(new Element("questionanswer").setText(question.getQuestionAnswer())   );

            rootNode.addContent(questionElement);
            rootNode.setAttribute("nofquestions",  Integer.parseInt( rootNode.getAttributeValue("nofquestions") )+1+"");


            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new FileWriter(Config.questionsPath+category.getFileName()));

        } catch (JDOMException ex) {
            Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void updateQuestionsCountInXML(Category category , int addedQuestionsCount){
               SAXBuilder builder = new SAXBuilder();


        try {
            File xmlFile = new File(   Config.classificationXMLPath );
            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            
             //Build the xpath expression
            XPath xpathExpression = XPath.newInstance("//*[@id=$categoryID]");

            xpathExpression.setVariable("categoryID", category.getId());

            ArrayList<Element> curCategoryNode= (ArrayList<Element>) xpathExpression.selectNodes(document);

            curCategoryNode.get(0).setAttribute("nofquestions",
                    Integer.parseInt( curCategoryNode.get(0).getAttributeValue("nofquestions") ) +addedQuestionsCount+"" );

            Element parentElement=curCategoryNode.get(0).getParentElement();

            while(parentElement!=null &&  !parentElement.getName().equals("classification")){
                    parentElement.setAttribute("nofquestions", 
                            Integer.parseInt( parentElement.getAttributeValue("nofquestions")  )+addedQuestionsCount+"" );
                            
                    parentElement=parentElement.getParentElement();
            }
            XMLOutputter xmlOutput = new XMLOutputter();
            // display nice
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document, new FileWriter(Config.classificationXMLPath));

        }
        catch(IOException ex){
              Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(JDOMException ex){
              Logger.getLogger(ReadWriteClassificationXML.class.getName()).log(Level.SEVERE, null, ex);
        }


        return ;
    }


    public static void main(String[] args) throws InterruptedException {

        System.out.println("Reading classification.xml");
        String xmlFileName = ReadWriteClassificationXML.class.getResource("classification.xml").getPath();

        System.out.println(xmlFileName);

        Classification classification = new Classification();

        SAXBuilder builder = new SAXBuilder();

        try {
            File xmlFile = new File(xmlFileName);
            Document document = (Document) builder.build(xmlFile);
            Element rootNode = document.getRootElement();
            List<Element> list = rootNode.getChildren();

            for (int i = 0; i < list.size(); i++) {

                Element elem = list.get(i);
                com.ap.logic.Classification.Class curClass = new com.ap.logic.Classification.Class();

                curClass.setId(elem.getAttributeValue("id"));
                curClass.setName(elem.getAttributeValue("name"));
                curClass.setnOfQuestions(Integer.parseInt(elem.getAttributeValue("nofquestions")));
                curClass.setnOfSubcategories(Integer.parseInt(elem.getAttributeValue("nofsubcategories")));



                //Main part get categories tree , with recursion
                        /*
                 */
                LinkedHashMap<String, Category> categories = new LinkedHashMap<String, Category>();
                List<Element> subcategories = elem.getChildren();
                for (Element item : subcategories) {
                    Category currCategory = new Category();
                    currCategory.setName(item.getAttributeValue("name"));
                    currCategory.setId(item.getAttributeValue("id"));
                    currCategory.setnOfQuestions(Integer.parseInt(item.getAttributeValue("nofquestions")));
                    currCategory.setnOfSubcategories(Integer.parseInt(item.getAttributeValue("nofsubcategories")));

                    //and here a piece of recursion
                    //currCategory.setCategories(null);

                    fillCategory(item, currCategory);

                    //add category

                    categories.put(currCategory.getId(), currCategory);
                }
                curClass.setCategories(categories);
                classification.getClasses().put(curClass.getId(), curClass);

//                        System.out.println("id "+elem.getAttributeValue("id") +" "+elem.getAttributeValue("name"));
                System.out.println("Class " + elem.getAttributeValue("name") + " : has  " + elem.getChildren().size() + " children");

            }
        } catch (IOException e) {
        } catch (JDOMException e) {
            e.printStackTrace();
        }
        Thread.sleep(73);
        System.out.println("=======================");
        Set<String> classesNames = classification.getClasses().keySet();
        for (String item : classesNames) {
            System.err.println(classification.getClasses().get(item).getName());
        }
    }

    public static void fillCategory(Element elem, Category category) {
        // here some recursion
        List<Element> children = elem.getChildren();

        if (children.size() == 0) {
            return; // no children
        }
        for (int i = 0; i < children.size(); i++) {
            Category child = new Category();
            child.setId(children.get(i).getAttributeValue("id"));
            child.setName(children.get(i).getAttributeValue("name"));
            child.setnOfQuestions(Integer.parseInt(children.get(i).getAttributeValue("nofquestions")));
            child.setnOfSubcategories(Integer.parseInt(children.get(i).getAttributeValue("nofsubcategories")));
            child.setFileName(children.get(i).getAttributeValue("filename"));

            category.getCategories().put(child.getId(), child);
            fillCategory(children.get(i), child);
        }

    }
}
