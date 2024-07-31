package dev.omerdanismaz.WSIW.repositories;

import dev.omerdanismaz.WSIW.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>
{
    Optional<UserModel> findByDbfUserEmail(String userEmail);
}
