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

import java.util.Calendar;
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

    private java.util.Calendar date;

    private java.util.Calendar creationDate;

    private WeatherDTO weather;

    /**
     * Default construct 
     */
    public ForecastDTO() {
    }

    /**
     *
     * @param id the id of the forecast
     * @param location the location of the forecast
     * @param latitude the latitude of the forecast
     * @param longitude the longitude of the forecast
     * @param date the date of the forecast
     * @param creationDate the creation date of the forecast
     * @param weather the weather related to the forecast
     */
    public ForecastDTO(Long id, String location, Float latitude, Float longitude, Calendar date, Calendar creationDate, WeatherDTO weather) {
        this.id = id;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.creationDate = creationDate;
        this.weather = weather;
    }

    /**
     * 
     * @return the id of the forecast
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return the location of the forecast
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return the latitude of the forecast
     */
    public Float getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude the latitude to set
     */
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return the longitude of the forecast
     */
    public Float getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude the longitude to set
     */
    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return the date of the forecast
     */
    public Calendar getDate() {
        return date;
    }

    /**
     *
     * @param date the date to set
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     *
     * @return the creation date of the forecast
     */
    public Calendar getCreationDate() {
        return creationDate;
    }

    /**
     *
     * @param creationDate the creation date to set
     */
    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    /**
     *
     * @return the weather of the forecast
     */
    public WeatherDTO getWeather() {
        return weather;
    }

    /**
     *
     * @param weather the weather to set
     */
    public void setWeather(WeatherDTO weather) {
        this.weather = weather;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForecastDTO that = (ForecastDTO) o;

        if (!id.equals(that.id)) return false;
        if (!location.equals(that.location)) return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude != null) return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude != null) return false;
        if (!date.equals(that.date)) return false;
        if (!creationDate.equals(that.creationDate)) return false;
        return weather.equals(that.weather);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + location.hashCode();
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + date.hashCode();
        result = 31 * result + creationDate.hashCode();
        result = 31 * result + weather.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ForecastDTO{" + "id=" + id + ", location=" + location + ", latitude=" + latitude + ", longitude=" + longitude + ", date=" + date.getTime() + ", creationDate=" + creationDate.getTime() + ", weather=" + weather + '}';
    }

}
