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
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity that rappresent the Weather of the Event in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@Table(name = "weather")
public class Weather implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String weatherConditionCode;

    private String description;

    private Float temperature;

    private String icon;

    /**
     *
     * @return the icon of the weather
     */
    public String getIcon() {
        return icon;
    }

    /**
     *
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     *
     * @return the weather condition code
     */
    public String getWeatherConditionCode() {
        return weatherConditionCode;
    }

    /**
     *
     * @param weatherConditionCode the weather condition code to set
     */
    public void setWeatherConditionCode(String weatherConditionCode) {
        this.weatherConditionCode = weatherConditionCode;
    }

    /**
     *
     * @return the description of the weather
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return the temperature of the weather
     */
    public Float getTemperature() {
        return temperature;
    }

    /**
     *
     * @param temperature the temperature to set
     */
    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    /**
     *
     * @return the id of the weather
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
    public String toString() {
        return "Weather{" + "id=" + id + ", weatherConditionCode=" + weatherConditionCode + ", description=" + description + ", temperature=" + temperature + ", icon=" + icon + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weather)) return false;
        Weather weather = (Weather) o;
        return Objects.equals(id, weather.id) &&
                Objects.equals(weatherConditionCode, weather.weatherConditionCode) &&
                Objects.equals(description, weather.description) &&
                Objects.equals(temperature, weather.temperature) &&
                Objects.equals(icon, weather.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weatherConditionCode, description, temperature, icon);
    }
}
