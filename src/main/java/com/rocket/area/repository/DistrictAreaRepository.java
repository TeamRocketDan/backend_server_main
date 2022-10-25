package com.rocket.area.repository;

import com.rocket.area.entity.CityArea;
import com.rocket.area.entity.DistrictArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictAreaRepository extends JpaRepository<DistrictArea, String> {

    List<DistrictArea> findByCityArea(CityArea cityArea);
}
