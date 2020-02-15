package com.group5.quacker.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    List<User> likers;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_map_id")
    private FileMap attachment;

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

    public List<User> getLikers() { return likers; }

    public void addLiker(User user) { likers.add(user); }

    public void removeLiker(User user) { likers.remove(user); }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FileMap getAttachment() {
        return attachment;
    }

    public void setAttachment(FileMap attachment) {
        this.attachment = attachment;
    }

    /**
     * Testaamista varten
     * @param likers
     */
    public void setLikers(List<User> likers) {
        this.likers = likers;
    }
}
