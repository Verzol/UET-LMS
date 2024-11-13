package controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class GoogleBooksAPI {
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    public static void searchBookByTitle(String title) {
        String requestUrl = API_URL + title.replace(" ", "+");

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                String inline = "";
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
                scanner.close();

                JSONObject json = new JSONObject(inline);
                System.out.println("Results from Google Books:");
                json.getJSONArray("items").forEach(item -> {
                    JSONObject book = (JSONObject) item;
                    System.out.println("Title: " + book.getJSONObject("volumeInfo").getString("title"));
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}