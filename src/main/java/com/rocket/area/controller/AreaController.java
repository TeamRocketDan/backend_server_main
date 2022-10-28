package com.rocket.area.controller;

import com.rocket.area.service.CityAreaService;
import com.rocket.area.service.DistrictAreaService;
import com.rocket.utils.ApiUtils;
import com.rocket.utils.ApiUtils.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="지역 API",description = "시도, 구를 조회하는 API")
public class AreaController {

    private final CityAreaService cityAreaService;
    private final DistrictAreaService districtAreaService;

    @GetMapping("/city")
    @Operation(summary = "시도 리스트" ,description = "전국 시도 리스트 조회가 가능합니다.")
    public ApiResult cityList() {

        return success(cityAreaService.cityList());
    }

    @GetMapping("/{cityId}/district")
    @Operation(summary = "구 리스트" ,description = "전국 구 리스트 조회가 가능합니다.")
    public ApiResult districtList(
            @Parameter(description = "시도의 ID가 필요합니다.")@PathVariable("cityId") String cityId) {

        return success(districtAreaService.districtAreaList(cityId));
    }
}
