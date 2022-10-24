package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.NearEarthObject;
import com.harper.asteroids.service.util.DateUtil;
import com.harper.asteroids.service.util.ServiceConstants;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Receives a set of neo ids and rates them after earth proximity.
 * Retrieves the approach data for them and sorts to the n closest next week
 * https://api.nasa.gov/neo/rest/v1/neo/
 * Alerts if someone is possibly hazardous.
 */
public class ApproachDetector<apiKey> {
    private final Client client;
    private final ObjectMapper mapper ;
    private final String apiKey;

    public ApproachDetector(ObjectMapper mapper, Client client, String apiKey) {
        this.client = client;
        this.mapper = mapper;
        this.apiKey = apiKey;
    }

    /**
     * Get the n closest approaches in this period
     * @param nearEarthObjectIds - List of objectsIds of asteroids near to earth
     * @param limit - number of asteroids details to be returned
     */
    public List<NearEarthObject> getClosestApproaches(List<String> nearEarthObjectIds, int limit) {
        List<NearEarthObject> neos = new ArrayList<>(limit);
        for(String id: nearEarthObjectIds) {
            try {
                System.out.println("Check passing of object " + id);
                Response response = client
                    .target(ServiceConstants.NASA_API_URL).path("neo").path(id)
                    .queryParam("api_key", this.apiKey)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

                NearEarthObject neo = mapper.readValue(response.readEntity(String.class), NearEarthObject.class);
                neos.add(neo);
            } catch (IOException e) {
                System.err.println("Failed scanning for asteroids: " + e);
            }
        }
        System.out.println("Received " + neos.size() + " neos, now sorting");

        return getClosest(neos, limit);
    }

    /**
     * Get the closest passing.
     * @param neos the NearEarthObjects
     * @param limit specifies the size of the closest earth objects to be returned
     * @return a list of closest earth objects
     */
    public List<NearEarthObject> getClosest(List<NearEarthObject> neos, int limit) {
        //TODO: Should ignore the passes that are not today/this week.
        return neos.stream()
                .filter(this::isApproachingComingCurrentWeek)
                .sorted(new VicinityComparator())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private boolean isApproachingComingCurrentWeek(NearEarthObject neo) {
        if(neo.getCloseApproachData() == null) {
            return false;
        }

        return neo.getCloseApproachData().stream()
                .anyMatch(closeApproachData ->
                        DateUtil.isDateInCurrentWeek(closeApproachData.getCloseApproachEpochDate()));
    }

}
