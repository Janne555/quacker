package com.group5.quacker.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Entity for quacks
 */
@Entity
public class Quack {

    /**
     * Unique ID for individual quacks
     */
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /**
     * This is the data lob for the message data
     */
    @Lob
    private String quackMessage;

    @JsonIgnore
    private int likes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date datePosted;

    @JsonIgnore
    private String formattedDate;

    /**
     * Object reference for the user who posted the quack as a JPA relation
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User poster;

    /**
     * List of users who have liked the quack as a JPA relation
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    List<User> likers;

    /**
     * Object reference for the attachment posted with the quack
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "file_map_id")
    private FileMap attachment;

    /**
     * @return Returns the message content as a String object
     */
    public String getQuackMessage() {
        return quackMessage;
    }

    /**
     * @param quackMessage String that the message should be set to
     */
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

    /**
     * @return List of Users that have liked the quack
     */
    public List<User> getLikers() {
        return likers;
    }

    /**
     * @param user User to be added to the list of users who have liked the quack
     */
    public void addLiker(User user) {
        likers.add(user);
    }

    /**
     * @param user User to be removed from the list of Users who have liked the quack
     */
    public void removeLiker(User user) {
        likers.remove(user);
    }

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
     *
     * @param likers
     */
    public void setLikers(List<User> likers) {
        this.likers = likers;
    }

    public boolean isNew(Date latestQuackView) {
        if (latestQuackView == null) {
            return true;
        }
        return getDatePosted().after(latestQuackView);
    }
}
