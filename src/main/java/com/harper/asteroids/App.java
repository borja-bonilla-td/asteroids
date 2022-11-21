/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.harper.asteroids;

import com.harper.asteroids.model.CloseApproachData;
import com.harper.asteroids.model.Feed;
import com.harper.asteroids.model.NearEarthObject;
import com.harper.asteroids.neo.NeoException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main app. Gets the list of closest asteroids from NASA at
 * <a
 * href="https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY">...</a>
 * See documentation on the Asteroids - NeoWs API at <a href="https://api.nasa.gov/">...</a>
 * <p>
 * Prints the 10 closest
 * <p>
 * Risk of getting throttled if we don't sign up for own key on <a
 * href="https://api.nasa.gov/">...</a> Set environment variable 'API_KEY' to override.
 */
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    private final ApproachDetector approachDetector = new ApproachDetector();

    private List<NearEarthObject> neos;

    public static void main(String[] args) throws NeoException {
        new App().checkForAsteroids();
    }

    private void checkForAsteroids() throws NeoException {
        Feed neoFeed = approachDetector.getFeedMapping();
        neos = approachDetector.getClosestApproaches(neoFeed);
        displayClosestPassingNextWeek();
    }

    private void displayClosestPassingNextWeek() {
        Date today = new Date();
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DATE, 7);
        Date afterWeek = c.getTime();

        List<CloseApproachData> closestPassingList = approachDetector.getClosest(neos, today,
            afterWeek);

        if (closestPassingList != null && !closestPassingList.isEmpty()) {
            System.out.println("Hazard?    When                             Distance(km)     Name");
            System.out.println("----------------------------------------------------------------------");

            for (CloseApproachData closestPass : closestPassingList) {
                Optional<NearEarthObject> sortedNeos = getNeoByClosetPassing(closestPass);
                if (sortedNeos.isEmpty()) {
                    continue;
                }

                System.out.printf("%s  %s        %12.3f     %s%n",
                    (sortedNeos.get().isPotentiallyHazardous() ? "!!!" : " - "),
                    closestPass.getCloseApproachDateTime(),
                    closestPass.getMissDistance().getKilometers(),
                    sortedNeos.get().getName());
            }
            LOG.info("Displayed closest approaches with asteroids information in this week");
        } else {
            LOG.warn("No asteroids are approaching to the earth in this week");
            System.out.println("No asteroids are approaching to the earth in this week");
        }
    }

    private Optional<NearEarthObject> getNeoByClosetPassing(CloseApproachData closestPass) {
        return neos.stream().
            filter(neo -> neo.getCloseApproachData().stream()
                .anyMatch(closestPass::equals))
            .toList().stream().findAny();
    }
}
