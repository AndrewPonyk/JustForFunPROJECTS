package com.ap.questinerwebapp.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by andrew on 17.05.15.
 */
public class AddContactEvent extends GwtEvent<AddContactEventHandler>{
    public static Type<AddContactEventHandler> TYPE = new Type<AddContactEventHandler>();

    @Override
    public Type<AddContactEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AddContactEventHandler addContactHandler) {
        addContactHandler.onAddContact(this);
    }
}
