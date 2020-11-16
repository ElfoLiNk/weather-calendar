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

import it.polimi.meteocal.util.Visibility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity that rappresent the Calendar of the User in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@Table(name = "calendar")
@NamedQueries({
        @NamedQuery(name = Calendar.FIND_BY_ORGANIZEDEVENT, query = "SELECT c FROM Calendar c WHERE :event MEMBER OF c.organizedEvents"),})
public class Calendar implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_ORGANIZEDEVENT = "Calendar.FIND_BY_ORGANIZEDEVENT";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "calendar_participatedevents")
    private List<Event> participatedEvents = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "calendar_organizedevents")
    private List<Event> organizedEvents = new ArrayList<>();

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Visibility visibility = it.polimi.meteocal.util.Visibility.PRIVATE;

    /**
     * @return the calendar visibility
     * @see Visibility
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Set the Calendar visibility
     *
     * @param visibility the visibility to set
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     * @return the list of the partecipated events by the owner's calendar
     */
    public List<Event> getParticipatedEvents() {
        return participatedEvents;
    }

    /**
     * @param participatedEvents the list of participated events to set
     */
    public void setParticipatedEvents(List<Event> participatedEvents) {
        this.participatedEvents = participatedEvents;
    }

    /**
     * @return the list of organized events by the calendar's owner
     */
    public List<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    /**
     * @param organizedEvents the list of owned events to set
     */
    public void setOrganizedEvents(List<Event> organizedEvents) {
        this.organizedEvents = organizedEvents;
    }

    /**
     * @return the id of the calendar
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Add event to the organized list
     *
     * @param event the event to add
     */
    public void addOrganizedEvent(Event event) {
        organizedEvents.add(event);

    }

    /**
     * Add event to the participated list
     *
     * @param event the event to add
     */
    public void addParticipatedEvent(Event event) {
        participatedEvents.add(event);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Calendar)) return false;
        Calendar calendar = (Calendar) o;
        return Objects.equals(id, calendar.id) &&
                Objects.equals(participatedEvents, calendar.participatedEvents) &&
                Objects.equals(organizedEvents, calendar.organizedEvents) &&
                visibility == calendar.visibility;
    }

}
