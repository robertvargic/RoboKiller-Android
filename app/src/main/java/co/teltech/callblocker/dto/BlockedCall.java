package co.teltech.callblocker.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class BlockedCall implements Serializable {

    private String incomingNumber;
    private Date eventDate;

    public BlockedCall(String incomingNumber, Date eventDate) {
        this.incomingNumber = incomingNumber;
        this.eventDate = eventDate;
    }

    public String getIncomingNumber() {
        return incomingNumber;
    }

    public Date getEventDate() {
        return eventDate;
    }
}
