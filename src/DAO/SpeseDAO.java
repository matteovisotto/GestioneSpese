package DAO;

import Beans.Spesa;
import org.apache.commons.lang3.StringEscapeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class SpeseDAO {
    private final Connection connection;

    public SpeseDAO(Connection connection){
        this.connection = connection;
    }

    public ArrayList<Spesa> getSpese(int userId) throws SQLException {
        String sql = "SELECT * FROM spese WHERE userId=? AND payed=0";
        return getSpese(sql, userId);
    }

    public ArrayList<Spesa> getStorico(int userId) throws SQLException{

        String sql = "SELECT * FROM spese WHERE userId=? AND payed=1";
        return getSpese(sql, userId);
    }

    private ArrayList<Spesa> getSpese(String sql, int userId) throws SQLException{
        ArrayList<Spesa> spese = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, userId);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String description = StringEscapeUtils.unescapeJava(resultSet.getString("description"));
            Date date = resultSet.getDate("date");
            float value = resultSet.getFloat("value");
            boolean payed = intToBool(resultSet.getInt("payed"));
            Spesa spesa = new Spesa();
            spesa.setId(id);
            spesa.setDescription(description);
            spesa.setDate(date);
            spesa.setValue(value);
            spesa.setStatus(payed);
            spese.add(spesa);
        }
        return spese;
    }

    public void addSpesa(String description, Date date, float value, int userId) throws SQLException {
        String sql = "INSERT INTO spese (description, date, value, userId, payed) VALUES (?,?,?,?,0)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,description);
        preparedStatement.setDate(2, new java.sql.Date(date.getTime()));
        preparedStatement.setFloat(3, value);
        preparedStatement.setInt(4, userId);
        preparedStatement.executeUpdate();
    }

    private boolean intToBool(int value){
        return value > 0;
    }
}
