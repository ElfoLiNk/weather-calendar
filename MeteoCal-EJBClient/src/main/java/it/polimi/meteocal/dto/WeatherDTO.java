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

/**
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class WeatherDTO {

    private Long id;

    private String weatherConditionCode;

    private String description;

    private Float temperature;

    private String icon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeatherConditionCode() {
        return weatherConditionCode;
    }

    public void setWeatherConditionCode(String weatherConditionCode) {
        this.weatherConditionCode = weatherConditionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public WeatherDTO() {
    }

    public WeatherDTO(Long id, String weatherConditionCode, String description, Float temperature, String icon) {
        this.id = id;
        this.weatherConditionCode = weatherConditionCode;
        this.description = description;
        this.temperature = temperature;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "WeatherDTO{" + "id=" + id + ", weatherConditionCode=" + weatherConditionCode + ", description=" + description + ", temperature=" + temperature + ", icon=" + icon + '}';
    }

}
