package dev.omerdanismaz.WSIW.services;

import dev.omerdanismaz.WSIW.models.MovieRecordModel;

import java.util.List;

public interface IMovieRecordService
{
    void createMovieRecord(MovieRecordModel movieRecordModel);
    MovieRecordModel readMovieRecordById(Long movieRecordId);
    List<MovieRecordModel> readAllRecommendMovieRecordsByUserId(Long userId);
    List<MovieRecordModel> readAllDoNotRecommendMovieRecordsByUserId(Long userId);
    void deleteMovieRecord(MovieRecordModel movieRecordModel);
}
