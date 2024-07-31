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
@Table(name = "dbtSeriesRecords")
public class SeriesRecordModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbfSeriesRecordId;

    @Column(nullable = false)
    private Long dbfSeriesRecordUserId;

    @Column(length = 64, nullable = false)
    private String dbfSeriesRecordSeriesName;

    @Column(nullable = false)
    private boolean dbfSeriesRecordDoNotRecommend;

    @Column(nullable = false)
    private Date dbfSeriesRecordCreatedOn;
}
