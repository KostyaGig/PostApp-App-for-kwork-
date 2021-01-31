package ru.kostya.postforkowrk.models;

public class Post {

    private String title;
    private String text;
    private String postImageUrl;
    private String publisherImageUrl;
    private String publisherName;

    public Post() {
    }

    public Post(String title, String text, String postImageUrl, String publisherImageUrl, String publisherName) {
        this.title = title;
        this.text = text;
        this.postImageUrl = postImageUrl;
        this.publisherImageUrl = publisherImageUrl;
        this.publisherName = publisherName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPublisherImageUrl() {
        return publisherImageUrl;
    }

    public void setPublisherImageUrl(String publisherImageUrl) {
        this.publisherImageUrl = publisherImageUrl;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }


}
