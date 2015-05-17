package com.ap.questinerwebapp.client.rpc.Contact;

import java.util.ArrayList;
import java.util.List;

import com.ap.questinerwebapp.shared.Contact;
import com.ap.questinerwebapp.shared.ContactDetails;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ContactServiceAsync {

	void getStrings(AsyncCallback<List<String>> callback);

	void addContact(Contact contact, AsyncCallback<Contact> callback);

	void deleteContact(String id, AsyncCallback<Boolean> callback);

	void deleteContacts(ArrayList<String> ids,
			AsyncCallback<ArrayList<ContactDetails>> callback);

	void getContact(String id, AsyncCallback<Contact> callback);

	void getContactDetails(AsyncCallback<ArrayList<ContactDetails>> callback);

	void updateContact(Contact contact, AsyncCallback<Contact> callback);
}
