package com.gotravel.server.servidor;

import com.google.gson.*;
import com.gotravel.server.model.Metodopago;
import com.gotravel.server.model.Paypal;
import com.gotravel.server.model.Tarjetacredito;

import java.lang.reflect.Type;

public class MetodopagoAdapter implements JsonDeserializer<Metodopago> {
    @Override
    public Metodopago deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Determina el tipo de objeto Metodopago por el número de atributos.
        switch (jsonObject.entrySet().size()) {
            // Asume que Paypal tiene 2 atributos y Tarjetacredito tiene 5.
            case 2:
                return context.deserialize(json, Paypal.class);
            case 8:
                return context.deserialize(json, Tarjetacredito.class);
            default:
                throw new JsonParseException("No se pudo deserializar el objeto Metodopago: número de atributos desconocido");
        }
    }
}

