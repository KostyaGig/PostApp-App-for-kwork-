package ru.kostya.postforkowrk.models;

public class Comment {

    private String commentText;
    private String uId;
    private String userName;
    private String userProfileImageUrl;

    public Comment(){

    }

    public Comment(String commentText, String uId, String userName,String userProfileImageUrl) {
        this.commentText = commentText;
        this.uId = uId;
        this.userName = userName;
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }
}
