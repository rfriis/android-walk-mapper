package uk.ac.abertay.cmp309.WalkMapper;

public class Walk {
    private int walkID;
    private String walkName;
    private float rating;
    private double distance;
    private String coordinates;
    private String date;

    public Walk(int walkID, String walkName, float rating, double distance, String coordinates, String date) {
        this.walkID = walkID;
        this.walkName = walkName;
        this.rating = rating;
        this.distance = distance;
        this.coordinates = coordinates;
        this.date = date;
    }

    public int getWalkID() { return walkID; }

    public void setWalkID(int walkID) { this.walkID = walkID; }

    public String getWalkName() {
        return walkName;
    }

    public void setWalkName(String walkName) {
        this.walkName = walkName;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCoordinates() { return coordinates; }

    public void setCoordinates(String coordinates) { this.coordinates = coordinates; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }
}
