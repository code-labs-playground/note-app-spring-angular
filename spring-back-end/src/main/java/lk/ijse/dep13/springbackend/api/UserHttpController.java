package lk.ijse.dep13.springbackend.api;

import lk.ijse.dep13.springbackend.entity.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@RestController
@RequestMapping("/users")
public class UserHttpController {

    @Autowired
    private Connection connection;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = "application/json")
    public User signUp(@RequestBody User user) throws SQLException {
        try (PreparedStatement stm1 = connection
                .prepareStatement("SELECT * FROM \"user\" WHERE email = ?");
             PreparedStatement stm2 = connection
                     .prepareStatement("INSERT INTO \"user\" (email, password, profile_picture, full_name) VALUES (?, ?, DEFAULT, ?)")) {
            stm1.setString(1, user.getEmail());
            if (stm1.executeQuery().next())
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
            stm2.setString(1, user.getEmail());
            String encryptedPassword = DigestUtils.sha256Hex(user.getPassword());
            stm2.setString(2, encryptedPassword);
            stm2.setString(3, user.getFullName());
            stm2.executeUpdate();
            user.setPassword(encryptedPassword);
            user.setProfilePicture(User.DEFAULT_PROFILE_PICTURE);
            return user;
        }
    }

    @GetMapping("/me")
    public String getUserInfo() {
        return "Get authenticated user information";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/me")
    public String updateUser() {
        return "Update authenticated user information";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me")
    public String deleteUser() {
        return "Delete authenticated user account";
    }
}