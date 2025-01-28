package com.racha.ChatWithMe.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.racha.ChatWithMe.model.Account;
import com.racha.ChatWithMe.payload.auth.AccountDTO;
import com.racha.ChatWithMe.payload.auth.AccountIdDTO;
import com.racha.ChatWithMe.payload.auth.AccountLoginDTO;
import com.racha.ChatWithMe.payload.auth.AccountPasswordDTO;
import com.racha.ChatWithMe.payload.auth.AccountViewIdDTO;
import com.racha.ChatWithMe.payload.auth.AccountViewsDTO;
import com.racha.ChatWithMe.payload.auth.AccountViewsDTOs;
import com.racha.ChatWithMe.payload.auth.TokenDTO;
import com.racha.ChatWithMe.service.AccountService;
import com.racha.ChatWithMe.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "*"}, maxAge = 3600, allowedHeaders = "Authorization, Content-Type")
@Tag(name = "Auth Controller", description = "Controller for Account management")
@Slf4j
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @GetMapping("")
    public String getRespond() {
        return "hello chatwhithme";
    }

    @PostMapping("/token")
    public ResponseEntity<TokenDTO> tokenGenerate(@Valid @RequestBody AccountLoginDTO accountDTO)
            throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(accountDTO.getEmail(), accountDTO.getPassword()));
            TokenDTO token = new TokenDTO(tokenService.generateToken(authentication));

            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            log.debug("Error while generating token: " + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }
    }

    // add a new account
    @PostMapping(value = "/user/add", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add a new user")
    public ResponseEntity<String> addUser(@RequestBody @Valid AccountDTO accountDTO) {
        try {
            Account account = new Account();
            account.setUsername(accountDTO.getUsername().toLowerCase());
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());

            try {
                byte[] imageData = Files.readAllBytes(new File("src/main/resources/static/images/user.png").toPath());
                account.setPpicture("data:image/png;base64," + Base64.getEncoder().encodeToString(imageData));
            } catch (IOException e) {
                log.error("Error while reading image: " + e.getMessage());
            }
            accountService.save(account);
            return ResponseEntity.ok("Account added successfully");
        } catch (Exception e) {
            log.error("Error while adding account: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while adding account" + e.getMessage());
        }
    }

    @PostMapping("/user/update-profile-picture")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<String> updateProfilepicture(
            @Valid @RequestPart(required = true) MultipartFile profilePicture,
            Authentication authentication) {
        String email = authentication.getName();

        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account account = optionalAccount.get();

        try {
            byte[] imageData = profilePicture.getBytes();
            account.setPpicture("data:image/png;base64," +
                    Base64.getEncoder().encodeToString(imageData));

            accountService.save(account);

            return ResponseEntity.ok("Profile picture updated successfully");
        } catch (IOException e) {
            log.error("Error while reading image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error while reading image" + e.getMessage());
        }

    }

    // update new account
    @PutMapping("/user/update")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing user")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<String> updateUser(@RequestBody @Valid AccountDTO accountDTO, Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> OptionalAccount = accountService.findByEmail(email);
            if (!OptionalAccount.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }

            Account account = OptionalAccount.get();
            account.setUsername(accountDTO.getUsername().toLowerCase());
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());
            accountService.save(account);
            return ResponseEntity.ok("Account updated successfully");
        } catch (Exception e) {
            log.error("Error while updating account: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while updating account" + e.getMessage());
        }
    }

    // change password
    @PutMapping("/user/{id}/changepassword")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change password")
    public ResponseEntity<String> updatePassword(@PathVariable("id") String id,
            @RequestBody @Valid AccountPasswordDTO accountDTO) {
        Optional<Account> optionalAccount = accountService.findById(id);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
        Account account = optionalAccount.get();
        if (passwordEncoder.matches(accountDTO.getPassword(), account.getPassword())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("password should not be the same");
        }

        account.setPassword(accountDTO.getPassword());
        accountService.save(account);

        return ResponseEntity.ok("Password updated successfully");
    }

    // delete account
    @DeleteMapping("/user/delete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete an existing user")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<String> deleteUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Account> OptionalAccount = accountService.findByEmail(email);
            if (!OptionalAccount.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }

            Account account = OptionalAccount.get();
            accountService.delete(account);
            authentication.setAuthenticated(false);

            return ResponseEntity.ok("Account deleted successfully");
        } catch (Exception e) {
            log.error("Error while deleting account: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while deleting account" + e.getMessage());
        }
    }

    // view the user account
    @GetMapping(value = "/user", produces = "application/json")
    @SecurityRequirement(name = "chat-whith-me-01")
    @Operation(summary = "view the user account")
    public ResponseEntity<Account> getUserDetails(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> OptionalAccount = accountService.findByEmail(email);
        if (!OptionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Account());
        }

        Account account = OptionalAccount.get();
        return ResponseEntity.ok(account);
    }

    @GetMapping(value = "/user/{id}", produces = "application/json")
    @SecurityRequirement(name = "chat-whith-me-01")
    @Operation(summary = "view the an account")
    public ResponseEntity<AccountViewsDTO> getUserDetails(@Valid @PathVariable("id") String id) {

        Optional<Account> optionalAccount = accountService.findById(id);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AccountViewsDTO());
        }

        Account account = optionalAccount.get();

        return ResponseEntity.ok(new AccountViewsDTO(account.getId(), account.getUsername(), account.getPpicture()));
    }

    @GetMapping(value = "/user/users", produces = "application/json")
    @SecurityRequirement(name = "chat-whith-me-01")
    @Operation(summary = "View all accounts")
    public ResponseEntity<List<AccountViewsDTOs>> getAllUsers(Authentication authentication) {

        String email = authentication.getName();

        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<AccountViewsDTOs>());
        }
        Account userAccount = optionalAccount.get();
        List<Account> accounts = accountService.findAll();
        List<AccountViewsDTOs> viewDTOs = new ArrayList<>();
        accounts.forEach(account -> {
            if (!account.getId().equals(userAccount.getId())) {
                viewDTOs.add(new AccountViewsDTOs(account.getId(), account.getUsername(), account.getPpicture(),
                        account.getFriends(), account.getFriendRequests(), account.getSentFriendRequests()));
            }

        });

        return ResponseEntity.ok(viewDTOs);
    }

    @GetMapping("/user/get-userid")
    @Operation(summary = "View user id")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<AccountViewIdDTO> getMethodName(Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AccountViewIdDTO());
        }
        Account account = optionalAccount.get();
        return ResponseEntity.ok(new AccountViewIdDTO(account.getId()));

    }

    @GetMapping(value = "/user/friends", produces = "application/json")
    @Operation(summary = "View friends list")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<List<AccountViewsDTO>> getFriendsList(Authentication authentication) {

        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<AccountViewsDTO>());
        }
        Account account = optionalAccount.get();
        List<AccountViewsDTO> friends = new ArrayList<>();
        if (account.getFriends().size() > 0) {
            for (String id : account.getFriends()) {
                Optional<Account> friendOptional = accountService.findById(id);
                if (friendOptional.isPresent()) {
                    friends.add(new AccountViewsDTO(friendOptional.get().getId(), friendOptional.get().getUsername(),
                            friendOptional.get().getPpicture()));
                } else {
                    log.error("friend not found: " + id);
                }
            }
            return ResponseEntity.ok(friends);
        } else {
            log.error("no friends list found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<AccountViewsDTO>());
        }
    }

    @GetMapping(value = "/user/get-friendrequests", produces = "application/json")
    @SecurityRequirement(name = "chat-whith-me-01")
    @Operation(summary = "View friend requests")
    public ResponseEntity<List<AccountViewsDTO>> getFriendsRequest(Authentication authentication) {
        String email = authentication.getName();

        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<AccountViewsDTO>());
        }
        Account account = optionalAccount.get();
        List<AccountViewsDTO> friends = new ArrayList<>();
        if (account.getFriendRequests().size() > 0) {
            for (String id : account.getFriendRequests()) {
                Optional<Account> friendOptional = accountService.findById(id);
                if (friendOptional.isPresent()) {
                    friends.add(new AccountViewsDTO(friendOptional.get().getId(), friendOptional.get().getUsername(),
                            friendOptional.get().getPpicture()));
                } else {
                    log.error("friend not found: " + id);
                }
            }
            return ResponseEntity.ok(friends);
        } else {
            log.error("no friend request list found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<AccountViewsDTO>());
        }
    }

    @GetMapping(value = "/user/get-sentfriendrequests", produces = "application/json")
    @Operation(summary = "View sent friend requests")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<List<AccountViewsDTO>> getSentFriendsRequest(Authentication authentication) {

        String email = authentication.getName();

        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<AccountViewsDTO>());
        }
        Account account = optionalAccount.get();
        List<AccountViewsDTO> friends = new ArrayList<>();
        if (account.getSentFriendRequests().size() > 0) {
            for (String id : account.getSentFriendRequests()) {
                Optional<Account> friend = accountService.findById(id);
                if (friend.isPresent()) {
                    friends.add(new AccountViewsDTO(friend.get().getId(), friend.get().getUsername(),
                            friend.get().getPpicture()));
                } else {
                    log.error("friend not found: " + id);
                }
            }
            return ResponseEntity.ok(friends);
        } else {
            log.error("no sent friend request list found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<AccountViewsDTO>());
        }
    }

    @PostMapping(value = "/user/add-to-friendrequests", consumes = "application/json")
    @SecurityRequirement(name = "chat-whith-me-01")
    @Operation(summary = "send friends requests")
    public ResponseEntity<String> addToFriendRequests(@RequestBody AccountIdDTO accountIdDTO,
            Authentication authentication) {
        // varify the account user
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
        Account account = optionalAccount.get();

        // verify the friend user
        Optional<Account> optionalFriendAccount = accountService.findById(accountIdDTO.getId());

        if (!optionalFriendAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend account not found");
        }
        Account friendAccount = optionalFriendAccount.get();

        if (account.getFriends().contains(friendAccount.getId())
                || account.getSentFriendRequests().contains(friendAccount.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User already in your friend list or friend request list");
        }
        // if a user is in the friend request just accept it
        if (account.getFriendRequests().contains(friendAccount.getId())
                && friendAccount.getSentFriendRequests().contains(account.getId())) {
            account.getFriends().add(friendAccount.getId());
            account.getFriendRequests().remove(friendAccount.getId());
            friendAccount.getFriends().add(account.getId());
            friendAccount.getSentFriendRequests().remove(account.getId());
        } else {
            account.getSentFriendRequests().add(friendAccount.getId());
            friendAccount.getFriendRequests().add(account.getId());
        }

        accountService.save(account);
        accountService.save(friendAccount);
        return ResponseEntity.ok("Friend request sent successfully");
    }

    @PostMapping("/user/accept-friend-request")
    @SecurityRequirement(name = "chat-whith-me-01")
    @Operation(summary = "Accept a friend request")
    public ResponseEntity<String> postMethodName(@RequestBody AccountIdDTO accountIdDTO,
            Authentication authentication) {
        // varify the account user
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
        Account account = optionalAccount.get();

        // verify the friend user
        Optional<Account> optionalFriendAccount = accountService.findById(accountIdDTO.getId());

        if (!optionalFriendAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend account not found");
        }
        Account friendAccount = optionalFriendAccount.get();

        if (!account.getFriendRequests().contains(friendAccount.getId())
                && !friendAccount.getSentFriendRequests().contains(account.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have a friend request from this user or sent friend requests not found.");
        }
        account.getFriends().add(friendAccount.getId());
        account.getFriendRequests().remove(friendAccount.getId());
        friendAccount.getFriends().add(account.getId());
        friendAccount.getSentFriendRequests().remove(account.getId());
        accountService.save(account);
        accountService.save(friendAccount);
        return ResponseEntity.ok("Friend request accepted successfully");
    }

    @PostMapping("/user/decline-friend-request")
    @SecurityRequirement(name = "chat-whith-me-01")
    @Operation(summary = "Accept a decline request")
    public ResponseEntity<String> declinerequest(@RequestBody AccountIdDTO accountIdDTO,
            Authentication authentication) {
        // varify the account user
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
        Account account = optionalAccount.get();

        // verify the friend user
        Optional<Account> optionalFriendAccount = accountService.findById(accountIdDTO.getId());

        if (!optionalFriendAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend account not found");
        }
        Account friendAccount = optionalFriendAccount.get();

        System.out.println(account.getFriendRequests() + " : " + friendAccount.getSentFriendRequests());

        if (!account.getFriendRequests().contains(friendAccount.getId())
                && !friendAccount.getSentFriendRequests().contains(account.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have a friend request from this user or sent friend requests not found.");
        }
        account.getFriendRequests().remove(friendAccount.getId());
        friendAccount.getSentFriendRequests().remove(account.getId());
        accountService.save(account);
        accountService.save(friendAccount);
        return ResponseEntity.ok("Friend request declined successfully");
    }

    @PostMapping("/user/remove-friend")
    @SecurityRequirement(name = "chat-whith-me-01")
    @Operation(summary = "Remove a friend")
    public ResponseEntity<String> removeFriend(@RequestBody AccountIdDTO accountIdDTO,
            Authentication authentication) {
        // varify the account user
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
        Account account = optionalAccount.get();

        // verify the friend user
        Optional<Account> optionalFriendAccount = accountService.findById(accountIdDTO.getId());

        if (!optionalFriendAccount.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend account not found");
        }
        Account friendAccount = optionalFriendAccount.get();

        System.out.println(account.getFriendRequests() + " : " + friendAccount.getSentFriendRequests());

        if (!account.getFriends().contains(friendAccount.getId())
                && !friendAccount.getFriends().contains(account.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("friend not found");
        }
        account.getFriends().remove(friendAccount.getId());
        friendAccount.getFriends().remove(account.getId());
        accountService.save(account);
        accountService.save(friendAccount);
        return ResponseEntity.ok("Friend removed successfully");
    }

    // get authenticated user
    @GetMapping("/user/details-using-authentication")
    @Operation(summary = "Get authenticated user details")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<Map<String, String>> getUserDetailsUsingAuthentication(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            response.put("username", authentication.getName()); // Get the username
            return ResponseEntity.ok(response);
        }
        response.put("message", "No user authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}
