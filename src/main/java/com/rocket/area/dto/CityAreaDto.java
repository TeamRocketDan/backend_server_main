package com.rocket.area.dto;

import com.rocket.area.entity.CityArea;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityAreaDto {

    private String id;
    private String cityName;
    private String longitude;
    private String latitude;

    public static CityAreaDto fromEntity(CityArea cityArea) {
        return CityAreaDto.builder()
                .id(cityArea.getId())
                .cityName(cityArea.getCityName())
                .longitude(cityArea.getLongitude())
                .latitude(cityArea.getLatitude())
                .build();
    }
}
