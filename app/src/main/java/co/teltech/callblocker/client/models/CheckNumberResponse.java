package co.teltech.callblocker.client.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class CheckNumberResponse {

    @SerializedName("number_type")
    private String numberType;

    public CheckNumberResponse(String numberType) {
        this.numberType = numberType;
    }

    public String getNumberType() {
        return numberType;
    }

}
