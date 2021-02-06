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

import it.polimi.meteocal.util.Site;
import it.polimi.meteocal.util.Visibility;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Class that maps the Event entity
 * 
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class EventDTO {

    private String id;

    private String eoId;

    private String title;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean allDay = false;

    private boolean editable;

    private Site site;

    private Visibility visibility;

    private String description;

    private String location;

    private List<UserDTO> eventParticipants;

    private List<UserDTO> invitedUsers;

    private WeatherDTO weather;

    /**
     *
     * @param id the id of the event
     * @param eoId the id of the owner of the event
     * @param title the title of the event
     * @param startDate the start date of the event
     * @param endDate the end date of the event
     * @param editable if the event is editable
     * @param site the site of the event
     * @param visibility the visibility of the event
     * @param description the description of the evnet
     * @param location the location of the event
     * @param eventParticipants  the list of event participants
     * @param invitedUsers the list of the invited users
     * @param weather the weather of the event
     */
    public EventDTO(String id, String eoId, String title, LocalDateTime startDate, LocalDateTime endDate, boolean editable, Site site, Visibility visibility, String description, String location, List<UserDTO> eventParticipants, List<UserDTO> invitedUsers, WeatherDTO weather) {
        this.id = id;
        this.eoId = eoId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.editable = editable;
        this.site = site;
        this.visibility = visibility;
        this.description = description;
        this.location = location;
        this.eventParticipants = eventParticipants;
        this.invitedUsers = invitedUsers;
        this.weather = weather;
    }

    /**
     *
     * @return the weather of the event
     */
    public WeatherDTO getWeather() {
        return weather;
    }

    /**
     *
     * @param weather the weather to set
     */
    public void setWeather(WeatherDTO weather) {
        this.weather = weather;
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
     * @return the invited users of the event
     */
    public List<UserDTO> getInvitedUsers() {
        return invitedUsers;
    }

    /**
     *
     * @param invitedUsers the invited users to set
     */
    public void setInvitedUsers(List<UserDTO> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    /**
     *
     * @return the event organizer id
     */
    public String getEoId() {
        return eoId;
    }

    /**
     *
     * @param eoId the event organizer to set
     */
    public void setEoId(String eoId) {
        this.eoId = eoId;
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
     * @return the event participants
     */
    public List<UserDTO> getEventParticipants() {
        return eventParticipants;
    }

    /**
     *
     * @param eventParticipants the event participants to set
     */
    public void setEventParticipants(List<UserDTO> eventParticipants) {
        this.eventParticipants = eventParticipants;
    }

    /**
     *
     * @return the id of the event
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return the start date of the event
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     *
     * @param startDate the start date to set
     */
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return the end date of the event
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     *
     * @param endDate the end date to set
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    /**
     *
     * @return if the event is all day
     */
    public boolean isAllDay() {
        return allDay;
    }

    /**
     *
     * @param allDay set if the event is all day
     */
    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    /**
     *
     * @return if the event is editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     *
     * @param editable set  if the event is editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventDTO)) return false;
        EventDTO eventDTO = (EventDTO) o;
        return allDay == eventDTO.allDay &&
                editable == eventDTO.editable &&
                Objects.equals(id, eventDTO.id) &&
                Objects.equals(eoId, eventDTO.eoId) &&
                Objects.equals(title, eventDTO.title) &&
                site == eventDTO.site &&
                visibility == eventDTO.visibility &&
                Objects.equals(description, eventDTO.description) &&
                Objects.equals(location, eventDTO.location) &&
                Objects.equals(eventParticipants, eventDTO.eventParticipants) &&
                Objects.equals(invitedUsers, eventDTO.invitedUsers) &&
                Objects.equals(weather, eventDTO.weather);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eoId, title, startDate, endDate, allDay, editable, site, visibility, description, location, eventParticipants, invitedUsers, weather);
    }

    @Override
    public String toString() {
        return "EventDTO{" +
                "id='" + id + '\'' +
                ", eoId='" + eoId + '\'' +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", allDay=" + allDay +
                ", editable=" + editable +
                ", site=" + site +
                ", visibility=" + visibility +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", eventParticipants=" + eventParticipants +
                ", invitedUsers=" + invitedUsers +
                ", weather=" + weather +
                '}';
    }
}
