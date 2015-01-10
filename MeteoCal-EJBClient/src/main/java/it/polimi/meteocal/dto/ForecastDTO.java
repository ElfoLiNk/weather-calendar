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

/**
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

    public ForecastDTO() {
    }

    public ForecastDTO(Long id, String location, Float latitude, Float longitude, Calendar date, Calendar creationDate, WeatherDTO weather) {
        this.id = id;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.creationDate = creationDate;
        this.weather = weather;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public WeatherDTO getWeather() {
        return weather;
    }

    public void setWeather(WeatherDTO weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "ForecastDTO{" + "id=" + id + ", location=" + location + ", latitude=" + latitude + ", longitude=" + longitude + ", date=" + date + ", creationDate=" + creationDate + ", weather=" + weather + '}';
    }

}
