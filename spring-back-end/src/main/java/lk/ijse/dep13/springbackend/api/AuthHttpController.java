package lk.ijse.dep13.springbackend.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthHttpController {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/login")
    public String login(){
        return "Login successful";
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/logout")
    public String logout(){
        return "Logout successful";
    }
}
