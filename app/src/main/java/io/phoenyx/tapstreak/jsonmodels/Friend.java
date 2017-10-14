
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
    private int streakLength;
    @SerializedName("last_streak")
    @Expose
    private long lastStreak;
    @SerializedName("streak_start")
    @Expose
    private long streakStart;

    public String getFriendId() {
        return userId;
    }

    public void setFriendId(String userId) {
        this.userId = userId;
    }

    public String getFriendUsername() {
        return username;
    }

    public void setFriendUsername(String username) {
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

    public long getFirstStreak() {
        return streakStart;
    }

    public void setFirstStreak(long startStreak) {
        this.streakStart = startStreak;
    }

}
