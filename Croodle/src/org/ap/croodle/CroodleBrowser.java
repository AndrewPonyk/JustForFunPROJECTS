package org.ap.croodle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CroodleBrowser {
    private String loginUrl;
    private String username;
    private String password;
    private String quizUrl;
    private Double correctQuestionsPercent;
    private FirefoxDriver firefox = new FirefoxDriver();
    private String questionsFile = "";
    
    private CroodleQuestionsParser parser = new CroodleQuestionsParser();
    
    private List<WebElement> checkboxes = null;
    private List<WebElement> radiobuttons = null;       
    private WebElement nextQuestionButton = null;
    private WebDriverWait wait = new WebDriverWait(firefox, 11);
    
    public void goAndPassTest(){
        loginAndGoToQuizURL();
        
        //now we will start quiz
        startQuiz();
        
        checkboxes = getElementByLocator(By.cssSelector("input[type=checkbox]"));
        radiobuttons = getElementByLocator(By.cssSelector("input[type=radio]"));        
        nextQuestionButton = null;
        
        // main part of appliction 
        while( checkboxes.size() >1 || radiobuttons.size()>1){
            processQuestion(); // !!! here we get question text , question variants  and try to get answer
            
            // go to next question
            nextQuestionButton.click();
            checkboxes = getElementByLocator(By.cssSelector("input[type=checkbox]"));
            radiobuttons = getElementByLocator(By.cssSelector("input[type=radio]"));        
        }
        
        System.err.println("==========================================="+ "finish questions");
        //try to finish attempt
        finishQuiz(true);
    }
    
    
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Integer processQuestion(){
        WebElement questionTextParagraph = getElementByLocator(
            By.cssSelector("div[class=qtext] p")).get(0);
            
            nextQuestionButton = firefox.findElementByCssSelector("input[type=submit]");
            
            // we will click on longest variant =)
            int longestVariantIndex = 0;
            int maxLength = 0;
            ArrayList<Integer> correctVariants = new ArrayList<Integer>();
            
            // get question text and variants
            List<WebElement> variantsElements = getElementByLocator(
                        By.cssSelector("div.r0 label, div.r1 label"));
            
            ArrayList<String> variants = new ArrayList<String>();
            for(WebElement element:variantsElements){
                variants.add(element.getText());
            }
            
            
            System.out.println(questionTextParagraph.getText() +"\n");
            for(int i=0; i< variantsElements.size(); i++){
                 System.out.println("   [length="+ variantsElements.get(i).getText().length()+"] " 
                        + variantsElements.get(i).getText());
                if(variantsElements.get(i).getText().length() > maxLength){
                    maxLength = variantsElements.get(i).getText().length();
                    longestVariantIndex = i;
                }
            }
            
            parser.tryToAnswer(questionTextParagraph.getText(), variants);
            
            if(checkboxes.size() >= 2){ 
                System.out.println("Szzzzze = " + checkboxes.size() );
                checkboxes.get(longestVariantIndex).click();    //
                System.err.println("========next question");
            }
            if(radiobuttons.size() > 1){
                radiobuttons.get(longestVariantIndex).click();
                
                System.err.println("========next question");
            }
        return 0;
    }
    
    public Integer loginAndGoToQuizURL(){
        
        firefox.get(this.loginUrl);
    
        WebElement username = firefox.findElementByCssSelector("input[name=username]");
        WebElement password = firefox.findElementByCssSelector("input[type=password]");
        
        username.sendKeys(this.username);
        password.sendKeys(this.getPassword());
        password.submit();
        firefox.get(this.quizUrl);
        
        return 1;
    }
    
    public Integer startQuiz(){
        WebElement startQuiz = firefox.findElementByCssSelector("input[type=submit]");
        startQuiz.click();
        
        try{
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[type=button]")));
            WebElement confirmStart = firefox.findElementsByCssSelector("button[type=button]").get(1);
            if(confirmStart != null){
                confirmStart.click();
            }
        }
        catch(Exception ex){
            System.err.println("no confirm");
        }
        
        return 1;
    }
    
    // boolean parameter : true - finish test , false finish but not click submit finish , so you can return to quiz
    public Integer finishQuiz(boolean clickSubmitFinish){
        getElementByLocator(By.cssSelector("input[type=submit]")).get(1).click();

        
        
        // there is one more confirmation
        if(!clickSubmitFinish){
            return 1; // dont submit 
        }
        try{
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[type=button]")));
            WebElement confirmEnd = firefox.findElementsByCssSelector("button[type=button]").get(1);
            if(confirmEnd != null){
                confirmEnd.click();
            }
        }
        catch(Exception ex){
            System.err.println("no end confirm");
        }
        
        
        return 1;
    }
    
    public List<WebElement>  getElementByLocator( final By locator ) {
        firefox.manage().timeouts().implicitlyWait( 15, TimeUnit.SECONDS );
        List<WebElement> we = null;
        int tries = 0;
        while ( tries < 4 ) { // try 4 times
                try {
          we = firefox.findElements(locator );
          break;
        } catch ( StaleElementReferenceException sere ) {  					
          
        } catch ( NoSuchElementException nse ) {  					
            System.err.println( "Ignoring NoSuchElementException");
            return new ArrayList<WebElement>();
        } finally { // use finally block to increment count regardless of error
          tries += 1;
        }
  }
  return we;
}
    
    
    /**
     * @param loginUrl the loginUrl to set
     */
    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param quizUrl the quizUrl to set
     */
    public void setQuizUrl(String quizUrl) {
        this.quizUrl = quizUrl;
    }

    /**
     * @param correctQuestionsPercent the correctQuestionsPercent to set
     */
    public void setCorrectQuestionsPercent(Double correctQuestionsPercent) {
        this.correctQuestionsPercent = correctQuestionsPercent;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param questionsFile the questionsFile to set
     */
    public void setQuestionsFile(String questionsFile) {
        this.questionsFile = questionsFile;
        this.parser.setQuestionFile(new File(questionsFile));
    }
}
