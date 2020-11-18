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
package it.polimi.meteocal.ejb;

import it.polimi.meteocal.dto.ForecastDTO;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 * Class that handle the forecast in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Local
public interface HandleForecast {

    /**
     * Method that search the forecast information for the given location and
     * target date, if not present call the method addForecast(location).
     *
     * @param location the location of the event to find the forecast
     * @param date the date of the event to find the forecast
     * @return the forecast information for the given location and date if
     * present, null otherwise
     */
    ForecastDTO getForecast(String location, Date date);

    /**
     * Method that search and returns all the forecast information available for
     * the given location
     *
     * @param location the location to search the forecasts
     * @return the list of the forecasts for the location
     */
    List<ForecastDTO> getForecasts(String location);

    /**
     * Method that search in the DB and return the forecast of the given
     * forecast ID
     *
     * @param idForecast the id of the forecast in the db
     * @return the forecast if found or null
     */
    ForecastDTO getForecast(long idForecast);

    /**
     * Method that user the OWM API to save in the DB the hourly forecast
     * information available for the given location
     *
     * @param location the location to get and save the forecast from OWM API
     */
    void addHourlyForecasts(String location);

    /**
     * Method that user the OWM API to save in the DB the daily forecast
     * information available for the given location
     *
     * @param location the location to get and save the forecast from OWM API
     */
    void addDailyForecasts(String location);

    /**
     * Method usefull to clean the DB from the old forecast information 
     */
    void removeOldForecast();

    /**
     * Method that crawl the location of the OWM API and save it in the DB Used
     * only in the development phase to populate the DB with the location Very
     * Long Execution Not Enable In Production Phase
     */
    void setLocations();

    /**
     * Method that query the DB find the locations that match the given query
     *
     * @param query the string query
     * @return the list of the location that match the query
     */
    List<String> searchLocation(String query);

}
