package com.example.unipiaudiostoriesapp.models;

import com.google.firebase.firestore.DocumentId;

public class Story {
    @DocumentId  //Firestore stores doc ID
    private String id;
    private String title;
    private String author;
    private String year;
    private String content;
    private String imageName; // Image file name (will add ".jpg")
    private String language;

    // Required from Firestore to read data
    public Story() { }

    public Story(String id, String title, String author, String year, String content, String imageName, String language) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.content = content;
        this.imageName = imageName;
        this.language = language;
    }

    // Getters
    public String getId() { return id; }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }

    public String getYear() { return year; }

    public String getContent() { return content; }

    public String getImageName() { return imageName; }

    public String getLanguage() { return language; }
}
