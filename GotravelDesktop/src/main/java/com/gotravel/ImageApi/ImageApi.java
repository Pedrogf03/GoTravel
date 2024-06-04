package com.gotravel.ImageApi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ImageApi {

    private static final String BASE_URL = "https://api.api-ninjas.com/v1/randomimage?category=city&width=1920&height=1080";
    private static final String API_KEY = "mvJLckP+ODVVNtwGqUupVw==FY5JmodyfUIlt5QX";

    public static byte[] getImage() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("X-Api-Key", API_KEY)
                .header("Accept", "image/jpg")
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            System.err.println("Error: " + response.statusCode());
            return null;
        }
    }


}
