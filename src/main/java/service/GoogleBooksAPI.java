package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javafx.scene.control.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.json.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.json.JsonObject;

public class GoogleBooksAPI {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String API_KEY = "AIzaSyAwX6q4VWAACKNbfczZRFhMfp038PqO0ao";

    public static void searchBookByTitle(String title, ListView<String> listView) {
        String requestUrl = API_URL + title.replace(" ", "+") + "&key=" + API_KEY;

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                listView.getItems().clear();
                listView.getItems().add("Error: Unable to fetch data (HTTP " + responseCode + ")");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());

            if (!json.has("items")) {
                listView.getItems().clear();
                listView.getItems().add("No results found for \"" + title + "\".");
                return;
            }

            JSONArray items = json.getJSONArray("items");
            System.out.println("Updating ListView...");
            listView.getItems().clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject book = items.getJSONObject(i).getJSONObject("volumeInfo");

                String bookTitle = book.getString("title");
                String authors = book.has("authors") ? book.getJSONArray("authors").join(", ").replaceAll("\"", "") : "Unknown Author";
                String publisher = book.has("publisher") ? book.getString("publisher") : "Unknown Publisher";
                String publishedDate = book.has("publishedDate") ? book.getString("publishedDate") : "Unknown Year";
                String description = book.has("description") ? book.getString("description") : "No description available.";
                String imageUrl = book.has("imageLinks") ?
                        book.getJSONObject("imageLinks").optString("thumbnail", "No image available") :
                        "No image available";

                String bookDetails = bookTitle + " - " + authors + "\nPublisher: " + publisher + "\nPublished: " + publishedDate + "\nDescription: " + description + "\nImage URL: " + imageUrl;
                listView.getItems().add(bookDetails);
                System.out.println("Added book: " + bookTitle);
            }

        } catch (Exception e) {
            System.out.println("Error while connecting to API: " + e.getMessage());
            listView.getItems().clear();
            listView.getItems().add("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
