package co.teltech.callblocker.client.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class CheckNumberRequest {

    @SerializedName("incoming_number")
    private String incomingNumber;

    public CheckNumberRequest(String incomingNumber) {
        this.incomingNumber = incomingNumber;
    }

}
