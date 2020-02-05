package com.group5.quacker.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(unique = true)
    private String name;

    private String email;

    private String passwordHash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_map_id")
    private FileMap profilePhoto;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "quack_id")
    private List<Quack> quacks;

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

}