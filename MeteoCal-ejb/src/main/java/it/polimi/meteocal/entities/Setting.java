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

import it.polimi.meteocal.util.DateFormat;
import it.polimi.meteocal.util.TimeFormat;
import java.io.Serializable;
import java.util.TimeZone;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

/**
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
public class Setting implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ObjectTypeConverter(name = "dateformat", objectType = DateFormat.class, dataType = String.class, conversionValues = {
        @ConversionValue(objectValue = "DMY", dataValue = "dd/MM/yyyy"),
        @ConversionValue(objectValue = "MDY", dataValue = "MM/dd/yyyy"),
        @ConversionValue(objectValue = "YMD", dataValue = "yyyy/MM/dd")
    })
    @Convert("dateformat")
    private DateFormat dateFormat = DateFormat.DMY;

    @ObjectTypeConverter(name = "timeformat", objectType = TimeFormat.class, dataType = String.class, conversionValues = {
        @ConversionValue(objectValue = "DEFAULT", dataValue = "24h"),
        @ConversionValue(objectValue = "AMPM", dataValue = "AM/PM"),})
    @Convert("timeformat")
    private TimeFormat timeFormat = TimeFormat.DEFAULT;

    private TimeZone timeZone;

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public TimeFormat getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}