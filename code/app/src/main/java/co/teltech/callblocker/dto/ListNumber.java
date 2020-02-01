package co.teltech.callblocker.dto;

import java.io.Serializable;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class ListNumber implements Serializable {

    private String name;
    private String number;

    public ListNumber(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

}
