package lk.ijse.dep13.springbackend.api;

import jakarta.servlet.http.Part;
import lk.ijse.dep13.springbackend.entity.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Objects;

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
    public User getUserInfo(@SessionAttribute(value = "user", required = false) String email) throws SQLException {
        // Checking in the SecurityInterceptor
//        if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email");
        try(var stm = connection.prepareStatement("SELECT * FROM \"user\" WHERE email=?")){
            stm.setString(1, email);
            ResultSet rst = stm.executeQuery();
            rst.next();
            return new User(rst.getString("full_name"),
                    email,
                    rst.getString("password"),
                    Objects.requireNonNullElse(rst.getString("profile_picture"),
                            User.DEFAULT_PROFILE_PICTURE));
        }
    }

    @PatchMapping("/me")
    public User updateUser(@SessionAttribute("user") String email,
                           @RequestPart("fullName") String fullName,
                           @RequestPart(value = "profilePicture", required = false) Part profilePicture,
                           @RequestPart(value = "password", required = false) String password) throws SQLException, IOException {
        // Checking in the SecurityInterceptor
//        if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email");
        try (var stm = connection
                .prepareStatement("UPDATE \"user\" SET full_name=?, profile_picture=?, password=? WHERE email=?");
             var stm2 = connection.prepareStatement("SELECT password FROM \"user\" WHERE email=?")) {
            stm2.setString(1, email);
            ResultSet rst = stm2.executeQuery();
            rst.next();
            String encryptedPassword = password != null ? DigestUtils.sha256Hex(password):
                    rst.getString("password");
            String base64DataUrl = profilePicture != null ? generateBase64DataUrl(profilePicture) : null;
            stm.setString(1, fullName);
            stm.setString(2, base64DataUrl);
            stm.setString(3, encryptedPassword);
            stm.setString(4, email);
            stm.executeUpdate();
            return new User(fullName, email, encryptedPassword,
                    Objects.requireNonNullElse(base64DataUrl, User.DEFAULT_PROFILE_PICTURE));
        }
    }
    // Create the Profile picture Url
    private String generateBase64DataUrl(Part part) throws IOException {
        byte[] bytes = part.getInputStream().readAllBytes();
        String mimeType = part.getContentType();
        String base64Data = Base64.getEncoder().encodeToString(bytes);
        return "data:" + mimeType + ";base64," + base64Data;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/me")
    public void deleteUser(@SessionAttribute("user") String email) throws SQLException {
        // Checking in the SecurityInterceptor
//    if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email");
        try(PreparedStatement stm = connection.prepareStatement("DELETE FROM \"user\" WHERE email=?")) {
            stm.setString(1, email);
            stm.executeUpdate();
        }
    }
}