package com.example.bbcnewsreader.data;

import java.io.Serializable;

/**
 * Model class representing a BBC news article.
 * Implements Serializable to allow passing between activities.
 */
public class NewsArticle implements Serializable {
    private long id;
    private String title;
    private String description;
    private String link;
    private String pubDate;

    /**
     * Default constructor
     */
    public NewsArticle() {
    }

    /**
     * Constructor with all fields except id
     * @param title Article title
     * @param description Article description
     * @param link Article URL
     * @param pubDate Publication date
     */
    public NewsArticle(String title, String description, String link, String pubDate) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return title;
    }
}
