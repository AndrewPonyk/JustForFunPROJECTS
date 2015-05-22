package com.ap.questinerwebapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ap.questinerwebapp.client.rpc.questionerwebapprpc.QuestionerService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class QuestionerRPCImpl extends RemoteServiceServlet implements
		QuestionerService {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	String GIT_URL = "https://raw.githubusercontent.com/AndrewPonyk/JustForFunPROJECTS/master/questioner/data/";
	String CLASSIFICATION_URL = GIT_URL + "classification.xml";
	@Override
	public String getClassification() {
		return getHTML(CLASSIFICATION_URL);
	}

	public String getHTML(String urlToRead) {
	      URL url;
	      HttpURLConnection conn;
	      BufferedReader rd;
	      String line;
	      String result = "";
	      try {
	         url = new URL(urlToRead);
	         conn = (HttpURLConnection) url.openConnection();
	         conn.setRequestMethod("GET");
	         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	         while ((line = rd.readLine()) != null) {
	            result += line;
	         }
	         rd.close();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return result;
	   }

	@Override
	public String getCategoryQuestions(String filename) {
		 String questions = getHTML(GIT_URL + "questions/" + filename);

		 questions = questions.replaceAll("&amp;nbsp;", " ");
		 questions = questions.replaceAll("/data/images",
				 "https://raw.githubusercontent.com/AndrewPonyk/JustForFunPROJECTS/master/questioner/data/images");
		 return questions;
	}

}