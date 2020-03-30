package com.bullb.ctf.API;



import android.content.Context;
import android.os.Build;

import com.bullb.ctf.BuildConfig;
import com.bullb.ctf.R;
import com.bullb.ctf.Utils.LanguageUtils;
import com.bullb.ctf.Utils.SharedPreference;
import com.bullb.ctf.Utils.SharedUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bullb.ctf.ServerPreference.SERVER_VERSION_HK;
import static com.bullb.ctf.ServerPreference.getServerUrl;


public class ServiceGenerator {
    public static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl( BuildConfig.SERVER_URL_HK)
            .addConverterFactory(GsonConverterFactory.create());


    public static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    public static <S> S createService(Class<S> serviceClass, final Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("x-ctf-app-version", String.valueOf(BuildConfig.VERSION_CODE))
                        .addHeader("x-ctf-app-id", "ctf-smart-talent")
                        .addHeader("x-client-os-platform", "android")
                        .addHeader("x-client-os-version", String.valueOf(android.os.Build.VERSION.SDK_INT))
                        .addHeader("x-app-language", LanguageUtils.getLanguageSimbol(context))
                        .addHeader("x-client-id", SharedPreference.getUUID(context))
                        .addHeader("x-ctf-content-type", "application/json; charset=UTF-8")
                        .addHeader("x-ctf-encrypted", "1")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);  // <-- this is the important line!
        httpClient.connectTimeout(120, TimeUnit.SECONDS);
        httpClient.readTimeout(120, TimeUnit.SECONDS);
        httpClient.writeTimeout(120, TimeUnit.SECONDS);

        builder.baseUrl(getServerUrl(context));

        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

    public void changeServer(Context context){
        builder.baseUrl(getServerUrl(context));

    }


    public static <S> S createServiceForImage(Class<S> serviceClass, final Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .addHeader("x-ctf-app-version", String.valueOf(BuildConfig.VERSION_CODE))
                        .addHeader("x-ctf-app-id", "ctf-smart-talent")
                        .addHeader("x-client-os-platform", "android")
                        .addHeader("x-client-os-version", String.valueOf(android.os.Build.VERSION.SDK_INT))
                        .addHeader("x-app-language", LanguageUtils.getLanguageSimbol(context))
                        .addHeader("x-client-id", SharedPreference.getUUID(context))
                        .addHeader("x-ctf-content-type", "image/png")
                        .addHeader("x-ctf-encrypted", "1")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);  // <-- this is the important line!
        httpClient.connectTimeout(120, TimeUnit.SECONDS);
        httpClient.readTimeout(120, TimeUnit.SECONDS);
        httpClient.writeTimeout(120, TimeUnit.SECONDS);

        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }







}
