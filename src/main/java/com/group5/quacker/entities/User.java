package com.group5.quacker.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Entity for users
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String name;

    private String email;

    private String passwordHash;

    /**
     * Object reference for the profile photo of a user
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_map_id")
    private FileMap profilePhoto;

    /**
     * Object references to the quacks posted by a user as a JPA relation
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "quack_id")
    private List<Quack> quacks;

    /**
     * Object references to users that this user has followed as a JPA relation
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<User> following;

    /**
     * Object references to users that have followed this user as a JPA relation
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private List<User> followers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public FileMap getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(FileMap profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public void addQuack(Quack quack) {
        quacks.add(quack);
    }

    public long getNumberOfQuacks() {
        return quacks.size();
    }

    public void addFollowing(User user) {
        following.add(user);
    }

    public void addFollower(User user) {
        followers.add(user);
    }

    public void removeFollowing(User user) {
        following.remove(user);
    }

    public void removeFollower(User user) {
        followers.remove(user);
    }

    public List<User> getFollowing() { return following; }

    public List<User> getFollowers() { return followers; }

    public long getNumberFollowing() { return following.size(); }

    public long getNumberFollowers() { return followers.size(); }

    public List<Quack> getQuacks() { return quacks; }

    /**
     * Testaamista varten. Kunnes löytyy parempi tapa saada lista mukaan
     * @param quacks
     */
    public void setQuacks(List<Quack> quacks) {
        this.quacks = quacks;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }
}