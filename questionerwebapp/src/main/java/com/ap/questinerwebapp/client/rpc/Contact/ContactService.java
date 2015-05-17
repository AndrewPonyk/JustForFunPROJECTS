package com.ap.questinerwebapp.client.rpc.Contact;

import java.util.ArrayList;
import java.util.List;

import com.ap.questinerwebapp.shared.Contact;
import com.ap.questinerwebapp.shared.ContactDetails;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("contactsService")
public interface ContactService extends RemoteService {
	Contact addContact(Contact contact);

	Boolean deleteContact(String id);

	ArrayList<ContactDetails> deleteContacts(ArrayList<String> ids);

	ArrayList<ContactDetails> getContactDetails();

	Contact getContact(String id);

	Contact updateContact(Contact contact);

	// my temp
	public List<String> getStrings();
}
