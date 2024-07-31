package dev.omerdanismaz.WSIW.services;

import dev.omerdanismaz.WSIW.models.MovieRecordModel;
import dev.omerdanismaz.WSIW.repositories.MovieRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MovieRecordImpl implements IMovieRecordService
{
    private final MovieRecordRepository movieRecordRepository;

    @Override
    public void createMovieRecord(MovieRecordModel movieRecordModel)
    {
        movieRecordModel.setDbfMovieRecordCreatedOn(new Date());
        movieRecordRepository.save(movieRecordModel);
    }

    @Override
    public MovieRecordModel readMovieRecordById(Long movieRecordId)
    {
        Optional<MovieRecordModel> searchedMovieRecord = movieRecordRepository.findById(movieRecordId);

        return searchedMovieRecord.orElseGet(() -> MovieRecordModel.builder()
                .dbfMovieRecordId(0L)
                .dbfMovieRecordUserId(0L)
                .dbfMovieRecordMovieName("EMPTY")
                .dbfMovieRecordDoNotRecommend(false)
                .dbfMovieRecordCreatedOn(new Date())
                .build());
    }

    @Override
    public List<MovieRecordModel> readAllRecommendMovieRecordsByUserId(Long userId)
    {
        return movieRecordRepository.findByDbfMovieRecordUserIdAndDbfMovieRecordDoNotRecommendFalse(userId);
    }

    @Override
    public List<MovieRecordModel> readAllDoNotRecommendMovieRecordsByUserId(Long userId)
    {
        return movieRecordRepository.findByDbfMovieRecordUserIdAndDbfMovieRecordDoNotRecommendTrue(userId);
    }

    @Override
    public void deleteMovieRecord(MovieRecordModel movieRecordModel)
    {
        movieRecordRepository.delete(movieRecordModel);
    }
}
