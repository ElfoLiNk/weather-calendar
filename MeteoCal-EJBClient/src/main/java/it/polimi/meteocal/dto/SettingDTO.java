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

import it.polimi.meteocal.util.DateFormat;
import it.polimi.meteocal.util.TimeFormat;
import java.util.TimeZone;

/**
 * Class that maps the Setting entity
 * 
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class SettingDTO {

    private DateFormat dateFormat;

    private TimeFormat timeFormat;

    private TimeZone timeZone;

    /**
     *
     * @param dateFormat the date format of the setting to set
     * @param timeFormat the time format of the setting to set
     * @param timeZone the time zone of the setting to set
     */
    public SettingDTO(DateFormat dateFormat, TimeFormat timeFormat, TimeZone timeZone) {
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.timeZone = timeZone;
    }

    /**
     *
     * @return the date format of the setting
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     *
     * @param dateFormat the date format to set
     */
    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     *
     * @return the time format of the setting
     */
    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    /**
     *
     * @param timeFormat the time format to set
     */
    public void setTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     *
     * @return the time zone of the notification
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     *
     * @param timeZone the time zone to set
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

}
