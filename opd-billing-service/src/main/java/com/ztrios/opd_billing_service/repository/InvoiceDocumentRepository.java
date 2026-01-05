package com.ztrios.opd_billing_service.repository;

import com.ztrios.opd_billing_service.entity.InvoiceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceDocumentRepository extends MongoRepository<InvoiceDocument, String> {

    Optional<InvoiceDocument> findByAppointmentId(UUID appointmentId);

    List<InvoiceDocument> findByPatientUserId(UUID patientUserId);

}
