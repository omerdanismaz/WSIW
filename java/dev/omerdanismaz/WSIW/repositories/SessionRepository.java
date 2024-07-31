package dev.omerdanismaz.WSIW.repositories;

import dev.omerdanismaz.WSIW.models.SessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<SessionModel, Long>
{
    Optional<SessionModel> findByDbfSessionToken(String sessionToken);
    List<SessionModel> findByDbfSessionUserId(Long sessionUserId);
}
