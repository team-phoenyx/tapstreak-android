package io.tapstreak.json_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Terrance on 10/14/2017.
 */

public class Salt {

    @SerializedName("resp_code")
    @Expose
    private String respCode;
    @SerializedName("salt")
    @Expose
    private String salt;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}