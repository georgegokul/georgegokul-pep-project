package DAO;

import Model.Message;
import Util.ConnectionUtil;

import static org.mockito.ArgumentMatchers.refEq;

import java.sql.*;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class MessageDAO {
     
     public Message insertMessage(Message message)
     {
          try {
               
               Connection connection = ConnectionUtil.getConnection();
               String query = "insert into message (posted_by, message_text, time_posted_epoch) values (?,?,?);";
               PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
               stmt.setInt(1, message.getPosted_by());
               stmt.setString(2, message.getMessage_text());
               stmt.setLong(3, message.getTime_posted_epoch());
   
               int affectedRows = stmt.executeUpdate();
               if (affectedRows == 0) {
                    throw new SQLException("Creating account failed, no rows affected.");
               }

               try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                         message.setMessage_id(generatedKeys.getInt(1));
                    }
               }
               return message;
           } catch (SQLException e) {
               e.printStackTrace();
           }
          
          return null;
     }

     public List<Message> getAllMessages()
     {
          List<Message> messages = new ArrayList<>();
          try {
              Connection connection = ConnectionUtil.getConnection();
              String query = "SELECT * FROM message;";
              PreparedStatement stmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
              ResultSet rs = stmt.executeQuery();
  
              while (rs.next()) {
                  Message message = new Message(
                          rs.getInt("message_id"),
                          rs.getInt("posted_by"),
                          rs.getString("message_text"),
                          rs.getLong("time_posted_epoch")
                  );
                  messages.add(message);
              }
          } catch (SQLException e) {
              e.printStackTrace();
          }
          return messages;
     }
      // Retrieves a message by ID from the database
    public Message getMessageById(int messageId) {
     Message message = null;
     try {
         Connection connection = ConnectionUtil.getConnection();
         String query = "SELECT * FROM message WHERE message_id = ?;";
         PreparedStatement stmt = connection.prepareStatement(query);
         stmt.setInt(1, messageId);
         ResultSet rs = stmt.executeQuery();

         if (rs.next()) {
             message = new Message(
                     rs.getInt("message_id"),
                     rs.getInt("posted_by"),
                     rs.getString("message_text"),
                     rs.getLong("time_posted_epoch")
             );
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return message;
 }


    public Message deleteMessageById(int messageId) {
        String query = "DELETE FROM message WHERE message_id = ?";

        try {
            Connection connection = ConnectionUtil.getConnection();
            Message deletedMessage = getMessageById(messageId);
            if (deletedMessage != null) {
                // Delete the message
                PreparedStatement deleteStmt = connection.prepareStatement(query);
                deleteStmt.setInt(1, messageId);
                deleteStmt.executeUpdate();
            }
            return deletedMessage; // Returns null if message didn’t exist
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Message updateMessageTextById(int messageId ,String newMessageText) {
        String query = "UPDATE message SET message_text = ? WHERE message_id = ?";
        try {
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, newMessageText);
            stmt.setInt(2, messageId);
    
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // Retrieve updated message to return full details
                return getMessageById(messageId);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Message> getAllMessagesByAccountId(int accountId)
     {
          List<Message> messages = new ArrayList<>();
          try {
              Connection connection = ConnectionUtil.getConnection();
              String query = "SELECT * FROM message where posted_by = ?;";
              PreparedStatement stmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
              stmt.setInt(1, accountId);
              ResultSet rs = stmt.executeQuery();
  
              while (rs.next()) {
                  Message message = new Message(
                          rs.getInt("message_id"),
                          rs.getInt("posted_by"),
                          rs.getString("message_text"),
                          rs.getLong("time_posted_epoch")
                  );
                  messages.add(message);
              }
          } catch (SQLException e) {
              e.printStackTrace();
          }
          return messages;
     }
}

