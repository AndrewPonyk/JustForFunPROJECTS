package com.ap.questinerwebapp.client.Presenter;

import java.util.ArrayList;
import java.util.List;

import com.ap.questinerwebapp.client.rpc.Contact.ContactServiceAsync;
import com.ap.questinerwebapp.shared.ContactDetails;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class ContactsPresenter implements Presenter{
	public interface Display{
		HasClickHandlers getAddButton();
		HasClickHandlers getDeleteButton();
		HasClickHandlers getList(); // this will be table of contacts : FlexTable
		void setData(List<String> data);
		int getClickedRow(ClickEvent event);
		List<Integer> getSelectedRows();
		Widget asWidget();
	}

	private List<ContactDetails> contactDetails;

	private final ContactServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;

	public ContactsPresenter(ContactServiceAsync rpcService,
			HandlerManager eventBus, Display view) {
		super();
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = view;
	}
	@Override
	public void go(HasWidgets container) {
		bind();
		//container.clear();

		container.add(display.asWidget());
		fetchContactDetails();
	}

	private void fetchContactDetails() {
		rpcService.getContactDetails(new AsyncCallback<ArrayList<ContactDetails>>() {
					public void onSuccess(ArrayList<ContactDetails> result) {
						contactDetails = result;
						sortContactDetails();
						List<String> data = new ArrayList<String>();

						for (int i = 0; i < result.size(); ++i) {
							data.add(contactDetails.get(i).getDisplayName());
						}

						display.setData(data);
					}

					public void onFailure(Throwable caught) {
						Window.alert("Error fetching contact details");
					}
				});
	}

	// most interesting method in Presenter
	private void bind() {
		display.getDeleteButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				deleteSelectedContacts();
			}
		});
	}

	public void sortContactDetails() {
		// Yes, we could use a more optimized method of sorting, but the
		// point is to create a test case that helps illustrate the higher
		// level concepts used when creating MVP-based applications.
		//
		for (int i = 0; i < contactDetails.size(); ++i) {
			for (int j = 0; j < contactDetails.size() - 1; ++j) {
				if (contactDetails
						.get(j)
						.getDisplayName()
						.compareToIgnoreCase(
								contactDetails.get(j + 1).getDisplayName()) >= 0) {
					ContactDetails tmp = contactDetails.get(j);
					contactDetails.set(j, contactDetails.get(j + 1));
					contactDetails.set(j + 1, tmp);
				}
			}
		}
	}

	private void deleteSelectedContacts() {
		List<Integer> selectedRows = display.getSelectedRows();
		ArrayList<String> ids = new ArrayList<String>();

		for (int i = 0; i < selectedRows.size(); ++i) {
			ids.add(contactDetails.get(selectedRows.get(i)).getId());
		}

		rpcService.deleteContacts(ids,
				new AsyncCallback<ArrayList<ContactDetails>>() {
					public void onSuccess(ArrayList<ContactDetails> result) {
						contactDetails = result;
						sortContactDetails();
						List<String> data = new ArrayList<String>();

						for (int i = 0; i < result.size(); ++i) {
							data.add(contactDetails.get(i).getDisplayName());
						}
						display.setData(data);
					}

					public void onFailure(Throwable caught) {
						Window.alert("Error deleting selected contacts");
					}
				});
	}

	public List<ContactDetails> getContactDetails() {
		return contactDetails;
	}
	public void setContactDetails(List<ContactDetails> contactDetails) {
		this.contactDetails = contactDetails;
	}

	public ContactDetails getContactDetail(int index){
		return contactDetails.get(index);
	}
}
