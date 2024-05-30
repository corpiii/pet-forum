package com.corpi.mong_nyang.repository;

import com.corpi.mong_nyang.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
