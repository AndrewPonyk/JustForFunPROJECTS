package com.ap.configuration;
/**
 *
 * @author andrew
 */
public class Config {
        //Windows
//        public static String questionerPath="D:\\questioner\\";
//        public static  String classificationXMLPath=questionerPath+"data\\classification.xml";
//        public static  String questionsPath=questionerPath+"data\\questions\\";
        
        
        //Linux
      //  public static String questionerPath="/home/andrew/GitRepositories/JustForFunPROJECTS/questioner/";
        //public static  String classificationXMLPath=questionerPath+"data/classification.xml";
        //public static  String questionsPath=questionerPath+"data/questions/";       

        //Linux , nout on GlobalLogic
        private static String questionerPath="";//="/home/andrew/git/JustForFunPROJECTS/questioner/";
        public static  String classificationXMLPath ="";//questionerPath+"data/classification.xml";
        public static  String questionsPath="";//questionerPath+"data/questions/";    
       
       public static void setQuestionerPath(String val){
             questionerPath=val;
             classificationXMLPath=questionerPath+"data/classification.xml";
             questionsPath=questionerPath+"data/questions/";    
       }
       
        public static String getQuestionerPath(){
            return Config.questionerPath;
        }

}