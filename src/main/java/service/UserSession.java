package service;

public class UserSession {
    private static String currentUsername;

    public static void setUsername(String username) {
        currentUsername = username;
    }

    public static String getUsername() {
        return currentUsername;
    }

    public static boolean isUserLoggedIn() {
        return currentUsername != null && !currentUsername.isEmpty();
    }
}
