package com.rocket.area.service.impl;

import com.rocket.area.dto.DistrictAreaDto;
import com.rocket.area.entity.CityArea;
import com.rocket.area.repository.CityAreaRepository;
import com.rocket.area.repository.DistrictAreaRepository;
import com.rocket.area.service.DistrictAreaService;
import com.rocket.error.exception.AreaException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.rocket.error.type.AreaErrorCode.AREA_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DistrictAreaServiceImpl implements DistrictAreaService {

    private final CityAreaRepository cityAreaRepository;
    private final DistrictAreaRepository districtAreaRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "#cityAreaId", value = "districtArea")
    public List<DistrictAreaDto> districtAreaList(String cityAreaId) {
        CityArea cityArea = getCityArea(cityAreaId);

        return districtAreaRepository.findByCityArea(cityArea)
                .stream()
                .map(districtArea -> DistrictAreaDto.fromEntity(districtArea))
                .collect(Collectors.toList());
    }

    private CityArea getCityArea(String cityAreaId) {
        return cityAreaRepository.findById(cityAreaId)
                .orElseThrow(() -> new AreaException(AREA_NOT_FOUND));
    }
}
