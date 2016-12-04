package org.webbitserver.rest;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

class HttpVerbHandler implements HttpHandler {
    private final String verb;
    private final HttpHandler httpHandler;

    public HttpVerbHandler(String verb, HttpHandler httpHandler) {
        this.verb = verb;
        this.httpHandler = httpHandler;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        if (request.method().equalsIgnoreCase(verb)) {
            httpHandler.handleHttpRequest(request, response, control);
        } else {
            control.nextHandler();
        }
    }
}
