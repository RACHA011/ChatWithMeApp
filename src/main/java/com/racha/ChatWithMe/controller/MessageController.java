package com.racha.ChatWithMe.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.racha.ChatWithMe.entity.Content;
import com.racha.ChatWithMe.model.Account;
import com.racha.ChatWithMe.model.File;
import com.racha.ChatWithMe.model.Group;
import com.racha.ChatWithMe.model.Message;
import com.racha.ChatWithMe.payload.chat.MessageDTO;
import com.racha.ChatWithMe.payload.chat.MessageIdDTO;
import com.racha.ChatWithMe.payload.chat.group.GroupDTO;
import com.racha.ChatWithMe.service.AccountService;
import com.racha.ChatWithMe.service.FileService;
import com.racha.ChatWithMe.service.GroupService;
import com.racha.ChatWithMe.service.MessageService;
import com.racha.ChatWithMe.utils.constants.ChatType;
import com.racha.ChatWithMe.utils.constants.MessageStatus;
import com.racha.ChatWithMe.utils.constants.MessageType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = {"http://localhost:3000", "*"}, maxAge = 3600, allowedHeaders = "Authorization, Content-Type")
@Tag(name = "Chat Controller", description = "Controller for Chat management")
@Slf4j
public class MessageController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/send", consumes = { "application/JSON" })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send a message ")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<String> sendsMessage(
            @RequestBody MessageDTO messageDTO,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        try {
            Message message = new Message();
            message.setSenderId(optionalAccount.get().getId());

            message.setContent(new Content(messageDTO.getContent().getType(), messageDTO.getContent().getData()));
            message.setStatus(MessageStatus.SENT);
            message.setTimestamp(messageDTO.getTimestamp());

            switch (messageDTO.getChatType()) {
                case GROUP:
                    message.setChatType(ChatType.GROUP);
                    message.setChatRoomId(messageDTO.getChatRoomId());
                    break;
                case PERSON_TO_PERSON:
                    message.setReceiverId(messageDTO.getReceiverId());
                    message.setChatType(ChatType.PERSON_TO_PERSON);
                    message.setChatRoomId(generateChatRoomId(message.getSenderId(), messageDTO.getReceiverId()));
                    break;
                default:
                    log.error("Unsupported chat type: " + messageDTO.getChatType());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported chat type");
            }

            Message respondMessage = messageService.save(message);

            if (respondMessage.getContent().getType() != MessageType.TEXT) {
                return ResponseEntity.ok("" + respondMessage.getId());

            }

            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            log.error("Error while sending message: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while sending message");
        }
    }

    @PutMapping(value = "/send-file/{chatroomid}/{id}", consumes = { "multipart/form-data" })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send a a file")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<String> sendFile(
            @RequestPart("file") MultipartFile file,
            @PathVariable("id") String id,
            @PathVariable("chatroomid") String chatRoomId,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
        if (chatRoomId == null || chatRoomId.isEmpty()) {
            log.error("Chat room ID is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chat room ID is required");
        }

        try {
            CompletableFuture<Message> messagesFuture = messageService.getMessage(chatRoomId, id);
            if (messagesFuture == null) {
                log.error("Error fetching messages");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching message");
            }
            Message message = messagesFuture.get();
            try {
                if (file.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is required");
                }
                // saving the file
                byte[] bytes = file.getBytes();
                // i dont know if i should set the download link for the file or the file in
                // byte form
                File files = new File();
                // link this file with the message
                // find a way to deferentiate the files in ths database
                files.setFileBytes(bytes);
                files.setFileName(file.getOriginalFilename());
                files.setContentType(file.getContentType());
                files = fileService.save(files);

                message.setContent(new Content(message.getContent().getType(), files.getId()));
                messageService.save(message);
                return ResponseEntity.ok("File sent successfully");
            } catch (IOException e) {
                log.error("Error saving file: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file");
            }

        } catch (Exception e) {
            log.error("Error fetching message: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error fetching message");
        }

    }

    @GetMapping("/downloaded-file/{id}")
    @Operation(summary = "get file")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<?> downloadFile(@PathVariable("id") String id, Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        if (id == null || id.isEmpty()) {
            log.error("File ID is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File ID is required");
        }

        Optional<File> optionalFile = fileService.findById(id);
        if (optionalFile.isEmpty()) {
            log.error("File not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        }

        File file = optionalFile.get();

        // Ensure file bytes are present
        if (file.getFileBytes() == null || file.getFileBytes().length == 0) {
            log.error("File data is empty");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("File data is empty");
        }

        // Set appropriate headers and content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // General binary data
        headers.setContentDisposition(ContentDisposition.attachment().filename("downloaded-file").build()); // Set
                                                                                                            // filename

        // Create a temporary file
        try {

            if (file.getFileBytes() == null || file.getFileBytes().length == 0) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("File data is empty");
            }

            // Determine content type (default to application/octet-stream)
            String contentType = file.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }

            // If you know the file is a PDF, set it explicitly
            if (file.getFileName().endsWith(".pdf")) {
                contentType = "application/pdf";
                // Create a Resource directly from the byte array
                ByteArrayResource resource = new ByteArrayResource(file.getFileBytes());

                // Set Content-Disposition to prompt download with the correct filename
                String headerValue = "attachment; filename=\"" + file.getFileName() + "\"";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                        .body(resource);
            }

            String base64Encoded = Base64.getEncoder().encodeToString(file.getFileBytes());

            return ResponseEntity.ok("data:" + contentType + ";base64," + base64Encoded);

        } catch (Exception e) {
            log.error("Errorfetching file: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching file");
        }

    }

    @GetMapping("/get-messages/{chatroomid}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all messages")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<List<Message>> getMessages(@PathVariable("chatroomid") String chatRoomId,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        }

        if (chatRoomId == null || chatRoomId.isEmpty()) {
            log.error("Chat room ID is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<>());
        }

        try {
            CompletableFuture<List<Message>> messagesFuture = messageService.getMessagesByChatRoomId(chatRoomId);
            return ResponseEntity.ok(messagesFuture.get());
        } catch (Exception e) {
            log.error("Error fetching messages: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @PatchMapping("/mark-read")
    @Operation(summary = "Mark message as read")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<String> markMessageAsRead(@Valid @RequestBody MessageIdDTO messageIdDTO,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        try {
            CompletableFuture<Message> futureMessage = messageService.getMessage(
                    messageIdDTO.getChatRoomId(),
                    messageIdDTO.getId());

            Message message = futureMessage.get();
            if (message == null) {
                log.error("Message not found for ID: {}", messageIdDTO.getId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Message not found");
            }

            message.setRead(true);
            message.setStatus(MessageStatus.READ);
            messageService.save(message);

            log.info("Message {} marked as read by user {}", messageIdDTO.getId(), email);
            return ResponseEntity.ok("Message marked as read");
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error marking message as read: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error marking message as read");
        }
    }

    @DeleteMapping("/delete-messages")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a message")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<?> deleteMessages(@Valid @RequestBody MessageIdDTO[] messageIdDTO,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }
        Map<String, List<String>> map = new HashMap<>();
        List<String> succeLists = new ArrayList<>();
        List<String> failLists = new ArrayList<>();

        try {

            for (MessageIdDTO idDTO : messageIdDTO) {
                if (messageService.delete(idDTO.getChatRoomId(), idDTO.getId())) {
                    succeLists.add(idDTO.getChatRoomId() + " " + idDTO.getId());
                } else {
                    failLists.add(idDTO.getChatRoomId() + " " + idDTO.getId());
                }
            }
            map.put("Success", succeLists);
            map.put("Failed", failLists);
            return ResponseEntity.ok(map);

        } catch (Exception e) {
            log.error("Error deleting message: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting message");
        }
    }

    @GetMapping("/get-messages/{chatroomid}/last-message")
    @Operation(summary = "get last message")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<Message> getLastMessage(@PathVariable("chatroomid") String chatRoomId,
            Authentication authentication) {

        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (chatRoomId == null || chatRoomId.isEmpty()) {
            log.error("Chat room ID is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message());
        }

        try {
            CompletableFuture<List<Message>> messagesFuture = messageService.getMessagesByChatRoomId(chatRoomId);
            List<Message> messages = messagesFuture.get();
            if (messages.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new Message());
            }
            Message message = messages.get(messages.size() - 1);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("Error fetching messages: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message());
        }

    }

    @PostMapping("/create-group")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new group")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<String> createGroup(@Valid @RequestBody GroupDTO groupDTO,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        try {
            Account account = optionalAccount.get();
            Group group = new Group();
            group.setGroupName(groupDTO.getGroupName());
            group.setDescription(groupDTO.getDescription());
            group.setAdmin(account.getId());
            group.setCreatedBy(account.getId());
            group.setTimestamp(groupDTO.getTimestamp());
            group.setChatRoomId(createGroupChatRoomId());

            String defaultIconPath = "src/main/resources/static/images/people.png";
            if (Files.exists(Path.of(defaultIconPath))) {
                byte[] imageData = Files.readAllBytes(Path.of(defaultIconPath));
                group.setIcon("data:image/png;base64," + Base64.getEncoder().encodeToString(imageData));
            } else {
                group.setIcon("data:image/png;base64,PLACEHOLDER");
            }

            List<String> members = new ArrayList<>();
            members.add(account.getId());
            if (groupDTO.getMembers() != null) {
                members.addAll(groupDTO.getMembers());
            }
            group.setMembers(members);

            groupService.save(group);
            return ResponseEntity.ok("Group created successfully");
        } catch (IOException e) {
            log.error("Error reading group icon: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating group");
        }
    }

    @GetMapping("/get-chatroom-id/{receiverid}")
    @Operation(summary = "Get the chat room ID")
    @SecurityRequirement(name = "chat-whith-me-01")
    public ResponseEntity<String> getChatRoomId(@PathVariable("receiverid") String receiverId,
            Authentication authentication) {
        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.error("Account not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account currentAccount = optionalAccount.get();

        // Ensure that the user is requesting the chat room for themselves or another
        // user they are allowed to access
        String userId = currentAccount.getId();

        // Generate the chat room ID based on the authenticated user's ID and the other
        // user's ID
        try {
            String chatRoomId = generateChatRoomId(userId, receiverId);
            return ResponseEntity.ok(chatRoomId);
        } catch (Exception e) {
            log.error("Error generating chat room ID: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating chat room ID");
        }
    }

    private String generateChatRoomId(String senderId, String receiverId) {
        // Combine the IDs in a sorted manner to ensure consistency
        if (senderId.compareTo(receiverId) < 0) {
            return senderId + "_" + receiverId;
        } else {
            return receiverId + "_" + senderId;
        }
    }

    private String createGroupChatRoomId() {
        return UUID.randomUUID().toString();
    }
}
