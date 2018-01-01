package me.harshithgoka.socmed.Storage;

public class Comment {
    public String uid;
    public String name;
    public int commentid;
    public String text;
    public String timestamp;

    public Comment(String uid, String name, int commentid, String text, String timestamp) {
        this.uid = uid;
        this.name = name;
        this.commentid = commentid;
        this.text = text;
        this.timestamp = timestamp;
    }
}
