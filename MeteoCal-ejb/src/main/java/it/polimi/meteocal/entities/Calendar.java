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
import javax.validation.constraints.NotNull;

/**
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Calendar.FIND_BY_ORGANIZEDEVENT, query = "SELECT c FROM Calendar c WHERE :event MEMBER OF c.organizedEvents"),})

public class Calendar implements Serializable {

    private static final long serialVersionUID = 1L;
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

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<Event> getParticipatedEvents() {
        return participatedEvents;
    }

    public void setParticipatedEvents(List<Event> participatedEvents) {
        this.participatedEvents = participatedEvents;
    }

    public List<Event> getOrganizedEvents() {
        return organizedEvents;
    }

    public void setOrganizedEvents(List<Event> organizedEvents) {
        this.organizedEvents = organizedEvents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addOrganizedEvent(Event event) {
        organizedEvents.add(event);

    }

    public void addParticipatedEvent(Event event) {
        participatedEvents.add(event);

    }

}
