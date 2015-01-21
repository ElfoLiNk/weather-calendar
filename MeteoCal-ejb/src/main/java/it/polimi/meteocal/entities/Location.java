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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Entity that rappresent valid Location in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Table(name="location",indexes = {
    @Index(columnList = "NAME")})
@Entity
@NamedQueries({
    @NamedQuery(name = Location.FIND_BY_SEARCHQUERY, query = "SELECT l FROM Location l WHERE l.name LIKE :query"),
    @NamedQuery(name = Location.FIND_BY_LOCATION_AND_COUNTRYCODE, query = "SELECT l FROM Location l WHERE (l.name LIKE :name AND l.countryCode = :countrycode)"),})
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_SEARCHQUERY = "Location.FIND_BY_SEARCHQUERY";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_LOCATION_AND_COUNTRYCODE = "Location.FIND_BY_LOCATION_AND_COUNTRYCODE";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 50)
    private String name;

    private float latitude;

    private float longitude;

    @Column(length = 2)
    private String countryCode;

    /**
     *
     * @return the name of the location
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the latitude of the location
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude the latitute to set
     */
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return the longitude of the location
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude the longitude to set
     */
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return the country code of the location
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     *
     * @param countryCode the country code to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     *
     * @return the id of the location
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "Location{" + "id=" + id + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + ", countryCode=" + countryCode + '}';
    }

}
