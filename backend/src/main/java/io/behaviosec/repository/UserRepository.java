package io.behaviosec.repository;

import io.behaviosec.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByEmailContaining(String email);
}
