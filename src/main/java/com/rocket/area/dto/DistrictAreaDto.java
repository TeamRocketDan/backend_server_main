package com.rocket.area.dto;

import com.rocket.area.entity.CityArea;
import com.rocket.area.entity.DistrictArea;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistrictAreaDto {

    private String id;
    private String cityAreaId;
    private String cityName;
    private String districtName;
    private String longitude;
    private String latitude;

    public static DistrictAreaDto fromEntity(DistrictArea districtArea) {
        return DistrictAreaDto.builder()
                .id(districtArea.getId())
                .cityAreaId(districtArea.getCityArea().getId())
                .cityName(districtArea.getCityArea().getCityName())
                .districtName(districtArea.getDistrictName())
                .longitude(districtArea.getLongitude())
                .latitude(districtArea.getLatitude())
                .build();
    }

}
