package com.ap.questinerwebapp.client.Presenter;

import com.ap.questinerwebapp.client.rpc.Contact.ContactServiceAsync;
import com.ap.questinerwebapp.shared.Contact;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class EditContactPresenter implements Presenter {
    public interface Display {
        HasClickHandlers getSaveButton();

        HasClickHandlers getCancelButton();

        HasValue<String> getFirstName();

        HasValue<String> getLastName();

        HasValue<String> getEmailAddress();

        Widget asWidget();
    }

    private Contact contact;
    private final ContactServiceAsync rpcService;
    private final HandlerManager eventBus;
    private final Display display;

    public EditContactPresenter(ContactServiceAsync rpcService, HandlerManager eventBus, Display display) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.contact = new Contact();
        this.display = display;
        bind();
    }

    public EditContactPresenter(ContactServiceAsync rpcService, HandlerManager eventBus, Display display, String id) {
        this.rpcService = rpcService;
        this.eventBus = eventBus;
        this.display = display;
        bind();

        rpcService.getContact(id, new AsyncCallback<Contact>() {
            public void onSuccess(Contact result) {
                contact = result;
                EditContactPresenter.this.display.getFirstName().setValue(contact.getFirstName());
                EditContactPresenter.this.display.getLastName().setValue(contact.getLastName());
                EditContactPresenter.this.display.getEmailAddress().setValue(contact.getEmailAddress());
            }

            public void onFailure(Throwable caught) {
                Window.alert("Error retrieving contact");
            }
        });

    }

    @Override
    public void go(HasWidgets container) {
        bind();
        container.clear();
        container.add(display.asWidget());
    }

    private void bind() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                goBackAfterCancel();
            }
        });

    }

    private void goBackAfterCancel() {
        History.newItem("list");
    }
}
