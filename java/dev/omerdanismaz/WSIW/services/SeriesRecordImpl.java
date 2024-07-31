package dev.omerdanismaz.WSIW.services;

import dev.omerdanismaz.WSIW.models.SeriesRecordModel;
import dev.omerdanismaz.WSIW.repositories.SeriesRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SeriesRecordImpl implements ISeriesRecordService
{
    private final SeriesRecordRepository seriesRecordRepository;

    @Override
    public void createSeriesRecord(SeriesRecordModel seriesRecordModel)
    {
        seriesRecordModel.setDbfSeriesRecordCreatedOn(new Date());
        seriesRecordRepository.save(seriesRecordModel);
    }

    @Override
    public SeriesRecordModel readSeriesRecordById(Long seriesRecordId)
    {
        Optional<SeriesRecordModel> searchedSeriesRecord = seriesRecordRepository.findById(seriesRecordId);

        return searchedSeriesRecord.orElseGet(() -> SeriesRecordModel.builder()
                .dbfSeriesRecordId(0L)
                .dbfSeriesRecordUserId(0L)
                .dbfSeriesRecordSeriesName("EMPTY")
                .dbfSeriesRecordDoNotRecommend(false)
                .dbfSeriesRecordCreatedOn(new Date())
                .build());
    }

    @Override
    public List<SeriesRecordModel> readAllRecommendSeriesRecordsByUserId(Long userId)
    {
        return seriesRecordRepository.findByDbfSeriesRecordUserIdAndDbfSeriesRecordDoNotRecommendFalse(userId);
    }

    @Override
    public List<SeriesRecordModel> readAllDoNotRecommendSeriesRecordsByUserId(Long userId)
    {
        return seriesRecordRepository.findByDbfSeriesRecordUserIdAndDbfSeriesRecordDoNotRecommendTrue(userId);
    }

    @Override
    public void deleteSeriesRecord(SeriesRecordModel seriesRecordModel)
    {
        seriesRecordRepository.delete(seriesRecordModel);
    }
}
