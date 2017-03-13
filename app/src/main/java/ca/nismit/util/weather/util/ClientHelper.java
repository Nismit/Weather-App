package ca.nismit.util.weather.util;

import android.util.Log;

import ca.nismit.util.weather.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientHelper {

    private static String TAG = ClientHelper.class.getSimpleName();
    private static Retrofit retrofit;

    /**
     * It is a helper method which makes an instance for retrofit
     * It returns retrofit service which means you can use retrofit method
     *
     * Also, you have to make sure that to set api url in env.properties
     *
     * @param serviceClass
     * @param <S> Interface class for retrofit
     * @return Retrofit Instance which has already build the api
     * @see /env.properties
     * @see /env.properties.example
     * @see /app/manifests/AndroidManifest.xml
     */
    public static <S> S createService(Class<S> serviceClass) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.OWM_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        return retrofit.create(serviceClass);
    }
}