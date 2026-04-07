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
package it.polimi.meteocal.web.schedule;

import java.util.List;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * Class that extends ScheduleModel to handle DefaultScheduleEvent<WeatherScheduleEventData>
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public interface WeatherScheduleModel extends ScheduleModel {

    /**
     *
     * @param event the event to add to the model
     */
    void addEvent(DefaultScheduleEvent<WeatherScheduleEventData> event);

    /**
     *
     * @param event the evnet to delete from the model
     * @return true if the model contains the list
     */
    boolean deleteEvent(DefaultScheduleEvent<WeatherScheduleEventData> event);

    /**
     *
     * @return the list of the events in the model
     */
    List<ScheduleEvent<?>> getEvents();

    /**
     *
     * @param id the id of the event in the model
     * @return the event of the model with the param id
     */
    @Override
    DefaultScheduleEvent<WeatherScheduleEventData> getEvent(String id);

    /**
     * Method that update the event in the model 
     * 
     * @param event  the event to update in the model
     */
    void updateEvent(DefaultScheduleEvent<WeatherScheduleEventData> event);

    /**
     *
     * @return the number of event in the model
     */
    @Override
    int getEventCount();

    /**
     * Reset the model with an empty list
     */
    @Override
    void clear();

}
