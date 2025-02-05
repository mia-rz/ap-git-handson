import java.util.*;
import java.util.Arrays;

public class RecommenderSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Recommender System!");

        System.out.println("Please select activity types from the following options (enter numbers separated by commas):");
        System.out.println("1. Food and Drink\n2. Experience\n3. Walking\n4. Tour\n5. Shopping\n6. Golf\n7. Nature and Wildlife");
        System.out.print("Enter your choices: ");
        String input = scanner.nextLine();
        String[] choices = input.split(",");
        List<String> activityTypes = new ArrayList<>();

        Map<String, String> activityMapping = Map.of(
                "1", "Food and Drink",
                "2", "Experience",
                "3", "Walking",
                "4", "Tour",
                "5", "Shopping",
                "6", "Golf",
                "7", "Nature and Wildlife"
        );

        for (String choice : choices) {
            if (activityMapping.containsKey(choice.trim())) {
                activityTypes.add(activityMapping.get(choice.trim()));
            } else {
                System.out.println("Invalid choice: " + choice.trim());
            }
        }

        System.out.print("Which season are you planning for? (Spring, Summer, Autumn, Winter): ");
        String season = scanner.nextLine();

        System.out.print("What is your budget range? (Low, Medium, High): ");
        String budget = scanner.nextLine();

        System.out.println("\nBased on your preferences:");
        System.out.println("Activity Types: " + String.join(", ", activityTypes));
        System.out.println("Season: " + season);
        System.out.println("Budget: " + budget);

        double[] userEmbedding = null;
        try {
            userEmbedding = EmbeddingGenerator.generateEmbedding(activityTypes, season, budget);
        } catch (Exception e) {
            System.err.println("Error generating user embedding: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Generated Embedding: " + Arrays.toString(userEmbedding));

        DestinationRecommender recommender = new DestinationRecommender();
        recommender.loadDestinations("C:\\Users\\Lenovo\\Desktop\\project\\destinations.csv");

        List<Destination> recommendations = recommender.findSimilarDestinations(userEmbedding, 5);

        System.out.println("\nTop 5 similar destinations:");
        for (Destination dest : recommendations) {
            System.out.println(dest);
        }

        List<String> finalRecommendations = LlamaRecommender.getFinalRecommendations(recommendations, activityTypes);

        System.out.println("\n🔥 Llama Final Recommendations:");
        for (String rec : finalRecommendations) {
            System.out.println("- " + rec);
        }
        System.out.print("\nDo you want to check the destination's weather? (yes/no): ");
        if (scanner.hasNextLine()) {
            String weatherCheck = scanner.nextLine().trim().toLowerCase();
            if (weatherCheck.equals("yes")) {
                Weather.checkWeather();
            }
        }

        TravelTimeCalculator.askAndCalculateTravelTime();
    }
}


