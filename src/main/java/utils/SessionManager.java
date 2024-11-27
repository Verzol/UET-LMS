package utils;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static int currentUserId;
    private static String currentUsername;
    private static String currentAvatarPath;

    private static final List<AvatarChangeListener> avatarChangeListeners = new ArrayList<>();

    public interface AvatarChangeListener {
        void onAvatarChanged(String newAvatarPath);
    }

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

    public static void setCurrentAvatarPath(String avatarPath) {
        currentAvatarPath = avatarPath;
        notifyAvatarChangeListeners(avatarPath);
    }

    public static String getCurrentAvatarPath() {
        return currentAvatarPath;
    }

    public static void addAvatarChangeListener(AvatarChangeListener listener) {
        avatarChangeListeners.add(listener);
    }

    public static void removeAvatarChangeListener(AvatarChangeListener listener) {
        avatarChangeListeners.remove(listener);
    }

    private static void notifyAvatarChangeListeners(String newAvatarPath) {
        for (AvatarChangeListener listener : avatarChangeListeners) {
            listener.onAvatarChanged(newAvatarPath);
        }
    }

    public static void clearSession() {
        currentUserId = 0;
        currentUsername = null;
        currentAvatarPath = null;
        notifyAvatarChangeListeners(null);
    }
}
