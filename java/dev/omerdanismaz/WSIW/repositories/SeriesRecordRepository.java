package dev.omerdanismaz.WSIW.repositories;

import dev.omerdanismaz.WSIW.models.SeriesRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeriesRecordRepository extends JpaRepository<SeriesRecordModel, Long>
{
    List<SeriesRecordModel> findByDbfSeriesRecordUserIdAndDbfSeriesRecordDoNotRecommendFalse(Long userId);
    List<SeriesRecordModel> findByDbfSeriesRecordUserIdAndDbfSeriesRecordDoNotRecommendTrue(Long userId);
}
