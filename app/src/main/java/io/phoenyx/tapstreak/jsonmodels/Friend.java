
package io.phoenyx.tapstreak.jsonmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Friend {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("streak_length")
    @Expose
    private String streakLength;
    @SerializedName("last_streak")
    @Expose
    private String lastStreak;
    @SerializedName("first_streak")
    @Expose
    private String firstStreak;

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

    public String getStreakLength() {
        return streakLength;
    }

    public void setStreakLength(String streakLength) {
        this.streakLength = streakLength;
    }

    public String getLastStreak() {
        return lastStreak;
    }

    public void setLastStreak(String lastStreak) {
        this.lastStreak = lastStreak;
    }

    public String getFirstStreak() {
        return firstStreak;
    }

    public void setFirstStreak(String firstStreak) {
        this.firstStreak = firstStreak;
    }

}
