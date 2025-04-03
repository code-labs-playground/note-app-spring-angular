package lk.ijse.dep13.springbackend.api;

import jakarta.servlet.http.HttpServletRequest;
import lk.ijse.dep13.springbackend.entity.User;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthHttpController {

    @Autowired
    private Connection connection;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/login", consumes = "application/json")
    public User logIn(@RequestBody User user, HttpServletRequest request) throws SQLException {
        try (var stm = connection.prepareStatement("SELECT * FROM \"user\" WHERE email=?")) {
            stm.setString(1, user.getEmail());
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                String actualPassword = rst.getString("password");
                String encryptedPassword = DigestUtils.sha256Hex(user.getPassword());
                if (actualPassword.equals(encryptedPassword)) {
                    request.getSession().setAttribute("user", user.getEmail());
                    String fullName = rst.getString("full_name");
                    // requireNonNullElse() is using for test and set the image
                    String profilePicture = Objects.requireNonNullElse(rst
                            .getString("profile_picture"), User.DEFAULT_PROFILE_PICTURE);
                    return new User(fullName, user.getEmail(), encryptedPassword, profilePicture);
                }else{
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
                }
            }else{
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
            }
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/logout")
    public String logOut() {
        return "Log out";
    }
}