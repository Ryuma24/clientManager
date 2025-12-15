package com.project.client.manager.repository;

import com.project.client.manager.model.Client;
import com.project.client.manager.model.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClientDao {


    private final DataSource dataSource;

    @Autowired
    private InvoiceDao invoiceDao;

    public ClientDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Client insertClient(Connection connection , Client client) {
        // Implementation for inserting a client into the database
        // and returning the generated client ID.
        String insertQuery = "INSERT INTO clients (name, email, phone) VALUES (?, ?, ?)";

        try(PreparedStatement statement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1, client.getName());
            statement.setString(2, client.getEmail());
            statement.setString(3, client.getPhone());

            int affectedRows = statement.executeUpdate();

            if(affectedRows == 0){
                throw new SQLException("Inserting client failed, no rows affected.");
            }

            try(ResultSet rs = statement.getGeneratedKeys()){
                if(rs.next()){
                    long id = rs.getLong("id");
                    client.setId(id);

                    return client;
                }else {
                    throw new SQLException("Inserting client failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Placeholder return value
    }

    public List<Client> fetchAllClients(Connection connection){

        String selectQuery = "SELECT * FROM clients";

        List<Client> clientList = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(selectQuery)) {

            try(ResultSet rs = statement.executeQuery()){

                while(rs.next()){

                    long clientId = rs.getLong("id");

                    Client client = Client.builder().email(rs.getString("email")).id(clientId).name(rs.getString("name")).phone(rs.getString("phone")).build();

                    List<Invoice> invoices = invoiceDao.getInvoicesByClientId(clientId);

                    if(!invoices.isEmpty()){
                        client.setInvoices(invoices);
                    }


                    clientList.add(client);

                }
            }

            return clientList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
