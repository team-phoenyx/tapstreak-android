package io.tapstreak.json_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Terrance on 10/14/2017.
 */

public class Authentication {

    @SerializedName("resp_code")
    @Expose
    private String respCode;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("username")
    @Expose
    private String username;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}