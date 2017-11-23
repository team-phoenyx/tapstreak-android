package io.tapstreak.json_models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Terrance on 10/14/2017.
 */

public class ResponseCode {

    @SerializedName("resp_code")
    @Expose
    private String respCode;

    @SerializedName("resp_msg")
    @Expose
    private String respMsg;

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }
}