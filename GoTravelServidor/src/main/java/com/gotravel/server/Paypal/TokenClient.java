package com.gotravel.server.Paypal;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class TokenClient {

    private String accessToken;

    private static final String CLIENT_ID =
            "AfZIYHTA99Uktc2T4jpWKsXgQlY3HRx925HZTAHtyu0_Cyd-68zVelGEd2byWcIGjR_HX_25-ZOMk3VX";

    private static final String CLIENT_SECRET =
            "EExHjSx9dl1uC6WHdy7LzO9voSVnUMFfKQnYzyg-6uQsf88SWtqyW1WtOJ359s90hDFlUPJTkFj-1XaQ";

    public String obtenerTokenPaypal() throws IOException {
        String url =
                "https://api-m.sandbox.paypal.com/v1/oauth2/token";

        OkHttpClient client = new OkHttpClient.Builder().build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials");

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", Credentials.basic(CLIENT_ID, CLIENT_SECRET))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Error: " + response.code());
                System.out.println("Response body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            } else {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);

                // Guarda la información en variables
                accessToken = json.getString("access_token");

                // Devuelve el token
                return accessToken;
            }
        }
    }

}
