import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DestinationRecommender {
    private List<Destination> destinations = new ArrayList<>();

    public void loadDestinations(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Read header line (if any)
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split("\",\"");
                if (values.length < 8) continue;

                String name = values[0].replace("\"", "");
                String address = values[5].replace("\"", "");
                String county = values[6].replace("\"", "");
                String country = values[3].replace("\"", "");
                List<String> tags = Arrays.asList(values[7].replace("\"", "").split(","));

                Destination dest = new Destination(name, address, county, country, tags);
                try {
                    // Generate the embedding vector by combining the tags, a season, and a budget.
                    // (Here "Summer" and "Medium" are hard-coded; you may wish to adjust these.)
                    dest.setEmbedding(EmbeddingGenerator.generateEmbedding(tags, "Summer", "Medium"));
                } catch (Exception e) {
                    System.err.println("Error generating embedding for destination " + name + ": " + e.getMessage());
                    e.printStackTrace();
                }
                destinations.add(dest);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Destination> findSimilarDestinations(double[] userEmbedding, int topN) {
        if (destinations.isEmpty()) {
            System.out.println("No destinations loaded!");
            return new ArrayList<>();
        }

        destinations.sort(Comparator.comparingDouble(d ->
                -EmbeddingGenerator.calculateSimilarity(userEmbedding, d.getEmbedding())));

        return new ArrayList<>(destinations.subList(0, Math.min(topN, destinations.size())));
    }
}

