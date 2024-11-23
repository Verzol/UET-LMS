package service;

import javafx.scene.Node;
import java.util.ArrayList;
import java.util.List;

public class BookDataService {
    private static List<Node> loadedBooks = new ArrayList<>();

    public static List<Node> getLoadedBooks() {
        return loadedBooks;
    }

    public static void setLoadedBooks(List<Node> books) {
        loadedBooks = books;
    }

    public static boolean isBooksLoaded() {
        return !loadedBooks.isEmpty();
    }
}
