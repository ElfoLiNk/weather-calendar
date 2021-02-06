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

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;

/**
 * Class that implements the WeatherScheduleModel interface
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class DefaultWeatherScheduleModel implements WeatherScheduleModel, Serializable {

    private List<DefaultScheduleEvent<WeatherScheduleEventData>> events;
    private boolean eventLimit = false;

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
    public DefaultWeatherScheduleModel(List<DefaultScheduleEvent<WeatherScheduleEventData>> events) {
        this.events = events;
    }

    /**
     *
     * @param event
     */
    @Override
    public void addEvent(DefaultScheduleEvent<WeatherScheduleEventData> event) {
        //event.setId(UUID.randomUUID().toString());
        events.add(event);
    }

    @Override
    public boolean deleteEvent(DefaultScheduleEvent<WeatherScheduleEventData> event) {
        return events.remove(event);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public DefaultScheduleEvent<WeatherScheduleEventData> getEvent(String id) {
        for (DefaultScheduleEvent<WeatherScheduleEventData> event : events) {
            if (event.getId().equals(id)) {
                return event;
            }
        }

        return null;
    }

    @Override
    public void updateEvent(DefaultScheduleEvent<WeatherScheduleEventData> event) {
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

    @Override
    public boolean isEventLimit() {
        return this.eventLimit;
    }

    /**
     *
     * @param event
     */
    @Override
    public void addEvent(ScheduleEvent event) {
        event.setId(UUID.randomUUID().toString());

        events.add((DefaultScheduleEvent<WeatherScheduleEventData>) event);
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
    public List<ScheduleEvent<?>> getEvents() {
        return new ArrayList<>(events);
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
            events.set(index, (DefaultScheduleEvent<WeatherScheduleEventData>) event);
        }
    }

}
