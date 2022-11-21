package com.harper.asteroids.model.neo;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.NearEarthObject;
import com.harper.asteroids.neo.NeoUtility;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class TestNeoUtility {
  private final ObjectMapper mapper = new ObjectMapper();
  private NearEarthObject neo1, neo2;

  @Before
  public void setUp() throws IOException {
    neo1 = mapper.readValue(getClass().getResource("/neo_example.json"), NearEarthObject.class);
    neo2 = mapper.readValue(getClass().getResource("/neo_example2.json"), NearEarthObject.class);

  }

  @Test
  public void testFiltering() throws ParseException {
    List<NearEarthObject> neos = List.of(neo1, neo2);

    String sDate1="2022-Nov-21 11:56";
    Date startDate =new SimpleDateFormat("yyyy-MMM-dd hh:mm").parse(sDate1);
    String sDate2="2022-Nov-28 11:56";
    Date endDate =new SimpleDateFormat("yyyy-MMM-dd hh:mm").parse(sDate2);

    List<CloseApproachData> filtered = NeoUtility.getClosest(neos, startDate, endDate, 5);

    assertEquals(2, filtered.size());

    assertEquals("Wed Nov 23 12:43:00 CET 2022",
        filtered.get(0).getCloseApproachDateTime().toString());

    assertEquals("Fri Nov 25 01:00:00 CET 2022",
        filtered.get(1).getCloseApproachDate().toString());

  }


}
