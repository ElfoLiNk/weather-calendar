/*
 * Copyright (C) 2014 Matteo Gazzetta, Alessandro Fato
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.polimi.meteocal.dto;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class that maps the Forecast entity
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class ForecastDTO {

    private Long id;

    private String location;

    private Float latitude;

    private Float longitude;

    private LocalDateTime date;

    private LocalDateTime creationDate;

    private WeatherDTO weather;

    /**
     * Default construct
     */
    public ForecastDTO() {
    }

    /**
     * @param id           the id of the forecast
     * @param location     the location of the forecast
     * @param latitude     the latitude of the forecast
     * @param longitude    the longitude of the forecast
     * @param date         the date of the forecast
     * @param creationDate the creation date of the forecast
     * @param weather      the weather related to the forecast
     */
    public ForecastDTO(Long id, String location, Float latitude, Float longitude, LocalDateTime date, LocalDateTime creationDate, WeatherDTO weather) {
        this.id = id;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.creationDate = creationDate;
        this.weather = weather;
    }

    /**
     * @return the id of the forecast
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the location of the forecast
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the latitude of the forecast
     */
    public Float getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude of the forecast
     */
    public Float getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the date of the forecast
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * @return the creation date of the forecast
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creation date to set
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the weather of the forecast
     */
    public WeatherDTO getWeather() {
        return weather;
    }

    /**
     * @param weather the weather to set
     */
    public void setWeather(WeatherDTO weather) {
        this.weather = weather;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForecastDTO)) return false;
        ForecastDTO that = (ForecastDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(location, that.location) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(date, that.date) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(weather, that.weather);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location, latitude, longitude, date, creationDate, weather);
    }

    @Override
    public String toString() {
        return "ForecastDTO{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", date=" + date +
                ", creationDate=" + creationDate +
                ", weather=" + weather +
                '}';
    }

}
