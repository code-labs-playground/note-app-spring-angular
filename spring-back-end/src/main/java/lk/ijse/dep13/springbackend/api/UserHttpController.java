package lk.ijse.dep13.springbackend.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserHttpController {

    @PostMapping
    public String signUp(){
        return "Create a new user account";
    }

    @GetMapping("/me")
    public String getUserInfo(){
        return "Get authenticated user information";
    }

    @PatchMapping("/me")
    public String updateUser(){
        return "Update user authenticated user information";
    }

    @DeleteMapping("/me")
    public String deleteUser(){
        return "Delete authentication user account";
    }
}
