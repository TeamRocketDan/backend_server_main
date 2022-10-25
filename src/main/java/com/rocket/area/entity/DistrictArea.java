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
@Table(name = "district_area")
public class DistrictArea {

    @Id
    @Column(name = "district_area_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_area_id")
    private CityArea cityArea;

    private String districtName;
    private String longitude;
    private String latitude;
}
