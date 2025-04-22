package com.example.myapplication1;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewsItem {
    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("published")
    private String publishedDate;

    @SerializedName("link")
    private String link;

    @SerializedName("source")
    private String source;

    @SerializedName("symbols")
    private List<String> symbols;

    // Removed sentiment field

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPublishedDate() { return publishedDate; }
    public String getLink() { return link; }
    public String getSource() { return source; }
    public List<String> getSymbols() { return symbols; }
    // Removed sentiment getter
}
