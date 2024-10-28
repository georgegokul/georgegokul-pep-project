package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {

    private final AccountDAO accountDAO = new AccountDAO();

    public Account registerAccount(Account account) {
        // Check if username and password meet the requirements
        if (account.getUsername() == null || account.getUsername().trim().isEmpty() ||
            account.getPassword() == null || account.getPassword().length() < 4) {
            return null;
        }

        // Check if the username already exists
        if (accountDAO.findByUsername(account.getUsername()) != null) {
            return null; // Username already taken
        }

        // If all validations pass, save the new account
        return accountDAO.createAccount(account);
    }

    public Account loginAccount(Account account)
    {
       return verifyCredentials(account.getUsername(), account.getPassword()) ;
    }

    // Verifies the username and password
    public Account verifyCredentials(String username, String password) {
        Account account = accountDAO.findByUsername(username); // Assuming this method retrieves an Account by username

        if (account != null && account.getPassword().equals(password)) {
            return account;
        }
        return null; // Return null if credentials are invalid
    }

    public boolean isUserExists(int account_id)
    {
        return accountDAO.isAccountExists(account_id);
    }

}
