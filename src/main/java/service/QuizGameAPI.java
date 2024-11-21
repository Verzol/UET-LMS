package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class QuizGameAPI {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String API_KEY = "AIzaSyAwX6q4VWAACKNbfczZRFhMfp038PqO0ao";


    public static List<String> fetchBooks(String query) {
        return fetchBooksByQuery(query);
    }


    public static List<String> fetchBooksByAuthor(String author) {
        String authorQuery = "inauthor:" + author;
        return fetchBooksByQuery(authorQuery);
    }


    private static List<String> fetchBooksByQuery(String query) {
        List<String> bookList = new ArrayList<>();

        try {

            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String requestUrl = API_URL + encodedQuery + "&key=" + API_KEY;


            HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
            connection.setRequestMethod("GET");


            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                bookList.add("Error: Unable to fetch data (HTTP " + responseCode + ")");
                return bookList;
            }


            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();


            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            if (!jsonResponse.has("items")) {
                bookList.add("No books found for query: " + query);
                return bookList;
            }

            JsonArray items = jsonResponse.getAsJsonArray("items");
            for (JsonElement item : items) {
                JsonObject book = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                String title = book.has("title") ? book.get("title").getAsString() : "Unknown Title";
                String authors = book.has("authors")
                        ? book.getAsJsonArray("authors").toString().replaceAll("[\\[\\]\"]", "")
                        : "Unknown Author";

                bookList.add("Title: " + title );
            }

        } catch (Exception e) {
            bookList.add("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return bookList;
    }

}
