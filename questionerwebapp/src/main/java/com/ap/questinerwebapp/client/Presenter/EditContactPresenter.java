package com.ap.questinerwebapp.client.Presenter;

import com.ap.questinerwebapp.client.rpc.Contact.ContactServiceAsync;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class EditContactPresenter implements Presenter{
	public interface Display {
		HasClickHandlers getSaveButton();
		HasClickHandlers getAddButton();
		HasValue<String> getFirstName();
		HasValue<String> getLastName();
		HasValue<String> getEmailAddress();
		Widget asWidget();
	}

	private Display display;
	private final HandlerManager eventBus;
	private final ContactServiceAsync rpcService;


	public EditContactPresenter(Display view, HandlerManager eventBus,
			ContactServiceAsync rpcService) {
		super();
		this.display = view;
		this.eventBus = eventBus;
		this.rpcService = rpcService;
	}

	@Override
	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());
	}

	private void bind() {

	}
}
