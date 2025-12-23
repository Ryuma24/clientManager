package com.project.client.manager.repository;

import com.project.client.manager.model.Invoice;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWarDeployment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.naming.PartialResultException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceDao {
    private final DataSource dataSource;

    public InvoiceDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Invoice> insertInvoice(Connection connection , long clientId, List<Invoice> invoices) {
        String sqlQuery = "INSERT INTO invoices (invoice_number, amount, status , issue_date , due_date ,client_id) VALUES ( ?, ?, ?,?,?,?)";

        try(PreparedStatement statement = connection.prepareStatement(sqlQuery,Statement.RETURN_GENERATED_KEYS)){
            for(Invoice inv : invoices){
                statement.setString(1, inv.getInvoiceNumber());
                statement.setDouble(2, inv.getAmount());
                statement.setString(3, inv.getStatus());
                statement.setDate(4, Date.valueOf(inv.getIssueDate()));
                statement.setDate(5, Date.valueOf(inv.getDueDate()));
                statement.setLong(6, clientId);
                statement.executeUpdate();

                //below code is not necessary if id is to be fetched later and not in this workflow , but for learning purpose we are setting it
                try(ResultSet rs = statement.getGeneratedKeys()){
                    if(rs.next()){
                        inv.setId(rs.getLong(1));
                    }

                }
            }





            return invoices;



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Invoice> getInvoicesByClientId(long clientId){
        String sqlQuery = "Select id , invoice_number, amount, status , issue_date , due_date ,  client_id from invoices where client_id = ?";

        List<Invoice> invoices = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQuery)){

            statement.setLong(1, clientId);
            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                Invoice inv = new Invoice();
                inv.setId(rs.getLong("id"));
                inv.setInvoiceNumber(rs.getString("invoice_number"));
                inv.setAmount(rs.getDouble("amount"));
                inv.setStatus(rs.getString("status"));
                inv.setDueDate(rs.getDate("due_date").toLocalDate());
                inv.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoices.add(inv);
            }

            return invoices;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Invoice> getAllInvoices() throws SQLException {
        String sqlQuery = "Select * from invoices";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlQuery)){

            ResultSet rs = statement.executeQuery();
            List<Invoice> invoices =new ArrayList<>();
            while(rs.next()){
                Invoice inv = new Invoice();
                inv.setId(rs.getLong("id"));
                inv.setInvoiceNumber(rs.getString("invoice_number"));
                inv.setAmount(rs.getDouble("amount"));
                inv.setStatus(rs.getString("status"));
                inv.setDueDate(rs.getDate("due_date").toLocalDate());
                inv.setIssueDate(rs.getDate("issue_date").toLocalDate());
                invoices.add(inv);
            }

            return invoices;

        }

    }

    public Boolean updateInvoiceStatus(long invoiceId, String status){
        String sqlQuery = "UPDATE invoices SET status = ? WHERE id = ?";

        try(Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery)){

            statement.setString(1,status);
            statement.setLong(2,invoiceId);


            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
