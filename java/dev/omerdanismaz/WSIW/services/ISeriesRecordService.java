package dev.omerdanismaz.WSIW.services;

import dev.omerdanismaz.WSIW.models.SeriesRecordModel;

import java.util.List;

public interface ISeriesRecordService
{
    void createSeriesRecord(SeriesRecordModel seriesRecordModel);
    SeriesRecordModel readSeriesRecordById(Long seriesRecordId);
    List<SeriesRecordModel> readAllRecommendSeriesRecordsByUserId(Long userId);
    List<SeriesRecordModel> readAllDoNotRecommendSeriesRecordsByUserId(Long userId);
    void deleteSeriesRecord(SeriesRecordModel seriesRecordModel);
}
