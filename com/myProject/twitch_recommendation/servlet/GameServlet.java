package com.myProject.twitch_recommendation.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myProject.twitch_recommendation.entity.Game;
import org.json.JSONObject;


import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class GameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        ObjectMapper mapper = new ObjectMapper();

        Game.Builder builder = new Game.Builder();
        builder.setName("World of warcraft");
        builder.setDeveloper("Blizzard Entertainment");
        builder.setReleaseTime("Feb 11, 2005");
        builder.setWebsite("https://www.worldofwarcraft.com");
        builder.setPrice(49.99);

        Game game = builder.build();

        response.getWriter().print(mapper.writeValueAsString(game));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    }
}
