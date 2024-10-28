package Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonNode;
import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;

public class SocialMediaController {

    private final AccountService accountService = new AccountService();
    private final MessageService messageService = new MessageService();

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::registerHandler);
        app.post("/login",this::loginHandler);
        app.post("/messages",this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesUser);
        
        

        return app;
    }

    /**
     * Handler method to process user registration.
     * @param context The Javalin Context object managing HTTP request and response.
     */
    private void registerHandler(Context context) {
        // Parse the Account object from JSON request body
        Account account = context.bodyAsClass(Account.class);
        
        // Validate input
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ||
        account.getPassword() == null || account.getPassword().length() < 4) {
            context.status(400);
            return;
        }

        // Attempt to register account
        Account createdAccount = accountService.registerAccount(account);

        if (createdAccount == null) {
            context.status(400);
            
        } else {
            context.status(200).json(createdAccount);
        }
    }
    private void loginHandler(Context context) {
        Account loginRequest = context.bodyAsClass(Account.class);

        // Check if both username and password are provided
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            context.status(401);
            return;
        }

        // Verify credentials
        Account existingAccount = accountService.verifyCredentials(loginRequest.getUsername(), loginRequest.getPassword());

        if (existingAccount != null) {
            context.status(200).json(existingAccount);
        } else {
            context.status(401);
        }
    }

    private void createMessageHandler(Context context) throws JsonProcessingException {
        // ObjectMapper mapper = new ObjectMapper();
        // Message message = mapper.readValue(context.body(), Message.class);
        Message message = context.bodyAsClass(Message.class);
        // Validate message text and character length
        if (
            message.getMessage_text() == null ||
            message.getMessage_text().length() > 255 || message.getMessage_text().isEmpty() ||
            !accountService.isUserExists(message.getPosted_by())
        ) {
            context.status(400);
            return;
        }
    
        // Save and return the message
        Message createdMessage = messageService.createMessage(message);
        context.json(createdMessage);
        context.status(200);
    }
    

    private void getAllMessagesHandler(Context context) {
        List<Message> messages = messageService.getAllMessages();
        context.json(messages);
        context.status(200);     
    }

    // Assuming this is part of your main controller class
    private void getMessageByIdHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageService.getMessageById(messageId);
        
        if (message != null) {
            context.json(message);  
        } else {
            context.json("");
        }
        context.status(200);  
    }
    
    public void deleteMessageByIdHandler(Context context) {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message deletedMessage = messageService.deleteMessageById(messageId);
        
        if (deletedMessage != null) {
            context.json(deletedMessage);
        } else {
            context.json(""); 
        }
        context.status(200); 
    }

    private void updateMessageHandler(Context context) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        // Parse the JSON body to get "message_text"
        JsonNode jsonNode = mapper.readTree(context.body());
        String newMessageText = jsonNode.get("message_text").asText();
    
        // Check if the message_text is valid
        if (newMessageText == null || newMessageText.isEmpty() || newMessageText.length() > 255) {
            context.status(400);
            return;
        }
    
        // Extract message_id from the path parameter
        int messageId = Integer.parseInt(context.pathParam("message_id"));
    
        // Perform the update in the service layer
        Message updatedMessage = messageService.updateMessageText(messageId, newMessageText);
    
        // If the message wasn't found or couldn't be updated
        if (updatedMessage == null) {
            context.status(400);
            return;
        }
        
        // Respond with the updated message and a 200 status
        context.status(200);
        context.json(updatedMessage);
    }
    
    public void getMessagesUser(Context context)
    {
        int accountId = Integer.parseInt(context.pathParam("account_id"));
        List<Message> messages = messageService.getAllMessagesByAccountId(accountId);
        context.json(messages);
        context.status(200);     
    }

}
