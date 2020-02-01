package co.teltech.callblocker.client.service;

import co.teltech.callblocker.client.constants.ApiConstants;
import co.teltech.callblocker.client.models.CheckNumberRequest;
import co.teltech.callblocker.client.models.CheckNumberResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public interface CallBlockerService {

    @POST(ApiConstants.API_CHECK_NUMBER)
    Call<CheckNumberResponse> checkNumber(@Body CheckNumberRequest checkNumberRequest);

}
