package io.phoenyx.tapstreak;

/**
 * Created by terrance on 5/20/17.
 */

public class Friend {
    private String id, username;
    private int streak, last_streak, streak_start;

    public Friend(String id, String username, int streak, int last_streak, int streak_start) {
        this.id = id;
        this.username = username;
        this.streak = streak;
        this.last_streak = last_streak;
        this.streak_start = streak_start;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLast_streak() {
        return last_streak;
    }

    public void setLast_streak(int last_streak) {
        this.last_streak = last_streak;
    }

    public int getStreak_start() {
        return streak_start;
    }

    public void setStreak_start(int streak_start) {
        this.streak_start = streak_start;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
}
