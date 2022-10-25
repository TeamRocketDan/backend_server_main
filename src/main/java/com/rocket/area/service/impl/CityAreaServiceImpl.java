package com.rocket.area.service.impl;

import com.rocket.area.dto.CityAreaDto;
import com.rocket.area.repository.CityAreaRepository;
import com.rocket.area.service.CityAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityAreaServiceImpl implements CityAreaService {

    private final CityAreaRepository cityAreaRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "cityArea")
    public List<CityAreaDto> cityList() {

        return cityAreaRepository.findAll().stream()
                .map(cityArea -> CityAreaDto.fromEntity(cityArea))
                .collect(Collectors.toList());
    }
}
