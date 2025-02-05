import java.util.List;

public class Destination {
    private String name;
    private String address;
    private String county;
    private String country;
    private List<String> tags;
    private double[] embedding;

    public Destination(String name, String address, String county, String country, List<String> tags) {
        this.name = name;
        this.address = address;
        this.county = county;
        this.country = country;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", county='" + county + '\'' +
                ", country='" + country + '\'' +
                ", tags=" + tags +
                '}';
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public double[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(double[] embedding) {
        this.embedding = embedding;
    }
}
