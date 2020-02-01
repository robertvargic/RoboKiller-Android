package co.teltech.callblocker.client;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.teltech.callblocker.BuildConfig;
import co.teltech.callblocker.client.constants.ApiConstants;
import co.teltech.callblocker.client.service.CallBlockerService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tomislavtusek on 17/08/2018.
 */

public class RestClient {

    private static RestClient instance;

    private CallBlockerService apiService;
    private Context context;

    private Interceptor interceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Api-Key", ApiConstants.API_KEY)
                    .build();
            return chain.proceed(newRequest);
        }
    };

    private RestClient(Context context) {
        this.context = context;

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.interceptors().add(loggingInterceptor);
        }
        OkHttpClient client = builder
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        this.apiService = retrofit.create(CallBlockerService.class);
    }

    public static CallBlockerService getApiService(Context context) {
        if (instance == null) {
            instance = new RestClient(context);
        }
        return instance.apiService;
    }

}
