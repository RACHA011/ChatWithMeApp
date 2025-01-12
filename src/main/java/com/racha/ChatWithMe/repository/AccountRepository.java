package com.racha.ChatWithMe.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.racha.ChatWithMe.model.Account;

public interface AccountRepository extends MongoRepository<Account, String> {

    Optional<Account> findTopByOrderByIdDesc();

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    @Query("{ }, { '$group': { '_id': null, 'maxId': { '$max': '$_id' } } }") // Valid MongoDB aggregation query
    Optional<Account> findMaxId(); // Adjust logic if IDs are strings
}
