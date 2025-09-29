package com.guitartune.project_raksa.repositorys;

import com.guitartune.project_raksa.models.Transaction;
import com.guitartune.project_raksa.models.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findTransactionByUser(User user);
}
