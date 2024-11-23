package utils;

public class SessionManager {
    private static int currentUserId;
    private static String currentUsername;

    public static void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void clearSession() {
        currentUserId = 0;
        currentUsername = null;
    }
}
