import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LlamaRecommender {
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static List<String> getFinalRecommendations(List<Destination> topDestinations, List<String> userPreferences) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter the number of days for the travel itinerary: ");
        int n = scanner.nextInt();
        scanner.nextLine();

        System.out.print("In what language would you like the recommendations? (e.g., English, Spanish, French): ");
        String language = scanner.nextLine().trim();

        List<Destination> limitedDestinations = new ArrayList<>(topDestinations);

        while (!limitedDestinations.isEmpty()) {
            try {
                String prompt = "Here are the destinations with details (including country, state, county, and address):\n" +
                        limitedDestinations.stream()
                                .map(dest -> "Name: " + dest.getName() +
                                        ", Address: " + dest.getAddress() +
                                        ", County: " + dest.getCounty() +
                                        ", Country: " + (dest.getCountry() != null ? dest.getCountry() : "Unknown") +
                                        ", Tags: " + dest.getTags())
                                .collect(Collectors.joining("\n")) +
                        "\nBased on these options and the user's preferences: " + userPreferences +
                        "\nPlease select ONLY ONE destination that best matches the preferences, provide a brief explanation, " +
                        "and also recommend a travel itinerary for " + n + " days including other destinations from the same state with similar characteristics." +
                        "\nAdditionally, provide a **detailed list of hotels, guesthouses, and Airbnb options** in or near the selected destination, with names and a short description." +
                        "\nRespond in " + language + ".";

                String response = fetchLlamaResponse(prompt);
                System.out.println("\n🔹 Raw Llama Response: " + response);
                String cleanedResponse = cleanResponse(response);

                System.out.println("\n🔥 Llama Suggested Destination and Stay:\n" + cleanedResponse);

                System.out.print("Do you like this destination? (yes/no): ");
                String userResponse = scanner.nextLine().trim().toLowerCase();

                if (userResponse.equals("yes")) {
                    return List.of(cleanedResponse);
                } else {
                    System.out.println("Removing the first suggested destination and trying again...");
                    if (!limitedDestinations.isEmpty()) {
                        limitedDestinations.remove(0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return List.of("Error getting recommendations from Llama.");
            }
        }
        return List.of("No more destinations available!");
    }

    public static String fetchLlamaResponse(String prompt) {
        if (cache.containsKey(prompt)) {
            return cache.get(prompt);
        }

        try {
            URL url = new URL("http://127.0.0.1:11434/api/generate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{ \"model\": \"llama3.2\", \"prompt\": \"" +
                    prompt.replace("\"", "\\\"").replace("\n", "\\n") +
                    "\", \"stream\": false }";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            String result = response.toString();
            cache.put(prompt, result);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error fetching response!";
        }
    }

    private static String cleanResponse(String response) {
        if (response.contains("\"response\":")) {
            int start = response.indexOf("\"response\":") + 11;
            int end = response.indexOf(",\"done\":", start);
            if (end == -1) { // If the end marker isn't found, use the last closing brace.
                end = response.lastIndexOf("}");
            }
            if (end > start) {
                return response.substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
            } else {
                return response.substring(start)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
            }
        }
        return response;
    }

}

