package com.rocket.area.controller;

import com.rocket.area.service.CityAreaService;
import com.rocket.area.service.DistrictAreaService;
import com.rocket.utils.ApiUtils;
import com.rocket.utils.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rocket.utils.ApiUtils.success;

@Slf4j
@RestController
@RequestMapping("/api/v1/areas")
@RequiredArgsConstructor
public class AreaController {

    private final CityAreaService cityAreaService;
    private final DistrictAreaService districtAreaService;

    @GetMapping("/city")
    public ApiResult cityList() {

        return success(cityAreaService.cityList());
    }

    @GetMapping("/{cityId}/district")
    public ApiResult districtList(
            @PathVariable("cityId") String cityId) {

        return success(districtAreaService.districtAreaList(cityId));
    }
}
