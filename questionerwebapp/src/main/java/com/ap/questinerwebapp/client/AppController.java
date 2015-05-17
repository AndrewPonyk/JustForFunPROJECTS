package com.ap.questinerwebapp.client;

import com.ap.questinerwebapp.client.Presenter.ContactsPresenter;
import com.ap.questinerwebapp.client.Presenter.Presenter;
import com.ap.questinerwebapp.client.View.ContactsView;
import com.ap.questinerwebapp.client.rpc.Contact.ContactServiceAsync;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class AppController implements Presenter, ValueChangeHandler<String> {
	private HasWidgets container;
	private final ContactServiceAsync rpcService;
	private final HandlerManager eventBus;

	public AppController (ContactServiceAsync rpcService, HandlerManager eventBus) {
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);

	}

	@Override
	public void go(HasWidgets container) {
		this.container = container;

		if("".equals(History.getToken())){
			History.newItem("list");
		}else{
			History.fireCurrentHistoryState();
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if(token != null){
			Presenter presenter = null;
			switch (token) {
			case "list":
				presenter = new ContactsPresenter(rpcService, eventBus, new ContactsView());
				break;
			}

			if(presenter != null){
				presenter.go(this.container);
			}
		}
	}

}