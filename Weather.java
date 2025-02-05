import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Weather {
    private static final String API_KEY = "7e68c29de1b5e7d7b3c2edbda0f3607a";
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast?units=metric&";

    public static void checkWeather() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Please enter the city name: ");
            String city = scanner.nextLine();

            String weatherData = getWeatherData(WEATHER_URL + "appid=" + API_KEY + "&q=" + city);

            if (weatherData != null && weatherData.contains("\"cod\":\"200\"")) {
                System.out.println("Weather forecast for " + city + ":");

                String[] days = extractForecastDays(weatherData);

                for (int i = 0; i < days.length; i++) {
                    System.out.println((i + 1) + ". " + days[i]);
                }

                System.out.print("Please select a day (1-" + days.length + "): ");
                int dayIndex = scanner.nextInt() - 1;
                scanner.nextLine();

                if (dayIndex >= 0 && dayIndex < days.length) {
                    String[] times = extractTimesForDay(weatherData, days[dayIndex]);

                    System.out.println("Available times for " + days[dayIndex] + ":");
                    for (int i = 0; i < times.length; i++) {
                        System.out.println((i + 1) + ". " + times[i]);
                    }

                    System.out.print("Please select a time (1-" + times.length + "): ");
                    int timeIndex = scanner.nextInt() - 1;
                    scanner.nextLine();

                    if (timeIndex >= 0 && timeIndex < times.length) {
                        String weatherInfo = getWeatherDataForTime(weatherData, days[dayIndex], times[timeIndex]);
                        System.out.println("Weather at " + times[timeIndex] + ": " + weatherInfo);
                    } else {
                        System.out.println("Invalid time selected.");
                    }
                } else {
                    System.out.println("Invalid day selected.");
                }
            } else {
                System.out.println("Error retrieving weather data :(");
            }

            System.out.print("Do you want to check another city? (yes/no): ");
            String anotherCity = scanner.nextLine().trim().toLowerCase();
            if (!anotherCity.equals("yes")) {
                break;
            }
        }
    }

    private static String getWeatherData(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (Exception e) {
            System.out.println("Error fetching data: " + e.getMessage());
            return null;
        }
    }

    private static String[] extractForecastDays(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray list = jsonObject.getJSONArray("list");
        Set<String> uniqueDays = new HashSet<>();

        for (int i = 0; i < list.length(); i++) {
            String dt_txt = list.getJSONObject(i).getString("dt_txt");
            String date = dt_txt.split(" ")[0];
            uniqueDays.add(date);
            if (uniqueDays.size() >= 5) break;
        }

        return uniqueDays.toArray(new String[0]);
    }

    private static String[] extractTimesForDay(String json, String day) {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray list = jsonObject.getJSONArray("list");
        Set<String> times = new HashSet<>();

        for (int i = 0; i < list.length(); i++) {
            String dt_txt = list.getJSONObject(i).getString("dt_txt");
            if (dt_txt.startsWith(day)) {
                String time = dt_txt.split(" ")[1];
                times.add(time);
            }
        }

        return times.toArray(new String[0]);
    }

    private static String getWeatherDataForTime(String json, String day, String time) {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray list = jsonObject.getJSONArray("list");

        for (int i = 0; i < list.length(); i++) {
            JSONObject weatherEntry = list.getJSONObject(i);
            String dt_txt = weatherEntry.getString("dt_txt");

            if (dt_txt.equals(day + " " + time)) {
                JSONArray weatherArray = weatherEntry.getJSONArray("weather");
                String description = weatherArray.getJSONObject(0).getString("description");
                double temp = weatherEntry.getJSONObject("main").getDouble("temp");
                int humidity = weatherEntry.getJSONObject("main").getInt("humidity");
                double windSpeed = weatherEntry.getJSONObject("wind").getDouble("speed");
                int pressure = weatherEntry.getJSONObject("main").getInt("pressure");

                return description + ", Temperature: " + temp + "°C, Humidity: " + humidity + "%" +
                        ", Wind Speed: " + windSpeed + " m/s, Pressure: " + pressure + " hPa";
            }
        }
        return "Weather data not available";
    }

    public static void main(String[] args) {
        checkWeather();
    }
}


