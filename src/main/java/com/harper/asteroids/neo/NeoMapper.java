package com.harper.asteroids.neo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harper.asteroids.model.Feed;
import com.harper.asteroids.model.NearEarthObject;
import java.io.IOException;

public class NeoMapper {
  private final ObjectMapper MAPPER = new ObjectMapper();

  public Feed readFeed(String response)
      throws NeoException {
    try {
      return MAPPER.readValue(response, Feed.class);
    } catch (JsonProcessingException e) {
      throw new NeoException(String.format("Error while mapping the feed Json %s", e.getMessage()));
    } catch (IllegalArgumentException | IOException e) {
      throw new NeoException(String.format("Failed scanning for asteroids: %s", e.getMessage()));
    }
  }

  public NearEarthObject readNeo(String response)
      throws NeoException {
    try {
      return MAPPER.readValue(response, NearEarthObject.class);
    } catch (JsonProcessingException e) {
      throw new NeoException(
          String.format("Error while mapping the near earth object Json %s", e.getMessage()));
    } catch (IllegalArgumentException | IOException e) {
      throw new NeoException(String.format("Failed scanning for asteroids: %s", e.getMessage()));
    }
  }

}
