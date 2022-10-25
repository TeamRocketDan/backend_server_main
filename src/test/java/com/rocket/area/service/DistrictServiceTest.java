package com.rocket.area.service;

import com.rocket.area.repository.CityAreaRepository;
import com.rocket.area.repository.DistrictAreaRepository;
import com.rocket.area.service.impl.DistrictAreaServiceImpl;
import com.rocket.config.jpa.JpaAuditingConfiguration;
import com.rocket.error.exception.AreaException;
import com.rocket.error.type.AreaErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static com.rocket.error.type.AreaErrorCode.AREA_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Import({JpaAuditingConfiguration.class})
@ExtendWith(MockitoExtension.class)
public class DistrictServiceTest {

    @Mock
    private CityAreaRepository cityAreaRepository;
    @Mock
    private DistrictAreaRepository districtAreaRepository;

    @InjectMocks
    private DistrictAreaServiceImpl districtAreaService;

    @Nested
    @DisplayName("구 리스트")
    public class district {
        @Test
        @DisplayName("구 리스트 실패 - 시도를 찾을 수 없습니다.")
        public void fail_district_01() throws Exception {
            // given
            given(cityAreaRepository.findById(anyString()))
                    .willReturn(Optional.empty());

            // when
            AreaException areaException = assertThrows(AreaException.class,
                    () -> districtAreaService.districtAreaList("1100000000"));

            // then
            assertEquals(areaException.getErrorCode(), AREA_NOT_FOUND);
        }
    }

}
