package com.abhishek.tracklist;

public class Track {
    private String trackId;
    private String trackName;
    private float trackRating;

    public Track(String trackId, String trackName, float trackRating) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackRating = trackRating;
    }

    public Track(){}

    public String getTrackId() {
        return trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public float getTrackRating() {
        return trackRating;
    }
}
