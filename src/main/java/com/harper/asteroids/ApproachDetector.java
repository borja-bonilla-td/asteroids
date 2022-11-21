package com.harper.asteroids;

import com.harper.asteroids.model.Feed;
import com.harper.asteroids.model.NearEarthObject;

import com.harper.asteroids.neo.NeoClient;
import com.harper.asteroids.neo.NeoException;
import com.harper.asteroids.neo.NeoMapper;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

}
