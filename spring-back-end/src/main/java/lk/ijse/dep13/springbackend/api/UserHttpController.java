package lk.ijse.dep13.springbackend.api;

import lk.ijse.dep13.springbackend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
        try (PreparedStatement stm = connection
                .prepareStatement("INSERT INTO \"user\" (email, password, profile_picture) VALUES (?,?,?)")) {
            stm.setString(1, user.email());
            stm.setString(2, user.password());
            stm.setString(3, user.profilePicture());
            stm.executeUpdate();
            return user;
        }
    }

    @GetMapping("/me")
    public String getUserInfo(){
        return "Get authenticated user information";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/me")
    public String updateUser(){
        return "Update user authenticated user information";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me")
    public String deleteUser(){
        return "Delete authentication user account";
    }
}
