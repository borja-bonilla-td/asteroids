package com.harper.asteroids.neo;

import java.time.LocalDate;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeoClient {
  private static final Logger LOG = LoggerFactory.getLogger(NeoClient.class);
  private static final String NEO_FEED_URL = "https://api.nasa.gov/neo/rest/v1/feed";
  private static final String NEO_URL = "https://api.nasa.gov/neo/rest/v1/neo/";
  private static String API_KEY = "DEMO_KEY";
  private static final String FEED_API_QUERY_PARAMS = "?start_date=%s&end_date=%s&api_key=%s";

  private final Client client;

  public NeoClient() {
    String apiKey = System.getenv("API_KEY");
    if (apiKey != null && !apiKey.isBlank()) {
      API_KEY = apiKey;
    }
    this.client = ClientBuilder.newClient();
  }

  public String getFeed(LocalDate today) throws NeoException {
    Response response = null;
    try {
      response = client
          .target(getFullFeedURI(today.toString(), API_KEY))
          .request(MediaType.APPLICATION_JSON)
          .get();

      LOG.info(String.format("Got response: %s", response));
      return response.readEntity(String.class);
    } catch (NotFoundException e) {
      LOG.warn(String.format("Failed querying feed %s", response.getStatus()));
      throw new NeoException(String.format("Failed querying feed, got %s %s",
          response.getStatus(), response.getStatusInfo()));
    }
  }


  public String getNeoDetails(String id) throws NeoException {
    try {
      LOG.info(String.format("Check passing of object  %s", id));
      System.out.println("Check passing of object " + id);
      Response response = client
          .target(NEO_URL + id)
          .queryParam("api_key", API_KEY)
          .request(MediaType.APPLICATION_JSON)
          .get();
      return response.readEntity(String.class);
    } catch (NotFoundException e) {
      LOG.warn(String.format("Failed querying feed %s", e.getMessage()));
      throw new NeoException(String.format("Failed scanning for asteroids: %s" + e));
    }
  }

  private String getFullFeedURI(String today, String api_key) {
    return String.format("%s%s", NEO_FEED_URL,
        getFeedApiQueryParams(today, api_key));
  }

  public String getFeedApiQueryParams(String today, String api_key) {
    return String.format(FEED_API_QUERY_PARAMS, today, today, api_key);
  }
}
