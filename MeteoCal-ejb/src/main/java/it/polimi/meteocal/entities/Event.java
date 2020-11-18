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

import it.polimi.meteocal.util.Site;
import it.polimi.meteocal.util.Visibility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

/**
 * Entity that rappresent the Event of the User in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@Table(name = "event")
@NamedQueries({
    @NamedQuery(name = Event.FIND_BY_SEARCHQUERY, query = "SELECT e FROM Event e WHERE e.visibility = it.polimi.meteocal.util.Visibility.PUBLIC AND (e.name LIKE :query)"),
    @NamedQuery(name = Event.FIND_USER_OCCUPATION_RESCHEDULE, query = "SELECT e FROM Event e WHERE e.startDate >= :tomorrow AND e.startDate <= :lastforecastday"),
    @NamedQuery(name = Event.FIND_NEAR_OUTDOOR, query = "SELECT e FROM Event e WHERE e.site = it.polimi.meteocal.util.Site.OUTDOOR AND e.startDate >= :today AND e.startDate <= :threeday"),
    @NamedQuery(name = Event.FIND_BY_FORECAST, query = "SELECT e FROM Event e WHERE e.forecast = :forecast"),})
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_USER_OCCUPATION_RESCHEDULE = "Event.FIND_USER_OCCUPATION_RESCHEDULE";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_SEARCHQUERY = "Event.FIND_BY_SEARCHQUERY";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_NEAR_OUTDOOR = "Event.FIND_NEAR_OUTDOOR";

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_FORECAST = "Event.FIND_BY_FORECAST";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User eo;

    @NotNull
    private String name;

    private String description;

    private String location;

    @ObjectTypeConverter(name = "site", objectType = Site.class, dataType = String.class, conversionValues = {
        @ConversionValue(objectValue = "INDOOR", dataValue = "INDOOR"),
        @ConversionValue(objectValue = "OUTDOOR", dataValue = "OUTDOOR"),})
    @Convert("site")
    @NotNull
    private Site site;

    @Temporal(value = javax.persistence.TemporalType.TIMESTAMP)
    @Mutable(true)
    @NotNull
    private java.util.Calendar startDate;

    @Temporal(value = javax.persistence.TemporalType.TIMESTAMP)
    @Mutable(true)
    @NotNull
    private java.util.Calendar endDate;

    @OneToOne(cascade = CascadeType.PERSIST)
    private Forecast forecast;

    @ObjectTypeConverter(name = "visibility", objectType = Visibility.class, dataType = String.class, conversionValues = {
        @ConversionValue(objectValue = "PUBLIC", dataValue = "PUBLIC"),
        @ConversionValue(objectValue = "PRIVATE", dataValue = "PRIVATE"),})
    @Convert("visibility")
    @NotNull
    private Visibility visibility;

    @OneToMany
    @JoinTable(name = "event_participants")
    private List<User> eventParticipants = new ArrayList<>();

    @OneToMany
    @JoinTable(name = "event_inviteduser")
    private List<User> invitedUsers = new ArrayList<>();

    /**
     *
     * @param eo tthe owner of the event
     * @param name the name of the event
     * @param startDate the start date of the event
     * @param endDate the end date of the event
     * @param site the site of the event
     * @param visibility the visibility of the event
     * @param description the description of the evnet
     * @param location the location of the event
     * @param eventParticipants the list of event participants
     * @param invitedUsers the list of the invited users
     * @param forecast the forecast weather of the event
     */
    public Event(User eo, String name, String description, String location, Site site, Calendar startDate, Calendar endDate, Forecast forecast, Visibility visibility, List<User> eventParticipants, List<User> invitedUsers) {
        this.eo = eo;
        this.name = name;
        this.description = description;
        this.location = location;
        this.site = site;
        this.startDate = startDate;
        this.endDate = endDate;
        this.forecast = forecast;
        this.visibility = visibility;
        this.eventParticipants = eventParticipants;
        this.invitedUsers = invitedUsers;
    }

    /**
     * Default constructor
     */
    public Event() {

    }

    /**
     *
     * @return the eo of the event
     */
    public User getEo() {
        return eo;
    }

    /**
     *
     * @param eo the eo to set
     */
    public void setEo(User eo) {
        this.eo = eo;
    }

    /**
     *
     * @return the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return the description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return the location of the event
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return the site of the event
     */
    public Site getSite() {
        return site;
    }

    /**
     *
     * @param site the site to set
     */
    public void setSite(Site site) {
        this.site = site;
    }

    /**
     *
     * @return the start date of the event
     */
    public java.util.Calendar getStartDate() {
        return startDate;
    }

    /**
     *
     * @param startDate the start date to set
     */
    public void setStartDate(java.util.Calendar startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return the end date of the event
     */
    public java.util.Calendar getEndDate() {
        return endDate;
    }

    /**
     *
     * @param endDate the end date to set
     */
    public void setEndDate(java.util.Calendar endDate) {
        this.endDate = endDate;
    }

    /**
     *
     * @return the forecast of the event
     */
    public Forecast getForecast() {
        return forecast;
    }

    /**
     *
     * @param forecast the forecast to set
     */
    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    /**
     *
     * @return the visibility of the event
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     *
     * @param visibility the visibility to set
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    /**
     *
     * @return the list of event participants
     */
    public List<User> getEventParticipants() {
        return eventParticipants;
    }

    /**
     *
     * @param eventParticipants the event participants to set
     */
    public void setEventParticipants(List<User> eventParticipants) {
        this.eventParticipants = eventParticipants;
    }

    /**
     *
     * @return the invited users to the event
     */
    public List<User> getInvitedUsers() {
        return invitedUsers;
    }

    /**
     *
     * @param invitedUsers the invited users to set
     */
    public void setInvitedUsers(List<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    /**
     *
     * @return the id of the event
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

//    @Override
//    public String toString() {
//        return "Event{" + "id=" + id + ", eo=" + eo + ", name=" + name + ", description=" + description + ", location=" + location + ", site=" + site + ", startDate=" + startDate.getTime() + ", endDate=" + endDate.getTime() + ", forecast=" + forecast + ", visibility=" + visibility + ", eventParticipants=" + eventParticipants + ", invitedUsers=" + invitedUsers + '}';
//    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     *
     * @param user the user to invite to the event
     */
    public void addInvitedUser(User user) {
        invitedUsers.add(user);

    }

    /**
     *
     * @param user the user to add to the event participants
     */
    public void addEventParticipant(User user) {
        eventParticipants.add(user);

    }
}
