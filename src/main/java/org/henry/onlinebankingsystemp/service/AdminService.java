package org.henry.onlinebankingsystemp.service;

import org.henry.onlinebankingsystemp.dto.RequestResponse;
import org.henry.onlinebankingsystemp.dto.UserInfo;
import org.henry.onlinebankingsystemp.entity.Users;
import org.henry.onlinebankingsystemp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public AdminService(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    public List<UserInfo> getAllUsers(){
        List<UserInfo> userInfos = new ArrayList<>();
        List<Users> users = userRepository.findAll();

        for(Users user : users){
            UserInfo currentUser = new UserInfo();
            currentUser.setUsername(user.getUsername());
            currentUser.setEmail(user.getEmail());
            currentUser.setAccountDetails(user.getAccount_details());
            currentUser.setFirstName(user.getFirst_name());
            currentUser.setLastName(user.getLast_name());
            currentUser.setPhoneNumber(user.getPhone_number());

            userInfos.add(currentUser);
        }

        return userInfos;
    }

    public Users getUserInfo(Long id){
        return userRepository.findById(id).orElseThrow(() ->
                new IllegalStateException("User with id " + id + "does not exist"));
    }

    public RequestResponse suspendUser(Long id){
        RequestResponse res = new RequestResponse();
        Users user = userRepository.findById(id).orElseThrow();
        boolean exists = userRepository.existsById(id);

        if(!exists){
            res.setStatusCode(500);
            res.setMessage("User with id" + id + "does not exist");
            return res;
        }

        user.setIsSuspended(true);
        authenticationService.revokeAllUserTokens(user);
        res.setStatusCode(200);
        res.setMessage("Successfully suspended users");
        res.setUsers(user);
        userRepository.save(user);
        return res;
    }

    public RequestResponse deleteUser(Long user_id) {
        RequestResponse res = new RequestResponse();
        boolean exists = userRepository.existsById(user_id);
        if(!exists){
            res.setStatusCode(500);
            res.setMessage("User with id" + user_id + "does not exist");
            return res;
        }
        userRepository.deleteById(user_id);

        res.setStatusCode(200);
        res.setMessage("User with id" + user_id + "has been deleted");
        return res;
    }
}
