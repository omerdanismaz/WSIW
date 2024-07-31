package dev.omerdanismaz.WSIW.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dbtMovieRecords")
public class MovieRecordModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbfMovieRecordId;

    @Column(nullable = false)
    private Long dbfMovieRecordUserId;

    @Column(length = 64, nullable = false)
    private String dbfMovieRecordMovieName;

    @Column(nullable = false)
    private boolean dbfMovieRecordDoNotRecommend;

    @Column(nullable = false)
    private Date dbfMovieRecordCreatedOn;
}
