package com.group5.quacker.entities;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Quack {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Lob
    private String quackMessage;

    private int likes;

    private Date datePosted;

    private String formattedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User poster;

    public int addLike() {
        return likes++;
    }

    public String getQuackMessage() {
        return quackMessage;
    }

    public void setQuackMessage(String quackMessage) {
        this.quackMessage = quackMessage;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public User getPoster() {
        return poster;
    }

    public void setPoster(User poster) {
        this.poster = poster;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
