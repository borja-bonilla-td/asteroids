package com.harper.asteroids.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Distances{


    private Double astronomical;

    private Double lunar;

    private Double kilometers;

    private Double miles;

    public Double getAstronomical() {
        return astronomical;
    }

    public Double getLunar() {
        return lunar;
    }

    public Double getKilometers() {
        return kilometers;
    }

    public Double getMiles() {
        return miles;
    }

}
