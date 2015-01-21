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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.primefaces.model.ScheduleEvent;

/**
 * Class that implements the WeatherScheduleModel interface
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class DefaultWeatherScheduleModel implements WeatherScheduleModel, Serializable {

    private List<WeatherScheduleEvent> events;

    /**
     * Defaulf Constructor
     */
    public DefaultWeatherScheduleModel() {
        events = new ArrayList<>();
    }

    /**
     * Constructor with events
     * @param events the events of the model to set
     */
    public DefaultWeatherScheduleModel(List<WeatherScheduleEvent> events) {
        this.events = events;
    }

    /**
     *
     * @param event
     */
    @Override
    public void addEvent(WeatherScheduleEvent event) {
        //event.setId(UUID.randomUUID().toString());
        events.add(event);
    }

    @Override
    public boolean deleteEvent(WeatherScheduleEvent event) {
        return events.remove(event);
    }

    /**
     *
     * @return
     */
    @Override
    public List<WeatherScheduleEvent> getWeatherEvents() {
        return events;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public WeatherScheduleEvent getEvent(String id) {
        for (WeatherScheduleEvent event : events) {
            if (event.getId().equals(id)) {
                return event;
            }
        }

        return null;
    }

    @Override
    public void updateEvent(WeatherScheduleEvent event) {
        int index = -1;

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(event.getId())) {
                index = i;

                break;
            }
        }

        if (index >= 0) {
            events.set(index, event);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public int getEventCount() {
        return events.size();
    }

    /**
     *
     */
    @Override
    public void clear() {
        events = new ArrayList<>();
    }

    /**
     *
     * @param event
     */
    @Override
    public void addEvent(ScheduleEvent event) {
        event.setId(UUID.randomUUID().toString());

        events.add((WeatherScheduleEvent) event);
    }

    /**
     *
     * @param event
     * @return
     */
    @Override
    public boolean deleteEvent(ScheduleEvent event) {
        return events.remove(event);
    }

    /**
     *
     * @return
     */
    @Override
    public List<ScheduleEvent> getEvents() {
        List<ScheduleEvent> eventi = new ArrayList<>();
        for (WeatherScheduleEvent evento : events) {
            eventi.add((ScheduleEvent) evento);
        }
        return eventi;
    }

    /**
     *
     * @param event
     */
    @Override
    public void updateEvent(ScheduleEvent event) {
        int index = -1;

        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(event.getId())) {
                index = i;

                break;
            }
        }

        if (index >= 0) {
            events.set(index, (WeatherScheduleEvent) event);
        }
    }

}
