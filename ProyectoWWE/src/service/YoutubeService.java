package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

/**
 *
 * @author iagom
 */
public class YoutubeService {

    private static final String API_KEY = "AIzaSyC1CSnD6Jbcw96A8v5VPq6q_c2eGyxXsG4";

    public String buscarVideo(String consulta) {
        try {
            String query = consulta.replace(" ", "+");
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + query + "&type=video&maxResults=1&key=" + API_KEY;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            String videoId = json.getJSONArray("items").getJSONObject(0)
                    .getJSONObject("id").getString("videoId");

            return "https://www.youtube.com/watch?v=" + videoId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
