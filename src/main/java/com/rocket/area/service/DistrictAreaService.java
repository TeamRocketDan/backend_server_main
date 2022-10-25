package com.rocket.area.service;

import com.rocket.area.dto.DistrictAreaDto;

import java.util.List;

public interface DistrictAreaService {

    List<DistrictAreaDto> districtAreaList(String cityAreaId);
}
