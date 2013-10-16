package com.ap.configuration;
/**
 *
 * @author andrew
 */
public class Config {
        //Windows on Nout 
        //public static String questionerPath="D:\\questioner\\";
        //public static  String classificationXMLPath=questionerPath+"data\\classification.xml";
        //public static  String questionsPath=questionerPath+"data\\questions\\";
        
        //Linux on Nout
        //public static String questionerPath="/home/andrew/GitRepositories/JustForFunPROJECTS/questioner/";
        //public static  String classificationXMLPath=questionerPath+"data/classification.xml";
        //public static  String questionsPath=questionerPath+"data/questions/";       

        //Linux , nout on GlobalLogic
        private static String questionerPath="";//="/home/andrew/git/JustForFunPROJECTS/questioner/";
        public  static String classificationXMLPath ="";//questionerPath+"data/classification.xml";
        public  static String questionsPath="";//questionerPath+"data/questions/";    
        public  static String imagesPath="";
        public  static String sourcesPath="";
        public  static String fileSeparator=System.getProperty("file.separator");

       public static void setQuestionerPath(String val){
             questionerPath=val;
             classificationXMLPath=questionerPath+"/data/classification.xml";
             questionsPath=questionerPath+"/data/questions/";
             imagesPath=questionerPath+"/data/images/";
             sourcesPath=questionerPath+"/data/sources/";
       }
       
        public static String getQuestionerPath(){
            return Config.questionerPath;
        }
}