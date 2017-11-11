package io.phoenyx.tapstreak.json_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Terrance on 10/14/2017.
 */

public class User {

    @SerializedName("resp_code")
    @Expose
    private String respCode;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("friends")
    @Expose
    private List<Friend> friends = null;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
    }

}