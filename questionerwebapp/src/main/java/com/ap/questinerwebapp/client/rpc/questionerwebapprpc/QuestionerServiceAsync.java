package com.ap.questinerwebapp.client.rpc.questionerwebapprpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QuestionerServiceAsync {
	void getClassification(AsyncCallback<String> callback);

	void getCategoryQuestions(String filename, AsyncCallback<String> callback);
}