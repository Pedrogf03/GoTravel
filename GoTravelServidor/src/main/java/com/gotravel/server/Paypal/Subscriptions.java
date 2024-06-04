package com.gotravel.server.Paypal;

import com.gotravel.server.model.Pago;
import com.gotravel.server.model.Suscripcion;
import com.gotravel.server.model.Usuario;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Subscriptions {

    private static final String PLAN_ID =
            "P-7EA59780H1130000JMZJTC5A";

    private String token;

    public Subscriptions() throws IOException {
        this.token = new TokenClient().obtenerTokenPaypal();
    }

    public String crearSuscripcion(Usuario u, String clientType) throws IOException {

        String url =
                "https://api-m.sandbox.paypal.com/v1/billing/subscriptions";

        OkHttpClient client = new OkHttpClient.Builder().build();

        MediaType mediaType = MediaType.parse("application/json");

        String json = "";

        if(clientType.equalsIgnoreCase("android")) {
            json = "{\n" +
                    "  \"plan_id\": \"" + PLAN_ID + "\",\n" +
                    "  \"quantity\": \"1\",\n" +
                    "  \"auto_renewal\": true,\n" +
                    "  \"subscriber\": {\n" +
                    "    \"name\": {\n" +
                    "      \"given_name\": \"" + u.getNombre() + "\",\n" +
                    "      \"surname\": \"" + (u.getApellidos() != null ? u.getApellidos() : "") + "\"\n" +
                    "    },\n" +
                    "    \"email_address\": \"" + u.getEmail() + "\"\n" +
                    "  },\n" +
                    "  \"application_context\": {\n" +
                    "    \"brand_name\": \"GoTravel!\",\n" +
                    "    \"locale\": \"es-ES\",\n" +
                    "    \"shipping_preference\": \"NO_SHIPPING\",\n" +
                    "    \"user_action\": \"SUBSCRIBE_NOW\",\n" +
                    "    \"payment_method\": {\n" +
                    "      \"payer_selected\": \"PAYPAL\",\n" +
                    "      \"payee_preferred\": \"IMMEDIATE_PAYMENT_REQUIRED\"\n" +
                    "    },\n" +
                    "    \"return_url\": \"gotravel://subscription_returnurl\",\n" +
                    "    \"cancel_url\": \"gotravel://subscription_cancelurl\"\n" +
                    "  }\n" +
                    "}";
        } else if(clientType.equalsIgnoreCase("desktop")) {
            json = "{\n" +
                    "  \"plan_id\": \"" + PLAN_ID + "\",\n" +
                    "  \"quantity\": \"1\",\n" +
                    "  \"auto_renewal\": true,\n" +
                    "  \"subscriber\": {\n" +
                    "    \"name\": {\n" +
                    "      \"given_name\": \"" + u.getNombre() + "\",\n" +
                    "      \"surname\": \"" + (u.getApellidos() != null ? u.getApellidos() : "") + "\"\n" +
                    "    },\n" +
                    "    \"email_address\": \"" + u.getEmail() + "\"\n" +
                    "  },\n" +
                    "  \"application_context\": {\n" +
                    "    \"brand_name\": \"GoTravel!\",\n" +
                    "    \"locale\": \"es-ES\",\n" +
                    "    \"shipping_preference\": \"NO_SHIPPING\",\n" +
                    "    \"user_action\": \"SUBSCRIBE_NOW\",\n" +
                    "    \"payment_method\": {\n" +
                    "      \"payer_selected\": \"PAYPAL\",\n" +
                    "      \"payee_preferred\": \"IMMEDIATE_PAYMENT_REQUIRED\"\n" +
                    "    },\n" +
                    "    \"return_url\": \"http://localhost:8080/subscription_returnurl\",\n" +
                    "    \"cancel_url\": \"http://localhost:8080/subscription_cancelurl\"\n" +
                    "  }\n" +
                    "}";
        }

        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Prefer", "return=representation")
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
                        if (link.getString("rel").equals("approve")) {
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

    public Suscripcion getSubscription(String subscriptionId) throws IOException {

        String url =
                "https://api-m.sandbox.paypal.com/v1/billing/subscriptions/" + subscriptionId;

        OkHttpClient client = new OkHttpClient.Builder().build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Error: " + response.code());
                System.out.println("Response body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            } else {

                String responseBody = response.body().string();

                JSONObject json = new JSONObject(responseBody);

                DateTimeFormatter formatoFromDb = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                String estado = json.getString("status");
                String fechaInicio = json.getString("start_time");
                JSONObject billingInfo = json.getJSONObject("billing_info");
                String fechaFinal = LocalDate.parse(fechaInicio.substring(0, 10), formatoFromDb).plusMonths(1).format(formatoFromDb);
                Double coste;
                try {
                    coste = Double.parseDouble(billingInfo.getJSONObject("last_payment").getJSONObject("amount").getString("value"));
                } catch (JSONException e) {
                    coste = 4.99;
                }

                List<Pago> pagos = new ArrayList<>();
                pagos.add(new Pago(coste, fechaInicio.substring(0, 10)));
                return new Suscripcion(subscriptionId, pagos, "1", estado, fechaInicio.substring(0, 10), fechaFinal);

            }
        }

    }

    public boolean cancelSubscription(String subscriptionId) throws IOException {

        String url =
                "https://api-m.sandbox.paypal.com/v1/billing/subscriptions/" + subscriptionId + "/suspend";

        OkHttpClient client = new OkHttpClient.Builder().build();

        MediaType mediaType = MediaType.parse("application/json");

        String json = "{ \"reason\": \" Cancelar suscripcion \" }";

        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Prefer", "return=representation")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Error: " + response.code());
                System.out.println("Response body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            } else {

                return true;

            }
        }

    }

    public boolean activateSubscription(String subscriptionId) throws IOException {

        String url =
                "https://api-m.sandbox.paypal.com/v1/billing/subscriptions/" + subscriptionId + "/activate";

        OkHttpClient client = new OkHttpClient.Builder().build();

        MediaType mediaType = MediaType.parse("application/json");

        String json = "{ \"reason\": \" Renovar suscripcion \" }";

        RequestBody body = RequestBody.create(json, mediaType);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Prefer", "return=representation")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("Error: " + response.code());
                System.out.println("Response body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            } else {

                return true;

            }
        }

    }

}
