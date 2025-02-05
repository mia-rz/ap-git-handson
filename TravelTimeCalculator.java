import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class TravelTimeCalculator {
    private static final String API_KEY = "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";
    private static final String API_URL = "https://api.tavily.com/search";

    public static void askAndCalculateTravelTime() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Do you want to know the travel time between two cities? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("yes")) {
            System.out.print("Enter departure city: ");
            String origin = scanner.nextLine();
            System.out.print("Enter destination city: ");
            String destination = scanner.nextLine();
            calculateTravelTime(origin, destination);
        }
    }

    public static void calculateTravelTime(String origin, String destination) {
        try {
            String query = "Travel time between " + origin + " and " + destination + " by train, car, and airplane";
            String requestBody = "{\"query\": \"" + query + "\", \"api_key\": \"" + API_KEY + "\"}";

            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                br.close();
                parseResponse(response.toString());
            } else {
                System.out.println("Error: " + responseCode);
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseResponse(String response) {
        if (response.contains("results")) {
            System.out.println("\nTravel Time Estimates:");
            String[] results = response.split("results");
            for (String result : results) {
                if (result.contains("title") && result.contains("url")) {
                    String title = result.substring(result.indexOf("title") + 7, result.indexOf(",", result.indexOf("title")));
                    String url = result.substring(result.indexOf("url") + 5, result.indexOf(",", result.indexOf("url")));
                    System.out.println("- " + title + ": " + url);
                }
            }
        } else {
            System.out.println("No results found.");
        }
    }
}

