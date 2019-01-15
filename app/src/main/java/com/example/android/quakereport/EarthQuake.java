package com.example.android.quakereport;

public class EarthQuake {
    //Magnitute of the earthquake will be stored in mag
    private double mag;
    //Title of the earthquake will be stored here
    private String title;
    //Timestamp is returned in int value which will then be converted to day month year
    private long timestamp;
    //URL of the quake
    private String URL;

    public EarthQuake(double mag, String title, long timestamp, String URL) {
        this.mag = mag;
        this.title = title;
        this.timestamp = timestamp;
        this.URL = URL;
    }

    public double getMag() {return mag;}

    public String getTitle() {return title;}

    public long getTimestamp() {return timestamp;}

    public String getURL() {return URL;}

    @Override
    public String toString() {
        return "EarthQuake{" +
                "mag=" + mag +
                ", title='" + title + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
