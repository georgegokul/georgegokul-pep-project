package Service;

import Model.Message;
import DAO.MessageDAO;


import java.util.List;

public class MessageService {

     private MessageDAO messageDAO = new MessageDAO();

    // Creates and saves a new message
    public Message createMessage(Message message) {
        return messageDAO.insertMessage(message); 
    }
    // Retrieves all messages
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }
   

    // Retrieves a single message by its ID
    public Message getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }

    public Message deleteMessageById(int messageId)
    {
        return messageDAO.deleteMessageById(messageId);
    }

    public Message updateMessageText(int messageId, String newMessageText) {
        return messageDAO.updateMessageTextById(messageId, newMessageText);
    }
    
    public List<Message> getAllMessagesByAccountId(int accountId)

    {
        return messageDAO.getAllMessagesByAccountId(accountId);
    }
    
    
}


