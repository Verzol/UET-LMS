package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class ChatbotService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    private static final String API_KEY = "AIzaSyBBDKPIlKYvh4i0mTa5PNo0b3sAuGc9cX0";

    public String getChatbotResponse(String userMessage) throws Exception {

        JsonObject payload = new JsonObject();
        JsonArray contents = new JsonArray();

        JsonObject content = new JsonObject();
        JsonArray messageParts = new JsonArray();
        JsonObject part = new JsonObject();


        part.addProperty("text", userMessage);
        messageParts.add(part);
        content.add("parts", messageParts);
        contents.add(content);

        payload.add("contents", contents);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "?key=" + API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                .build();


        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        JsonObject jsonResponse = new Gson().fromJson(response.body(), JsonObject.class);


        if (jsonResponse.has("candidates")) {
            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            if (!candidates.isEmpty()) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject contentObj = firstCandidate.getAsJsonObject("content");
                JsonArray candidateParts = contentObj.getAsJsonArray("parts");
                if (!candidateParts.isEmpty()) {
                    return candidateParts.get(0).getAsJsonObject().get("text").getAsString();
                }
            }
        }


        return "Error: No valid response from Gemini API. Response: " + jsonResponse.toString();
    }
}