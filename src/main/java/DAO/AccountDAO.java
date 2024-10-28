package DAO;

import Model.Account;
import Util.ConnectionUtil;
import java.sql.*;

public class AccountDAO {

    // Method to check if username already exists
    public Account findByUsername(String username) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String query = "SELECT * FROM account WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to save a new account to the database
    public Account createAccount(Account account) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String query = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getPassword());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setAccount_id(generatedKeys.getInt(1));
                }
            }
            return account;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isAccountExists(int accountId) {
        String query = "SELECT * FROM account WHERE account_id = ?";
        try {
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            if(rs.next()){
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if no account exists with the given account_id

    }
}
