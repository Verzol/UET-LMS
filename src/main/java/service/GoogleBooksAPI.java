package service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.scene.control.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleBooksAPI {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String API_KEY = "AIzaSyAwX6q4VWAACKNbfczZRFhMfp038PqO0ao";

    public static void searchBookByTitle(String title, ListView<String> listView) {
        String requestUrl = API_URL + title.replace(" ", "+") + "&key=" + API_KEY;

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                listView.getItems().clear();
                listView.getItems().add("Error: Unable to fetch data (HTTP " + responseCode + ").");
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
            listView.getItems().clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject book = items.getJSONObject(i).getJSONObject("volumeInfo");

                String bookTitle = book.optString("title", "Unknown Title");

                JSONArray authorsArray = book.optJSONArray("authors");
                String authors = "Unknown Author";
                if (authorsArray != null) {
                    authors = authorsArray.toList().stream()
                            .map(Object::toString)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("Unknown Author");
                }

                String publisher = book.optString("publisher", "Unknown Publisher");
                String publishedDate = book.optString("publishedDate", "Unknown Year");
                String description = book.optString("description", "No description available.");
                String imageUrl = book.has("imageLinks") ?
                        book.getJSONObject("imageLinks").optString("thumbnail", "No image available") :
                        "No image available";

                String bookDetails = bookTitle + " - " + authors + "\nPublisher: " + publisher +
                        "\nPublished: " + publishedDate + "\nDescription: " + description +
                        "\nImage URL: " + imageUrl;

                listView.getItems().add(bookDetails);
            }

        } catch (Exception e) {
            listView.getItems().clear();
            listView.getItems().add("Error: Unable to fetch data. Please try again.");
            e.printStackTrace();
        }
    }
}
