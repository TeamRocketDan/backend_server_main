package com.rocket.area.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "city_area")
public class CityArea {

    @Id
    @Column(name = "city_area_id")
    private String id;

    private String cityName;
    private String longitude;
    private String latitude;
}
