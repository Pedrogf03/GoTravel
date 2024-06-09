package com.gotravel.server.Paypal;

import com.gotravel.server.model.Contratacion;
import com.gotravel.server.model.Pago;
import com.gotravel.server.model.Servicio;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Checkout {

    private String token;

    public Checkout() throws IOException {
        this.token = new TokenClient().obtenerTokenPaypal();
    }

    public String crearPedido(Servicio s, String tipoCliente) throws IOException {

        String url =
                "https://api-m.sandbox.paypal.com/v2/checkout/orders";

        OkHttpClient client = new OkHttpClient.Builder().build();

        MediaType mediaType = MediaType.parse("application/json");

        String json;

        if(tipoCliente.equalsIgnoreCase("android")) {
            json = "{\n" +
                    "  \"intent\": \"CAPTURE\",\n" +
                    "  \"purchase_units\": [\n" +
                    "    {\n" +
                    "      \"amount\": {\n" +
                    "        \"currency_code\": \"EUR\",\n" +
                    "        \"value\": \"" + s.getPrecio() + "\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"payment_source\": {\n" +
                    "    \"paypal\": {\n" +
                    "      \"experience_context\": {\n" +
                    "        \"payment_method_preference\": \"IMMEDIATE_PAYMENT_REQUIRED\",\n" +
                    "        \"brand_name\": \"GoTrave!\",\n" +
                    "        \"locale\": \"es-ES\",\n" +
                    "        \"landing_page\": \"NO_PREFERENCE\",\n" +
                    "        \"shipping_preference\": \"NO_SHIPPING\",\n" +
                    "        \"payment_method_preference\": \"IMMEDIATE_PAYMENT_REQUIRED\",\n" +
                    "        \"user_action\": \"PAY_NOW\",\n" +
                    "        \"return_url\": \"gotravel://checkout_returnurl\",\n" +
                    "        \"cancel_url\": \"gotravel://checkout_cancelurl\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        } else {
            json = "{\n" +
                    "  \"intent\": \"CAPTURE\",\n" +
                    "  \"purchase_units\": [\n" +
                    "    {\n" +
                    "      \"amount\": {\n" +
                    "        \"currency_code\": \"EUR\",\n" +
                    "        \"value\": \"" + s.getPrecio() + "\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ],\n" +
                    "  \"payment_source\": {\n" +
                    "    \"paypal\": {\n" +
                    "      \"experience_context\": {\n" +
                    "        \"payment_method_preference\": \"IMMEDIATE_PAYMENT_REQUIRED\",\n" +
                    "        \"brand_name\": \"GoTrave!\",\n" +
                    "        \"locale\": \"es-ES\",\n" +
                    "        \"landing_page\": \"NO_PREFERENCE\",\n" +
                    "        \"shipping_preference\": \"NO_SHIPPING\",\n" +
                    "        \"payment_method_preference\": \"IMMEDIATE_PAYMENT_REQUIRED\",\n" +
                    "        \"user_action\": \"PAY_NOW\",\n" +
                    "        \"return_url\": \"http://localhost:8080/checkout_returnurl\",\n" +
                    "        \"cancel_url\": \"http://localhost:8080/checkout_cancelurl\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
        }


        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Error: " + response.code());
                System.out.println("Response body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            } else {
                String responseBody = response.body().string();
                try {
                    JSONObject responseJson = new JSONObject(responseBody);

                    // Extrae el enlace de aprobación
                    JSONArray links = responseJson.getJSONArray("links");
                    for (int i = 0; i < links.length(); i++) {
                        JSONObject link = links.getJSONObject(i);
                        if (link.getString("rel").equals("payer-action")) {
                            String approvalUrl = link.getString("href");

                            return approvalUrl;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        return null;

    }

    public Contratacion capturarPedido(String contratacionId) throws IOException {

        String url =
                "https://api-m.sandbox.paypal.com/v2/checkout/orders/" + contratacionId + "/capture";

        OkHttpClient client = new OkHttpClient.Builder().build();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create("", null))
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Error: " + response.code());
                System.out.println("Response body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            } else {
                String responseBody = response.body().string();
                try {
                    JSONObject responseJson = new JSONObject(responseBody);

                    JSONObject purchaseUnit = responseJson.getJSONArray("purchase_units").getJSONObject(0);
                    JSONObject payment = purchaseUnit.getJSONObject("payments");
                    JSONObject capture = payment.getJSONArray("captures").getJSONObject(0);
                    JSONObject amount = capture.getJSONObject("amount");
                    String value = amount.getString("value");

                    String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    Pago p = new Pago(Double.parseDouble(value), hoy);
                    return new Contratacion(contratacionId, p, hoy);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        return null;

    }

}
