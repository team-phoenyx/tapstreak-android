
package io.tapstreak.json_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Friend {

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("last_seen_time")
    @Expose
    private long lastSeenTime;

    @SerializedName("last_seen_lat")
    @Expose
    private double lat;

    @SerializedName("last_seen_lon")
    @Expose
    private double lon;

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

    public long getLastSeenTime() {
        return lastSeenTime;
    }

    public void setLastSeenTime(long lastSeenTime) {
        this.lastSeenTime = lastSeenTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}