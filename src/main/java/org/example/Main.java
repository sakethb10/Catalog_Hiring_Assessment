package org.example;

import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {
        // Load the JSON file from resources folder
        // Use filename of relevant test case in resources folder
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("TestCase2" +
                ".json");

        if (inputStream == null) {
            System.out.println("File Not Found");
            return;
        }

        // Read the JSON input from the file
        try {
            String jsonData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject input = new JSONObject(jsonData);

            // Process the input
            processInput(input);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the InputStream
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Function to process the JSON input
    private static void processInput(JSONObject input) {
        JSONObject keys = input.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        System.out.println("n: " + n + ", k: " + k);

        // Map to store the (x, y) points for interpolation
        Map<Integer, BigInteger> points = new HashMap<>();

        // Decode the values and store them in the map
        for (String key : input.keySet()) {
            if (key.equals("keys")) {
                continue;
            }

            JSONObject entry = input.getJSONObject(key);
            String baseStr = entry.getString("base");
            String valueStr = entry.getString("value");

            // Decode the value using BigInteger
            int base = Integer.parseInt(baseStr);
            BigInteger decodedValue = new BigInteger(valueStr, base);

            // Use the key as the x value and the decoded value as the y value
            points.put(Integer.parseInt(key), decodedValue);
        }

        // Calculate the secret (c) using Lagrange interpolation
        BigInteger secret = lagrangeInterpolation(points, BigInteger.ZERO);
        System.out.println("Secret (c): " + secret);
    }

    // Function to perform Lagrange interpolation using BigInteger
    private static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, BigInteger x) {
        BigInteger result = BigInteger.ZERO;

        for (Map.Entry<Integer, BigInteger> entry : points.entrySet()) {
            int xi = entry.getKey();
            BigInteger yi = entry.getValue();

            // Calculate the Lagrange basis polynomial for this point
            BigInteger term = yi; // Change to BigInteger to handle division
            for (Integer xj : points.keySet()) {
                if (xi != xj) {
                    term = term.multiply(x.subtract(BigInteger.valueOf(xj)))
                            .divide(BigInteger.valueOf(xi - xj));
                }
            }

            result = result.add(term);
        }

        return result;
    }
}
