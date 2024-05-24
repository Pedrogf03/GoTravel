package com.gotravel.server.PayPal;

import com.google.gson.Gson;
import lombok.Getter;
import okhttp3.*;

import java.io.IOException;

@Getter
public class PayPalToken {

    private String access_token;

    private OnTokenReceivedListener listener;

    public interface OnTokenReceivedListener {
        void onTokenReceived(String token);
    }

    private static final String CLIENT_ID = "AfZIYHTA99Uktc2T4jpWKsXgQlY3HRx925HZTAHtyu0_Cyd-68zVelGEd2byWcIGjR_HX_25-ZOMk3VX";
    private static final String CLIENT_SECRET = "EExHjSx9dl1uC6WHdy7LzO9voSVnUMFfKQnYzyg-6uQsf88SWtqyW1WtOJ359s90hDFlUPJTkFj-1XaQ";

    public PayPalToken(OnTokenReceivedListener onTokenReceivedListener) {

        this.listener = onTokenReceivedListener;

        String url = "https://api-m.sandbox.paypal.com/v1/oauth2/token";

        OkHttpClient client = new OkHttpClient.Builder().build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create("grant_type=client_credentials", mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", Credentials.basic(CLIENT_ID, CLIENT_SECRET))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                // Parsea la respuesta JSON a la clase TokenResponse
                Gson gson = new Gson();
                PayPalToken tokenResponse = gson.fromJson(response.body().string(), PayPalToken.class);

                // Guarda la información en variables
                access_token = tokenResponse.getAccess_token();

                // Notifica al listener que el token está disponible
                if (listener != null) {
                    listener.onTokenReceived(access_token);
                }
            }
        });
    }


}