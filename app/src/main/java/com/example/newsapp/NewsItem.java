package com.example.newsapp;

public class NewsItem {

    /**
     * Member Variables
     */
    private String title;
    private String section;
    private String author;
    private String pubDate;
    private String webUrl;

    /**
     * Constructor
     */
    public NewsItem(String title, String section, String author, String pubDate, String webUrl) {
        this.title = title;
        this.section = section;
        this.author = author;
        this.pubDate = pubDate;
        this.webUrl = webUrl;
    }

    /**
     * Getters and Setters of the respective data
     */
    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getAuthor() {
        return author;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getWebUrl() {
        return webUrl;
    }

    /**
     * Overridden method to display the data
     */
    @Override
    public String toString() {
        return "NewsItem{" +
                "title='" + title + '\'' +
                ", section='" + section + '\'' +
                ", author='" + author + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", webUrl='" + webUrl + '\'' +
                '}';
    }
}
