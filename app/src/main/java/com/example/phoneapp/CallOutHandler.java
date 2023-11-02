package com.example.phoneapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;

public class CallOutHandler {

    String responseData, errorData;
    int responseCode;
    ResponseWrapper rw;

    public ResponseWrapper doCallOut(){
        try {
            // Define the URL of the API you want to call
            String apiUrl = "https://jsonplaceholder.typicode.com/todos/1";

            // Create a URL object with the API URL
            URL url = new URL(apiUrl);

            // Open a connection to the URL
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set the request method (GET, POST, etc.)
            connection.setRequestMethod("GET");

            // Set request headers if needed (e.g., authorization)
            // connection.setRequestProperty("Authorization", "Bearer your_api_key");

            // Get the response code
            responseCode = connection.getResponseCode();

            // Read the response from the API
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Process the response data (response.toString())
                responseData = response.toString();

                System.out.println("API Response: " + responseData);

                // Close the connection
                connection.disconnect();
            } else {
                // Handle API error (e.g., by reading error response)
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String errorLine;
                StringBuilder errorResponse = new StringBuilder();

                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                errorReader.close();

                // Process the error response data (errorResponse.toString())
                errorData = errorResponse.toString();
                System.out.println("API Error Response: " + errorData);

                // Close the connection
                connection.disconnect();
            }
            if(responseCode == HttpsURLConnection.HTTP_OK){
                rw = new ResponseWrapper(responseCode, responseData);
            }
            else {
                rw = new ResponseWrapper(responseCode, errorData);
            }
        } catch (Exception e) {
            rw = new ResponseWrapper(responseCode, Arrays.toString(e.getStackTrace()));
        }
        finally{
            return rw;
        }
    }

    class ResponseWrapper{
        int status;
        String message;
        ResponseWrapper(int status, String message){
            this.status = status;
            this.message = message;
        }
    }
}

