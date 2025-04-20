package com.True.Care.repository;
import org.springframework.data.repository.CrudRepository;

import com.True.Care.modal.User;
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmail(String email); // <-- custom query method
}
