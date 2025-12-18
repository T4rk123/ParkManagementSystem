package com.amusementpark.services;

import com.amusementpark.models.User;
import com.amusementpark.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private UserRepository userRepo = new UserRepository();

    public User login(String username, String password) {
        User user = userRepo.findByUsername(username);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public void register(String username, String password, String role) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(username, hashed, role);
        userRepo.save(user);
    }
}