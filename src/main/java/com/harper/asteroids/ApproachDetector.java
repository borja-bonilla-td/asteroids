package com.harper.asteroids;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.Feed;
import com.harper.asteroids.model.NearEarthObject;

import com.harper.asteroids.neo.NeoClient;
import com.harper.asteroids.neo.NeoException;
import com.harper.asteroids.neo.NeoMapper;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Receives a set of neo ids and rates them after earth proximity. Retrieves the approach data for
 * them and sorts to the n closest.
 * <a href="https://api.nasa.gov/neo/rest/v1/neo/">...</a>
 * Alerts if someone is possibly hazardous.
 */
public class ApproachDetector {
    private static final int LIMIT = 10;
    private final NeoClient client = new NeoClient();
    private final NeoMapper mapper = new NeoMapper();
    List<NearEarthObject> neos = new ArrayList<>();


    public Feed getFeedMapping() throws NeoException {
        LocalDate today = LocalDate.now();
        String feedAsString = client.getFeed(today);
        return mapper.readFeed(feedAsString);
    }

    public List<NearEarthObject> getClosestApproaches(Feed neoFeed){
        List<String> nearEarthObjectIds = neoFeed.getAllObjectIds();
        ExecutorService executor = Executors.newFixedThreadPool(nearEarthObjectIds.size());

        neos = nearEarthObjectIds.stream().parallel()
            .map(id ->
                {
                    try {
                        return CompletableFuture.supplyAsync(() -> {
                            try {
                                return client.getNeoDetails(id);
                            } catch (NeoException e) {
                                throw new RuntimeException(e);
                            }
                        }, executor).thenApply(neo -> {
                            try {
                                return mapper.readNeo(neo);
                            } catch (NeoException e) {
                                throw new RuntimeException(e);
                            }
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            ).collect(Collectors.toList());

        executor.shutdown();
        return neos;
    }

    public List<CloseApproachData> getClosest(List<NearEarthObject> neos, Date today,
        Date afterWeek) {

        return neos.stream()
            .filter(neo -> neo.getCloseApproachData() != null && !neo.getCloseApproachData().isEmpty())
            .flatMap(neo -> neo.getCloseApproachData().stream())
            .filter(closest -> closest.getCloseApproachDateTime().after(today)
                && closest.getCloseApproachDateTime().before(afterWeek))
            .sorted(Comparator.comparing(
                CloseApproachData::getCloseApproachDateTime))
            .limit(LIMIT)
            .collect(Collectors.toList());
    }

}
