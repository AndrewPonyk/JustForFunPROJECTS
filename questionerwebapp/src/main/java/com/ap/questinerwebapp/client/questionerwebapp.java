package com.ap.questinerwebapp.client;

import com.ap.questinerwebapp.client.rpc.Contact.ContactService;
import com.ap.questinerwebapp.client.rpc.Contact.ContactServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class questionerwebapp implements EntryPoint {

	public void onModuleLoad() {
		Window.alert("It will be questionerwebapp.This is devMode");
		ContactServiceAsync contactServiceAsync = GWT.create(ContactService.class);
		HandlerManager eventBus = new HandlerManager(null);
		AppController appViewer = new AppController(contactServiceAsync, eventBus);
		appViewer.go(RootPanel.get());
	}
}