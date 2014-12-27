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
package it.polimi.meteocal.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Forecast.FIND_BY_LOCATION, query = "SELECT f FROM Forecast f WHERE f.location LIKE :location"),
    @NamedQuery(name = Forecast.FIND_OLD_FORECAST_LOCATION, query = "SELECT f FROM Forecast f WHERE f.creationDate < :today AND f.location LIKE :location"),})
public class Forecast implements Serializable {

    public static final String FIND_BY_LOCATION = "Forecast.FIND_BY_LOCATION";
    public static final String FIND_OLD_FORECAST_LOCATION = "Forecast.FIND_OLD_FORECAST_LOCATION";

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    private Float latitude;

    private Float longitude;

    @Basic
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private java.util.Calendar forecastDate;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private java.util.Calendar creationDate;

    @OneToOne(cascade = CascadeType.ALL)
    private Weather weather;

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

    public java.util.Calendar getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(java.util.Calendar forecastDate) {
        this.forecastDate = forecastDate;
    }

    public java.util.Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.util.Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Forecast)) {
            return false;
        }
        Forecast other = (Forecast) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "Forecast{" + "id=" + id + ", location=" + location + ", latitude=" + latitude + ", longitude=" + longitude + ", date=" + forecastDate + ", creationDate=" + creationDate + ", weather=" + weather + '}';
    }

}
