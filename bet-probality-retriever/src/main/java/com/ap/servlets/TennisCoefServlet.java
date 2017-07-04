package com.ap.servlets;

import com.ap.model.BetCoef;
import com.ap.services.WebRetriever;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/tennisCoef")
public class TennisCoefServlet extends HttpServlet{

    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String playerName = req.getParameter("name");
        BetCoef coef = WebRetriever.getBetCoef(playerName);

        mapper.writeValue(resp.getWriter(), coef);
    }
}
