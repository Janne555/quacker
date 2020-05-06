package com.group5.quacker.services;

import com.group5.quacker.entities.FileMap;
import com.group5.quacker.entities.Quack;
import com.group5.quacker.entities.User;
import com.group5.quacker.repositories.QuackRepository;
import com.group5.quacker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuackRepository quackRepository;

    public void deleteAccount(User user) {
        List<User> toSave = new ArrayList<>();
        toSave.add(user);
        user.getFollowers().forEach(follower -> {
            follower.removeFollowing(user);
            toSave.add(follower);
        });

        user.getFollowing().forEach(following -> {
            following.removeFollower(user);
            toSave.add(following);
        });

        user.getFollowing().clear();
        user.getFollowers().clear();

        List<Quack> likedQuacks = quackRepository.findAllByLikersContains(user);
        likedQuacks.forEach(quack -> {
            quack.removeLiker(user);
        });

        quackRepository.saveAll(likedQuacks);
        userRepository.saveAll(toSave);
        userRepository.delete(user);
    }

    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByName(auth.getName());
    }

    public List<FileMap> getFilesForUser(User user) {
        List<FileMap> files = user.getQuacks().stream()
                .map(quack -> quack.getAttachment())
                .filter(fileMap -> fileMap != null)
                .collect(Collectors.toList());
        if (user.getProfilePhoto() != null) {
            files.add(user.getProfilePhoto());
        }
        return files;
    }
}
