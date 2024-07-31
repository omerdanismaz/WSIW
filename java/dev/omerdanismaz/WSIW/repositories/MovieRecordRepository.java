package dev.omerdanismaz.WSIW.repositories;

import dev.omerdanismaz.WSIW.models.MovieRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRecordRepository extends JpaRepository<MovieRecordModel, Long>
{
    List<MovieRecordModel> findByDbfMovieRecordUserIdAndDbfMovieRecordDoNotRecommendFalse(Long userId);
    List<MovieRecordModel> findByDbfMovieRecordUserIdAndDbfMovieRecordDoNotRecommendTrue(Long userId);
}
