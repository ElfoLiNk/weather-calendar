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

import it.polimi.meteocal.util.Status;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Entity that rappresent the EventNotification in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@Table(name ="eventnotification")
@NamedQueries({
    @NamedQuery(name = EventNotification.FIND_BY_EVENT, query = "SELECT n FROM EventNotification n WHERE n.event = :event"),
    @NamedQuery(name = EventNotification.FIND_BY_EVENT_AND_USER, query = "SELECT n FROM EventNotification n WHERE n.event = :event AND n.user = :user"),
    @NamedQuery(name = EventNotification.FIND_OLD_NOTIFICATION, query = "SELECT n FROM EventNotification n WHERE n.event.endDate <= :now"),})
public class EventNotification extends Notification {

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_EVENT = "EventNotification.FIND_BY_EVENT";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_EVENT_AND_USER = "EventNotification.FIND_BY_EVENT_AND_USER";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_OLD_NOTIFICATION = "EventNotification.FIND_OLD_NOTIFICATION";

    @OneToOne
    @NotNull
    private Event event;

    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     *
     * @return the status of the event notification
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     *
     * @return the event of the event notification
     */
    public Event getEvent() {
        return event;
    }

    /**
     *
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventNotification)) return false;
        EventNotification that = (EventNotification) o;
        return Objects.equals(event, that.event) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, status);
    }
}
