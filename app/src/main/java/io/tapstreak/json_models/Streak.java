package io.tapstreak.json_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by terrance on 11/25/17.
 */

public class Streak {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("streak_length")
    @Expose
    private Integer streakLength;
    @SerializedName("last_streak")
    @Expose
    private Long lastStreak;
    @SerializedName("_id")
    @Expose
    private String id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStreakLength() {
        return streakLength;
    }

    public void setStreakLength(Integer streakLength) {
        this.streakLength = streakLength;
    }

    public Long getLastStreak() {
        return lastStreak;
    }

    public void setLastStreak(Long lastStreak) {
        this.lastStreak = lastStreak;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
