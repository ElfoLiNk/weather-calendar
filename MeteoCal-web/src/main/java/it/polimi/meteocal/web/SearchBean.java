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
package it.polimi.meteocal.web;

import it.polimi.meteocal.dto.ResultDTO;
import it.polimi.meteocal.ejb.HandleEvent;
import it.polimi.meteocal.ejb.HandleForecast;
import it.polimi.meteocal.ejb.HandleUser;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that handle the search bar in the web site
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Named
@RequestScoped
public class SearchBean implements Serializable {

    @EJB
    HandleEvent handleEvent;

    @EJB
    HandleUser handleUser;

    @EJB
    HandleForecast handleForecast;

    private List<ResultDTO> result;
    private String query = null;
    private String input = null;

    private static final Logger LOGGER = LogManager.getLogger(SearchBean.class.getName());
    
    /**
     * Creates a new instance of SearchBean
     */
    public SearchBean() {
        result = new ArrayList<>();
    }

    /**
     *
     * @return the query of the search
     */
    public String getQuery() {
        return query;
    }

    /**
     *
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     *
     * @return the input of the user
     */
    public String getInput() {
        return input;
    }

    /**
     *
     * @param input the input to set
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     *
     * @param event ajax event
     */
    public void reset(AjaxBehaviorEvent event) {
        result = new ArrayList<>();
    }

    /**
     * Method that search all the public user calendars and the owned/participated events and public events
     * 
     * @param query the query to search
     * @return the results of the search
     */
    public List<ResultDTO> search(String query) {

        result = new ArrayList<>();
        if (!query.isEmpty()) {
            result.addAll(handleUser.search(query));
            result.addAll(handleEvent.search(query));
            LOGGER.log(Level.INFO, "SIZE RESULT: " + result.size());
        }
        return result;
    }

    /**
     * Method that search the user in the system
     * 
     * @param query the query of the search
     * @return the results of the search
     */
    public List<ResultDTO> searchUser(String query) {
        result = new ArrayList<>();
        if (!query.isEmpty()) {
            result.addAll(handleUser.searchUser(query));
            LOGGER.log(Level.INFO, "SIZE RESULT: " + result.size());
        }
        return result;
    }

    /**
     * Method that search a valid location
     * @param query the query of the search
     * @return the name of the location resulted by the search
     */
    public List<String> completeLocations(String query) {
        List<String> filteredLocations = new ArrayList<>();
        if (!query.isEmpty() && query.length() > 1) {
            filteredLocations.addAll(handleForecast.searchLocation(query));
        }
        return filteredLocations;
    }

    /**
     *
     * @return the results of the search
     */
    public List<ResultDTO> getResult() {
        return result;
    }

}
