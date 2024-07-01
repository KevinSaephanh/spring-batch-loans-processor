package com.kevin.batch.loans;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Date;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID loanId;

    private UUID userId;

    private double total;

    private double interest;

    private double monthlyPayment;

    private double balance;

    private LoanStatus status;

    @CreatedDate
    private Date startDate;

    @LastModifiedDate
    private Date updatedDate;

    @Temporal(TemporalType.DATE)
    private Date nextPaymentDate;
}
