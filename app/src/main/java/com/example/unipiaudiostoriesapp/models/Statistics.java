package com.example.unipiaudiostoriesapp.models;

import com.google.firebase.firestore.DocumentId;

public class Statistics {
    @DocumentId
    private String storyId; // Story Id same as one in Firestore
    private int numReads; // Story read counter

    // Empty constructor (necessary for Firestore)
    public Statistics() { }

    public Statistics(String storyId, int numReads) {
        this.storyId = storyId;
        this.numReads = numReads;
    }

    // Getters & Setters
    public String getStoryId() {
        return storyId;
    }

    public int getNumReads() {
        return numReads;
    }

    public void setNumReads(int numReads) {
        this.numReads = numReads;
    }
}
