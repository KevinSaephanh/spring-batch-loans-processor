package com.kevin.batch.config;

import com.kevin.batch.loans.Loan;
import org.springframework.batch.item.ItemProcessor;

public class LoanProcessor implements ItemProcessor<Loan, Loan> {
    @Override
    public Loan process(Loan item) throws Exception {
        return item;
    }
}
