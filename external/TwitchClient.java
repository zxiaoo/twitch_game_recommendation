package external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myProject.twitch_recommendation.entity.Game;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import sun.net.TelnetProtocolException;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.List;

public class TwitchClient {
    private static final String TOKEN = "Bearer kd6kq50cjwux2x915i3un30imndajr";
    private static final String CLIENT_ID = "clvhqcg72mh7322gdw0j2g6fa8h9br";
    // url to get the info of the top X games from Twitch
    private static final String TOP_GAME_URL = "https://api.twitch.tv/helix/games/top?first=%s"; // need to complete request
    // url to get the info of a certain game from Twitch
    private static final String GAME_SEARCH_URL_TEMPLATE = "https://api.twitch.tv/helix/games?name=%s"; // need to complete request
    private static final int DEFAULT_GAME_LIMIT = 20;

    // build the request url when calling Twitch APIs to the get info for a specific game or the top X games
    private String buildGameURL(String url, String gameName, int limit){
        if (gameName.equals("")) {
            return String.format(url, limit);
        } else {
            try {
                // encode special characters from gameName in URL
                gameName = URLEncoder.encode(gameName, "UTF-8");
            } catch(UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return String.format(url, gameName);
        }
    }

    // send a http request to Twitch, return the body of the HTTP response from Twitch
    private String searchTwitch(String url) throws TwitchException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // define a response handler to parse and return the body of the HTTP response from Twitch
        // no need to consume and close the response manually
        ResponseHandler<String> responseHandler = response -> {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode != 200) {
                System.out.println("Response status: " + response.getStatusLine().getReasonPhrase());
                throw new TwitchException("Failed to get result from Twitch API");
            }
            HttpEntity entity = response.getEntity(); // get response body
            if (entity == null) {
                throw new TwitchException("Failed to get result from Twitch API");
            }
            JSONObject obj = new JSONObject(EntityUtils.toString(entity));
            return obj.getJSONArray("data").toString();
        };

        try {
            // Http request
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", TOKEN);
            request.setHeader("Client-Id", CLIENT_ID);
            return httpClient.execute(request, responseHandler);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to get result from Twitch API");
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // convert JSON data fetched from Twitch to a list of Game objects
    private List<Game> getGameList(String data) throws TwitchException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Arrays.asList(mapper.readValue(data, Game[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TwitchException("Failed to parse game data from Twitch API");
        }
    }

    // Integrate searchTwitch() and getGameList() together.
    // Send HTTP request to search on Twitch for the top X Games, convert the JSON data to a list of Game,
    // and return the list of the top X Games
    public List<Game> topGame(int limit) throws TwitchException {
        if (limit <= 0) {
            limit = DEFAULT_GAME_LIMIT;
        }
        return getGameList(searchTwitch(buildGameURL(TOP_GAME_URL,"", limit)));
    }

    // Integrate searchTwitch() and getGameList() together.
    // Send HTTP request to search on Twitch for the a specific Game.
    public Game searchGame(String gameName) throws TwitchException {
        List<Game> gameList = getGameList(searchTwitch(buildGameURL(GAME_SEARCH_URL_TEMPLATE, gameName, 0)));
        if (gameList.size() != 0) {
            return gameList.get(0);
        }
        return null;
    }

}
