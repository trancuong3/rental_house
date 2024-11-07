package org.example.profilecase5.Repository;


import org.example.profilecase5.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
