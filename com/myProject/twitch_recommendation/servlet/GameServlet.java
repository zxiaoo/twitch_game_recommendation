package com.myProject.twitch_recommendation.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myProject.twitch_recommendation.entity.Game;
import external.TwitchClient;
import external.TwitchException;
import org.json.JSONObject;


import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class GameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // get gameName from request URL
        String gameName = request.getParameter("game_name");
        TwitchClient client = new TwitchClient();

        response.setContentType("application/json");
        try {
            // return the specific game information if gameName is provided in the request URL
            // otherwise, return the top X games.
            if (gameName != null) {
                response.getWriter().print(new ObjectMapper().writeValueAsString(client.searchGame(gameName)));
            } else {
                response.getWriter().print(new ObjectMapper().writeValueAsString(client.topGame(0))); // use default limit
            }
        } catch (TwitchException e) {
            throw new ServletException(e);
        }
    }

}
