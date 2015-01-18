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
import org.primefaces.model.ScheduleModel;

/**
 * Class that extedens ScheduleModel to handle WeatherScheduleEvent
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public interface WeatherScheduleModel extends ScheduleModel {

    public void addEvent(WeatherScheduleEvent event);

    public boolean deleteEvent(WeatherScheduleEvent event);

    public List<WeatherScheduleEvent> getWeatherEvents();

    @Override
    public WeatherScheduleEvent getEvent(String id);

    public void updateEvent(WeatherScheduleEvent event);

    @Override
    public int getEventCount();

    @Override
    public void clear();

}
