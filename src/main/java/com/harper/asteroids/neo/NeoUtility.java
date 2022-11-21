package com.harper.asteroids.neo;

import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.NearEarthObject;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class NeoUtility {

  public static List<CloseApproachData> getClosest(List<NearEarthObject> neos, Date today,
      Date afterWeek, int limit) {

    return neos.stream()
        .filter(neo -> neo.getCloseApproachData() != null && !neo.getCloseApproachData().isEmpty())
        .flatMap(neo -> neo.getCloseApproachData().stream())
        .filter(closest -> closest.getCloseApproachDateTime().after(today)
            && closest.getCloseApproachDateTime().before(afterWeek))
        .sorted(Comparator.comparing(
            CloseApproachData::getCloseApproachDateTime))
        .limit(limit)
        .collect(Collectors.toList());
  }

}
