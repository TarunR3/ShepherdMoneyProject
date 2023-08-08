package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    // TODO: wire in the user repository (~ 1 line)
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @PutMapping("/user")
    public ResponseEntity<Integer> createUser(@RequestBody CreateUserPayload payload) {
        // TODO: Create an user entity with information given in the payload, store it in the database
        //       and return the id of the user in 200 OK response
        try {
            User user = new User();

            user.setName(payload.getName());
            user.setEmail(payload.getEmail());

            userRepository.save(user);

            return new ResponseEntity<>(user.getId(), HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<String> deleteUser(@RequestParam int userId) {
        // TODO: Return 200 OK if a user with the given ID exists, and the deletion is successful
        //       Return 400 Bad Request if a user with the ID does not exist
        //       The response body could be anything you consider appropriate
        try {
            //Checks if a userExists at that id and then deletes it returns bad request if an invalid ID is sent.
            if (userRepository.existsById(userId)) {
                userRepository.deleteById(userId);
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
