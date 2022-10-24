package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.NearEarthObject;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestApproachDetector {

    private ObjectMapper mapper = new ObjectMapper();
    private NearEarthObject neo1, neo2;
    private ApproachDetector approachDetector;

    @Before
    public void setUp() throws IOException {
        neo1 = mapper.readValue(getClass().getResource("/neo_example.json"), NearEarthObject.class);
        neo2 = mapper.readValue(getClass().getResource("/neo_example2.json"), NearEarthObject.class);
        approachDetector=new ApproachDetector(mapper, ClientBuilder.newClient(), "DEMO");
    }

    @Test
    public void testFiltering() {

        List<NearEarthObject> neos = List.of(neo1, neo2);
        List<NearEarthObject> filtered = approachDetector.getClosest(neos, 1);
        assertEquals(1, filtered.size());
        assertEquals(neo2, filtered.get(0));

    }
}
