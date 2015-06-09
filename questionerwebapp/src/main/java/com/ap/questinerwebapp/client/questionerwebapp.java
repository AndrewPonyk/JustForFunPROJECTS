package com.ap.questinerwebapp.client;

import org.apache.xalan.xsltc.compiler.util.NodeType;

import com.ap.questinerwebapp.client.rpc.Contact.ContactService;
import com.ap.questinerwebapp.client.rpc.Contact.ContactServiceAsync;
import com.ap.questinerwebapp.client.rpc.questionerwebapprpc.QuestionerService;
import com.ap.questinerwebapp.client.rpc.questionerwebapprpc.QuestionerServiceAsync;
import com.ap.questinerwebapp.shared.CategoryTreeItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class questionerwebapp implements EntryPoint {

	Node questionsDocumentRoot;

	private QuestionerServiceAsync questionerRPC = GWT
			.create(QuestionerService.class);

	public void onModuleLoad() {
		// Window.alert("It will be questionerwebapp.This is devMode");
		ContactServiceAsync contactServiceAsync = GWT
				.create(ContactService.class);
		HandlerManager eventBus = new HandlerManager(null);
		AppController appViewer = new AppController(contactServiceAsync,
				eventBus);
		// appViewer.go(RootPanel.get());

		////////////////////////////
		getClassification();
	}



	public void getClassification() {
		questionerRPC.getClassification(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {

				Label fullClassification = new Label();
				fullClassification.setText(result);
				parseXML(result);
				// RootPanel.get().add(fullClassification);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

	public void parseXML(String messageXml) {
		CategoryTreeItem rootOfOurClassification = new CategoryTreeItem(
				"Classificationnn", 0, 0, "");
		try {
			// parse the XML document into a DOM
			Document messageDom = XMLParser.parse(messageXml);
			Node rootItem = messageDom.getChildNodes().item(0);
			recursivelyBuildClassificationMap(rootItem, rootOfOurClassification);

			buildGwtTreeBasedOnMap(rootOfOurClassification);

			// //
			// create a label
			final Label labelMessage = new Label();
			labelMessage.setWidth("300");

			// Create a root tree item as department
			TreeItem rootInGWTTree = new TreeItem();
			rootInGWTTree.setText("Classification");

			// create the tree
			Tree tree = new Tree();

			rootInGWTTree = buildGwtTreeBasedOnMap(rootOfOurClassification);

			// add root item to the tree
			tree.addItem(rootInGWTTree);

			// Add text boxes to the root panel.
			final VerticalPanel panel = new VerticalPanel();
			panel.getElement().setAttribute("id", "treePanel");
			panel.setSpacing(10);
			panel.add(tree);
			panel.add(labelMessage);

			Button hideOpenClassification = new Button("Show/Hide classification");
			RootPanel.get("questionsClassificationContainer").add(hideOpenClassification);
			hideOpenClassification.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					panel.setVisible(!panel.isVisible());
				}
			});

			// add the tree to the root panel
			RootPanel.get("questionsClassificationContainer").add(panel);


			tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

				@Override
				public void onSelection(SelectionEvent<TreeItem> event) {
					CategoryTreeItem userObject = (CategoryTreeItem) event.getSelectedItem().getUserObject();
					 labelMessage.setText("Selected Value: "
					            + userObject.getFilename());
					 consoleLog("Requesting : " + userObject.getFilename());

					 if( userObject.getFilename() != null){
						 //Window.alert(DOM.eventGetType(event.ge) == Event.ONDBLCLICK+"");
						 getCategoryQuestionsAndAppendToBody(userObject.getFilename());
					 }

				}
			});


		} catch (DOMException e) {
			Window.alert("Could not parse XML document.");
		}
	}

	public void getCategoryQuestionsAndAppendToBody(String filename){
		questionerRPC.getCategoryQuestions(filename, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				consoleLog("Before parsing:");
				Document messageDom = XMLParser.parse(result);
				questionsDocumentRoot = messageDom.getChildNodes().item(0);
				consoleLog("After parsing");

				Node rootItem = messageDom.getChildNodes().item(0);
				RootPanel.get("questionsList").clear();
				for(int i = 0; i<rootItem.getChildNodes().getLength() ; i++){
					if(rootItem.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
						Element elem = (Element) rootItem.getChildNodes().item(i);
						final String questionId = elem.getAttribute("id");
						elem = (Element) elem.getElementsByTagName("questiontext").item(0);
						String questionText = elem.getChildNodes().item(0).getNodeValue();
						final InlineHTML q = new InlineHTML();
						q.getElement().setAttribute("questionId", questionId);
						q.setHTML(elem.getChildNodes().item(0).getNodeValue());
						q.setStyleName("questionItem");
						q.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								InlineHTML questionAnswer = new InlineHTML();
								questionAnswer.setHTML(getQuestionAnswerById(questionsDocumentRoot, questionId));

								RootPanel.get("questionItemContainer").clear();
								RootPanel.get("questionItemContainer").add(questionAnswer);
							}
						});
						RootPanel.get("questionsList").add(q);
					}
				}
				RootPanel.get("questionsList").getElement().focus();
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	public void recursivelyBuildClassificationMap(Node rootItem,
			CategoryTreeItem category) {
		for (int i = 0; i < rootItem.getChildNodes().getLength(); i++) {
			Node item = rootItem.getChildNodes().item(i);

			if (item.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) item;
				CategoryTreeItem value = new CategoryTreeItem(
						elem.getAttribute("name"), Integer.valueOf(elem.getAttribute("nofquestions")), 0, elem.getAttribute("filename"));
				category.getChildren().put(value.getName(), value);
				recursivelyBuildClassificationMap(elem, value);
			}
		}
	}

	public TreeItem buildGwtTreeBasedOnMap(CategoryTreeItem result) {

		// Create a root tree item as department
		TreeItem department = new TreeItem();
		department.setText(result.toString());
		department.setUserObject(result);
		for (String item : result.getChildren().keySet()) {
			TreeItem temp = buildGwtTreeBasedOnMap(result.getChildren().get(item));
			department.addItem(temp);
		}

		return department;
	}

	Boolean elementHasChildNodes(Node node) {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				return true;
			}
		}
		return false;
	}

	public String getQuestionAnswerById(Node questionsDocumentRoot, String id){
		for(int i = 0;i<questionsDocumentRoot.getChildNodes().getLength();i++){
			Node item = questionsDocumentRoot.getChildNodes().item(i);
			if(questionsDocumentRoot.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE){
				Element elem = (Element) questionsDocumentRoot.getChildNodes().item(i);
				consoleLog("Looking at " + elem.getAttribute("id"));
				if(id.equals(elem.getAttribute("id"))){
					return elem.getElementsByTagName("questionanswer").item(0).getChildNodes().item(0).getNodeValue();
				}
			}
		}
		return "<b>Error</b>";
	}

	native void consoleLog(String message) /*-{
		console.log("me:" + message);
	}-*/;
}