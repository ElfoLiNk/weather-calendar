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
import java.util.Calendar;
import java.util.List;
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
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

/**
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@NamedQueries({
    @NamedQuery(name = Event.FIND_BY_SEARCHQUERY, query = "SELECT e FROM Event e WHERE e.visibility = it.polimi.meteocal.util.Visibility.PUBLIC AND (e.name LIKE :query)"),
    @NamedQuery(name = Event.FIND_USER_OCCUPATION_RESCHEDULE, query = "SELECT e FROM Event e WHERE e.startDate >= :tomorrow AND e.startDate <= :lastforecastday"),
    @NamedQuery(name = Event.FIND_NEAR_OUTDOOR, query = "SELECT e FROM Event e WHERE e.site = it.polimi.meteocal.util.Site.OUTDOOR AND e.startDate >= :today AND e.startDate <= :threeday"),
    @NamedQuery(name = Event.FIND_BY_FORECAST, query = "SELECT e FROM Event e WHERE e.forecast = :forecast"),})
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String FIND_USER_OCCUPATION_RESCHEDULE = "Event.FIND_USER_OCCUPATION_RESCHEDULE";
    public static final String FIND_BY_SEARCHQUERY = "Event.FIND_BY_SEARCHQUERY";
    public static final String FIND_NEAR_OUTDOOR = "Event.FIND_NEAR_OUTDOOR";
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
    private List<User> eventParticipants;

    @OneToMany
    @JoinTable(name = "event_inviteduser")
    private List<User> invitedUsers;

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

    public Event() {

    }

    public User getEo() {
        return eo;
    }

    public void setEo(User eo) {
        this.eo = eo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public java.util.Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Calendar startDate) {
        this.startDate = startDate;
    }

    public java.util.Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Calendar endDate) {
        this.endDate = endDate;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<User> getEventParticipants() {
        return eventParticipants;
    }

    public void setEventParticipants(List<User> eventParticipants) {
        this.eventParticipants = eventParticipants;
    }

    public List<User> getInvitedUsers() {
        return invitedUsers;
    }

    public void setInvitedUsers(List<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Event{" + "id=" + id + ", eo=" + eo + ", name=" + name + ", description=" + description + ", location=" + location + ", site=" + site + ", startDate=" + startDate.getTime() + ", endDate=" + endDate.getTime() + ", forecast=" + forecast + ", visibility=" + visibility + ", eventParticipants=" + eventParticipants + ", invitedUsers=" + invitedUsers + '}';
    }

}
