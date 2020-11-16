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

import java.util.Objects;

/**
 * Class that maps the Weather entity
 * 
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class WeatherDTO {

    private Long id;

    private String weatherConditionCode;

    private String description;

    private Float temperature;

    private String icon;

    /**
     * Default Constructor
     */
    public WeatherDTO() {
    }

    /**
     *
     * @param id the id of the weather to set
     * @param weatherConditionCode the weather condition code of the weather to
     * set
     * @param description the description of the weather to set
     * @param temperature the temperature of the weather to set
     * @param icon the icon of the weather to set
     */
    public WeatherDTO(Long id, String weatherConditionCode, String description, Float temperature, String icon) {
        this.id = id;
        this.weatherConditionCode = weatherConditionCode;
        this.description = description;
        this.temperature = temperature;
        this.icon = icon;
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

    /**
     *
     * @return the weather condtion code
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeatherDTO)) return false;
        WeatherDTO that = (WeatherDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(weatherConditionCode, that.weatherConditionCode) &&
                Objects.equals(description, that.description) &&
                Objects.equals(temperature, that.temperature) &&
                Objects.equals(icon, that.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, weatherConditionCode, description, temperature, icon);
    }

    @Override
    public String toString() {
        return "WeatherDTO{" +
                "id=" + id +
                ", weatherConditionCode='" + weatherConditionCode + '\'' +
                ", description='" + description + '\'' +
                ", temperature=" + temperature +
                ", icon='" + icon + '\'' +
                '}';
    }
}
