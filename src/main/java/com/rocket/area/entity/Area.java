package com.rocket.area.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "area")
public class Area {

    @Id
    @Column(name = "area_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_area_id")
    private DistrictArea districtArea;

    private String areaName;
    private String longitude;
    private String latitude;
}
