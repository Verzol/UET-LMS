package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class QuizGameAPI {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String API_KEY = "AIzaSyAwX6q4VWAACKNbfczZRFhMfp038PqO0ao";


    public static List<String> fetchBooks(String query) {
        return fetchBooksByQuery(query, true);
    }


    public static List<String> fetchBooksByAuthor(String author) {
        String authorQuery = "inauthor:" + author;
        return fetchBooksByQuery(authorQuery, true);
    }

    public static List<String> fetchAuthorsByBook(String bookTitle) {
        return fetchBooksByQuery(bookTitle, false);
    }

    private static List<String> fetchBooksByQuery(String query, boolean isAuthorSearch) {
        List<String> resultList = new ArrayList<>();

        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String requestUrl = API_URL + encodedQuery + "&key=" + API_KEY;

            HttpURLConnection connection = (HttpURLConnection) new URL(requestUrl).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                resultList.add("Error: Unable to fetch data (HTTP " + responseCode + ")");
                return resultList;
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
                resultList.add("No results found for query: " + query);
                return resultList;
            }

            JsonArray items = jsonResponse.getAsJsonArray("items");
            for (JsonElement item : items) {
                JsonObject book = item.getAsJsonObject().getAsJsonObject("volumeInfo");

                if (isAuthorSearch) {
                    // Nếu là tìm sách theo tác giả, trả về tên sách
                    String title = book.has("title") ? book.get("title").getAsString() : "Unknown Title";
                    resultList.add("Title: " + title);
                } else {
                    // Nếu là tìm tác giả theo sách, trả về tên tác giả
                    String authors = book.has("authors")
                            ? book.getAsJsonArray("authors").toString().replaceAll("[\\[\\]\"]", "")
                            : "Unknown Author";
                    resultList.add(authors);
                }
            }

        } catch (Exception e) {
            resultList.add("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return resultList;
    }


}
