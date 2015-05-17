package com.ap.questinerwebapp.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by andrew on 17.05.15.
 */
public interface AddContactEventHandler extends EventHandler {
    void onAddContact(AddContactEvent addContactEvent);
}
