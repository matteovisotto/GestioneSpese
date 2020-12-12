package DAO;

import Beans.User;
import Utilities.Crypto;
import org.apache.commons.lang3.StringEscapeUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class UserDAO {
    private final Connection con;

    public UserDAO(Connection connection) {
        this.con = connection;
    }

    private String getUserSalt(String username) throws SQLException, NoSuchElementException {
        String query = "SELECT salt FROM users WHERE username = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setString(1, StringEscapeUtils.escapeJava(username));
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) throw new NoSuchElementException();
                else {
                    result.next();
                    pstatement.close();
                    return result.getString("salt");
                }
            }

        }
    }
    private String getUserSalt(int userId) throws SQLException, NoSuchElementException {
        String query = "SELECT salt FROM users WHERE id = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, userId);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) throw new NoSuchElementException();
                else {
                    result.next();
                    pstatement.close();
                    return result.getString("salt");
                }
            }finally {
                pstatement.close();
            }

        }
    }

    public User checkCredentials(String username, String password) throws SQLException, NoSuchElementException {
        String salt = getUserSalt(username);
        String query = "SELECT  * FROM users WHERE username = ? AND password = ?";
        String hash = Crypto.pwHash(password,salt.getBytes(StandardCharsets.UTF_8));
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setString(1, StringEscapeUtils.escapeJava(username));
            pstatement.setString(2, hash);
            try (ResultSet result = pstatement.executeQuery()) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                    throw new NoSuchElementException();
                else {
                    result.next();
                    User user = new User();
                    user.setId(result.getInt("id"));
                    user.setUsername(StringEscapeUtils.unescapeJava(result.getString("username")));
                    user.setName(result.getString("nome"));
                    user.setSurname(result.getString("cognome"));
                    pstatement.close();
                    return user;
                }
            } finally {
                pstatement.close();
            }
        }
    }


    public void addUser(String name, String surname, String username, String password) throws SQLException {
        String query = "INSERT INTO users (nome, cognome, username, password, salt) VALUES (?, ?, ?, ?, ?)";
        String salt = Crypto.createSalt();
        String hash = Crypto.pwHash(password,salt.getBytes(StandardCharsets.UTF_8));
        try (PreparedStatement statement = con.prepareStatement(query)){
            statement.setString(1, name);
            statement.setString(2, surname);
            statement.setString(3, StringEscapeUtils.escapeJava(username));
            statement.setString(4, hash);
            statement.setString(5, salt);
            statement.executeUpdate();
            statement.close();
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isUsernameFree(String username) throws SQLException {
        String query = "SELECT 1 FROM users WHERE username= ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setString(1, StringEscapeUtils.escapeJava(username));
            try (ResultSet result = pstatement.executeQuery()) {
                // no results, credential check failed
                pstatement.close();
                return !result.isBeforeFirst();
            }
        }
    }

    public boolean existsUser(int id) throws SQLException{
        String query = "SELECT username FROM users WHERE id = ?";
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, id);
            try (ResultSet result = pstatement.executeQuery()) {
                pstatement.close();
                return result.isBeforeFirst();
            }

        }
    }
}
