package com.project.client.manager.repository;

import com.project.client.manager.model.InvoiceItem;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface InvoiceItemRepository {

    List<InvoiceItem> findByInvoiceId(Long invoiceId);

    Optional<InvoiceItem> findByIdAndInvoiceId(Long id , Long invoiceId);

    void deleteByInvoiceId(Long invoiceId);
}
