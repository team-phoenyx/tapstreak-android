
package io.phoenyx.tapstreak.jsonmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Salt {

    @SerializedName("salt")
    @Expose
    private String salt;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

}
