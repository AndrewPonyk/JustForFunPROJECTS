package com.ap.questinerwebapp.client.rpc.questionerwebapprpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("questionerService")
public interface QuestionerService extends RemoteService{
	String getClassification();
	String getCategoryQuestions(String filename);
}