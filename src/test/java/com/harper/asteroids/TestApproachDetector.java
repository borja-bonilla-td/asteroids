package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.NearEarthObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestApproachDetector {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ApproachDetector approachDetector = new ApproachDetector();
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

        List<CloseApproachData> filtered = approachDetector.getClosest(neos, startDate, endDate);

        assertEquals(2, filtered.size());

        assertEquals("Wed Nov 23 12:43:00 CET 2022",
            filtered.get(0).getCloseApproachDateTime().toString());

        assertEquals("Fri Nov 25 01:00:00 CET 2022",
            filtered.get(1).getCloseApproachDate().toString());

    }
}
