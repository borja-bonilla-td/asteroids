package com.harper.asteroids;

import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.Distances;
import com.harper.asteroids.model.NearEarthObject;
import com.harper.asteroids.service.util.DateUtil;

import java.util.Comparator;
import java.util.Optional;

public class VicinityComparator implements Comparator<NearEarthObject> {

    public int compare(NearEarthObject neo1, NearEarthObject neo2) {

        Optional<Distances> neo1ClosestPass = neo1.getCloseApproachData().stream()
                .filter(closeApproachData -> DateUtil.isDateInCurrentWeek(closeApproachData.getCloseApproachEpochDate()))
                .min(Comparator.comparing(CloseApproachData::getMissDistance))
                .map(CloseApproachData::getMissDistance);
        Optional<Distances> neo2ClosestPass = neo2.getCloseApproachData().stream()
                .filter(closeApproachData -> DateUtil.isDateInCurrentWeek(closeApproachData.getCloseApproachEpochDate()))
                .min(Comparator.comparing(CloseApproachData::getMissDistance))
                .map(CloseApproachData::getMissDistance);

        if(neo1ClosestPass.isPresent()) {
            return neo2ClosestPass.map(distances -> neo1ClosestPass.get().compareTo(distances)).orElse(1);
        }
        else return -1;
    }
}
