package com.khalid.hippoClub.Controller;

import com.khalid.hippoClub.Exception.ResponseException;
import com.khalid.hippoClub.PasswordEncryption.EncryptPasswordGenerator;
import com.khalid.hippoClub.Service.AuthServise;
import com.khalid.hippoClub.dto.LoginReq;
import com.khalid.hippoClub.dto.RegisterReq;
import com.khalid.hippoClub.dto.UpdateUserReq;
import com.khalid.hippoClub.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthServise authServise;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterReq req) {
        //Checking for email patterns
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!req.getEmail().matches(regex))
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Email not valid. Make sure you are using correct email."), HttpStatus.BAD_REQUEST);

        // Check if email already exists in the database.
        User emailExists = authServise.findByEmail(req.getEmail());

        if (emailExists != null)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Oops! There is already a user with the email provided."), HttpStatus.BAD_REQUEST);

        //Password length
        if (req.getPassword().length() < 8)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Password too short. Should be at least 8 character."), HttpStatus.BAD_REQUEST);

        User user = authServise.register(req);

        return new ResponseEntity<>(ResponseException.jsonResponseWithUserInfo(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq req) {
        User user = authServise.findByEmail(req.getEmail());
        System.out.println(req);
        if (user == null)
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "Incorrect email or password"), HttpStatus.BAD_REQUEST);
        // Decrypt password
        EncryptPasswordGenerator.verifyUserPassword(req.getPassword(), user.getPassword(), user.getSalt());

        return new ResponseEntity<>(ResponseException.jsonResponseWithUserInfo(user), HttpStatus.OK);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserReq req, @PathVariable("userId") Long userId) {

        authServise.updateUser(req, userId);
        return new ResponseEntity<>(ResponseException.jsonResponse("success", "Post updated successfully"), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable("userId") Long id) {

        //checking if the user id exists in the database.
        if (authServise.findById(id).isEmpty())
            return new ResponseEntity<>(ResponseException.jsonResponse("error", "There is no user with that (" + id + ") id"), HttpStatus.BAD_REQUEST);

        authServise.deleteUser(id);
        return new ResponseEntity<>(ResponseException.jsonResponse("success", "User deleted successfully"), HttpStatus.OK);
    }
}