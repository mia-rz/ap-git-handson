import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmbeddingGenerator {
    private static final String API_URL = "http://localhost:11434/api/embeddings";
    private static final Map<String, double[]> embeddingCache = new HashMap<>();

    public static double[] generateEmbedding(List<String> activities, String season, String budget) throws Exception {
        String prompt = String.join(" ", activities) + " " + season + " " + budget;

        if (embeddingCache.containsKey(prompt)) {
            return embeddingCache.get(prompt);
        }
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("model", "nomic-embed-text");
        jsonPayload.put("prompt", prompt);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonResponse = new JSONObject(response.body());
        JSONArray embeddingArray = jsonResponse.getJSONArray("embedding");
        double[] embedding = new double[embeddingArray.length()];
        for (int i = 0; i < embeddingArray.length(); i++) {
            embedding[i] = embeddingArray.getDouble(i);
        }
        embeddingCache.put(prompt, embedding);
        return embedding;
    }
    public static double calculateSimilarity(double[] emb1, double[] emb2) {
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < emb1.length; i++) {
            dotProduct += emb1[i] * emb2[i];
            normA += emb1[i] * emb1[i];
            normB += emb2[i] * emb2[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-9);
    }
}

