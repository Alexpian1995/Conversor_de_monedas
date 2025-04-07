package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese la moneda de origen (por ejemplo, USD): ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Ingrese la moneda de destino (por ejemplo, EUR): ");
        String targetCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Ingrese la cantidad a convertir: ");
        double amount = scanner.nextDouble();

        double result = convertCurrency(baseCurrency, targetCurrency, amount);
        if (result != -1) {
            System.out.printf("%.2f %s = %.2f %s\n", amount, baseCurrency, result, targetCurrency);
        } else {
            System.out.println("No se pudo realizar la conversión. Verifica los datos ingresados o tu conexión.");
        }

        scanner.close();
    }

    public static double convertCurrency(String from, String to, double amount) {
        try {
            String apiUrl = String.format(
                    "https://api.exchangerate.host/convert?from=%s&to=%s&amount=%.2f",
                    from, to, amount
            );

            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder responseContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(responseContent.toString());

            // Validar si la respuesta fue exitosa
            if (jsonResponse.has("success") && jsonResponse.getBoolean("success")) {
                if (jsonResponse.has("result") && !jsonResponse.isNull("result")) {
                    return jsonResponse.getDouble("result");
                } else {
                    System.out.println("La API no devolvió un resultado válido.");
                }
            } else {
                System.out.println("Error de la API: " + jsonResponse.optJSONObject("error"));
            }

        } catch (Exception e) {
            System.out.println("Excepción al convertir monedas: " + e.getMessage());
        }
        return -1;
    }
}
