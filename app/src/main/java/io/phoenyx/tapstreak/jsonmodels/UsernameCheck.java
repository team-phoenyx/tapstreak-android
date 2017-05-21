package io.phoenyx.tapstreak.jsonmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsernameCheck {

    @SerializedName("alreadyExists")
    @Expose
    private String alreadyExists;

    public String getAlreadyExists() {
        return alreadyExists;
    }

    public void setAlreadyExists(String alreadyExists) {
        this.alreadyExists = alreadyExists;
    }

}