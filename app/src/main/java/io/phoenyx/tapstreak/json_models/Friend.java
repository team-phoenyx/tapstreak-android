
package io.phoenyx.tapstreak.json_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Friend {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("streak_length")
    @Expose
    private int streakLength;
    @SerializedName("last_streak")
    @Expose
    private long lastStreak;

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

    public int getStreakLength() {
        return streakLength;
    }

    public void setStreakLength(int streakLength) {
        this.streakLength = streakLength;
    }

    public long getLastStreak() {
        return lastStreak;
    }

    public void setLastStreak(long lastStreak) {
        this.lastStreak = lastStreak;
    }

}