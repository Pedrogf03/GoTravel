package com.gotravel.mobile.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.gotravel.mobile.data.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Callback
import okhttp3.Request
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.math.BigInteger
import java.net.Socket
import java.security.MessageDigest
import java.time.format.DateTimeFormatter

// Valores por defecto que se cambian al iniciar sesion
object AppUiState {
    var segundoPlano: Boolean = false
    var socket: Socket? = null
    lateinit var usuario: Usuario
    lateinit var entrada: DataInputStream
    lateinit var salida: DataOutputStream
}

object Regex {
    val regexEmail = "^(?=.{1,150}\$)[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    val regexContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\\s]).{8,}$".toRegex()
    val regexNombre = "^(?=.{1,45}\$)[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ]*$".toRegex()
    val regexCamposGrandes = "^(?=.{1,200}\$)[a-zA-Z0-9 áéíóúÁÉÍÓÚüÜñÑ]*$".toRegex()
    val regexTfno = "^\\d{9}$".toRegex()
    val regexTitular = "^[\\p{L} ]+\$".toRegex()
    val regexNumero = "^\\d{16}\$".toRegex()
    val regexCvv = "^\\d{3}\$".toRegex()
    val regexDireccion = Regex("^[\\p{L}0-9 .,\\\\/'º-]+$")
    val regexCiudadEstado = Regex("^[\\p{L} ]+$")
    val regexCP = Regex("^\\d{5}$")
}

fun String.sha256(): String {
    val md = MessageDigest.getInstance("SHA-256")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

@RequiresApi(Build.VERSION_CODES.O)
val formatoFromDb = DateTimeFormatter.ofPattern("yyyy-MM-dd")
@RequiresApi(Build.VERSION_CODES.O)
val formatoFinal: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

val countryCodes = mapOf(
    "Afganistán" to "AF",
    "Albania" to "AL",
    "Argelia" to "DZ",
    "Andorra" to "AD",
    "Angola" to "AO",
    "Argentina" to "AR",
    "Armenia" to "AM",
    "Australia" to "AU",
    "Austria" to "AT",
    "Azerbaiyán" to "AZ",
    "Bahamas" to "BS",
    "Baréin" to "BH",
    "Bangladés" to "BD",
    "Barbados" to "BB",
    "Bielorrusia" to "BY",
    "Bélgica" to "BE",
    "Belice" to "BZ",
    "Benín" to "BJ",
    "Bután" to "BT",
    "Bolivia" to "BO",
    "Bosnia y Herzegovina" to "BA",
    "Botsuana" to "BW",
    "Brasil" to "BR",
    "Brunéi" to "BN",
    "Bulgaria" to "BG",
    "Burkina Faso" to "BF",
    "Burundi" to "BI",
    "Costa de Marfil" to "CI",
    "Cabo Verde" to "CV",
    "Camboya" to "KH",
    "Camerún" to "CM",
    "Canadá" to "CA",
    "Chad" to "TD",
    "Chile" to "CL",
    "China" to "CN",
    "Colombia" to "CO",
    "Comoras" to "KM",
    "Congo (Brazzaville)" to "CG",
    "Congo (Kinshasa)" to "CD",
    "Costa Rica" to "CR",
    "Croacia" to "HR",
    "Cuba" to "CU",
    "Chipre" to "CY",
    "República Checa" to "CZ",
    "Dinamarca" to "DK",
    "Yibuti" to "DJ",
    "Dominica" to "DM",
    "República Dominicana" to "DO",
    "Ecuador" to "EC",
    "Egipto" to "EG",
    "El Salvador" to "SV",
    "Guinea Ecuatorial" to "GQ",
    "Eritrea" to "ER",
    "Estonia" to "EE",
    "Etiopía" to "ET",
    "Fiji" to "FJ",
    "Finlandia" to "FI",
    "Francia" to "FR",
    "Gabón" to "GA",
    "Gambia" to "GM",
    "Georgia" to "GE",
    "Alemania" to "DE",
    "Ghana" to "GH",
    "Grecia" to "GR",
    "Granada" to "GD",
    "Guatemala" to "GT",
    "Guinea" to "GN",
    "Guinea-Bisáu" to "GW",
    "Guyana" to "GY",
    "Haití" to "HT",
    "Honduras" to "HN",
    "Hungría" to "HU",
    "Islandia" to "IS",
    "India" to "IN",
    "Indonesia" to "ID",
    "Irán" to "IR",
    "Irak" to "IQ",
    "Irlanda" to "IE",
    "Israel" to "IL",
    "Italia" to "IT",
    "Jamaica" to "JM",
    "Japón" to "JP",
    "Jordania" to "JO",
    "Kazajistán" to "KZ",
    "Kenia" to "KE",
    "Kiribati" to "KI",
    "Corea del Norte" to "KP",
    "Corea del Sur" to "KR",
    "Kuwait" to "KW",
    "Kirguistán" to "KG",
    "Laos" to "LA",
    "Letonia" to "LV",
    "Líbano" to "LB",
    "Lesoto" to "LS",
    "Liberia" to "LR",
    "Libia" to "LY",
    "Liechtenstein" to "LI",
    "Lituania" to "LT",
    "Luxemburgo" to "LU",
    "Madagascar" to "MG",
    "Malaui" to "MW",
    "Malasia" to "MY",
    "Maldivas" to "MV",
    "Malí" to "ML",
    "Malta" to "MT",
    "Islas Marshall" to "MH",
    "Mauritania" to "MR",
    "Mauricio" to "MU",
    "México" to "MX",
    "Micronesia" to "FM",
    "Moldavia" to "MD",
    "Mónaco" to "MC",
    "Mongolia" to "MN",
    "Montenegro" to "ME",
    "Marruecos" to "MA",
    "Mozambique" to "MZ",
    "Birmania" to "MM",
    "Namibia" to "NA",
    "Nauru" to "NR",
    "Nepal" to "NP",
    "Países Bajos" to "NL",
    "Nueva Zelanda" to "NZ",
    "Nicaragua" to "NI",
    "Níger" to "NE",
    "Nigeria" to "NG",
    "Noruega" to "NO",
    "Omán" to "OM",
    "Pakistán" to "PK",
    "Palaos" to "PW",
    "Panamá" to "PA",
    "Papúa Nueva Guinea" to "PG",
    "Paraguay" to "PY",
    "Perú" to "PE",
    "Filipinas" to "PH",
    "Polonia" to "PL",
    "Portugal" to "PT",
    "Catar" to "QA",
    "Rumanía" to "RO",
    "Rusia" to "RU",
    "Ruanda" to "RW",
    "San Cristóbal y Nieves" to "KN",
    "Santa Lucía" to "LC",
    "San Vicente y las Granadinas" to "VC",
    "Samoa" to "WS",
    "San Marino" to "SM",
    "Santo Tomé y Príncipe" to "ST",
    "Arabia Saudí" to "SA",
    "Senegal" to "SN",
    "Serbia" to "RS",
    "Seychelles" to "SC",
    "Sierra Leona" to "SL",
    "Singapur" to "SG",
    "Eslovaquia" to "SK",
    "Eslovenia" to "SI",
    "Islas Salomón" to "SB",
    "Somalia" to "SO",
    "Sudáfrica" to "ZA",
    "Sudán del Sur" to "SS",
    "España" to "ES",
    "Sri Lanka" to "LK",
    "Sudán" to "SD",
    "Surinam" to "SR",
    "Suecia" to "SE",
    "Suiza" to "CH",
    "Siria" to "SY",
    "Tayikistán" to "TJ",
    "Tanzania" to "TZ",
    "Tailandia" to "TH",
    "Timor Oriental" to "TL",
    "Togo" to "TG",
    "Tonga" to "TO",
    "Trinidad y Tobago" to "TT",
    "Túnez" to "TN",
    "Turquía" to "TR",
    "Turkmenistán" to "TM",
    "Tuvalu" to "TV",
    "Uganda" to "UG",
    "Ucrania" to "UA",
    "Emiratos Árabes Unidos" to "AE",
    "Reino Unido" to "GB",
    "Estados Unidos" to "US",
    "Uruguay" to "UY",
    "Uzbekistán" to "UZ",
    "Vanuatu" to "VU",
    "Venezuela" to "VE",
    "Vietnam" to "VN",
    "Yemen" to "YE",
    "Zambia" to "ZM",
    "Zimbabue" to "ZW"
)

fun obtenerCodigoPais(nombrePais: String): String? {
    return countryCodes[nombrePais]
}

suspend fun addRolProfesional() {

    return withContext(Dispatchers.IO) {
        val gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()

        try {

            AppUiState.salida.writeUTF("suscribirse")
            AppUiState.salida.flush()

            val jsonFromServer = AppUiState.entrada.readUTF()
            val usuario : Usuario? = gson.fromJson(jsonFromServer, Usuario::class.java)

            if(usuario != null) {
                AppUiState.usuario = usuario.copy(foto = AppUiState.usuario.foto)
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}





