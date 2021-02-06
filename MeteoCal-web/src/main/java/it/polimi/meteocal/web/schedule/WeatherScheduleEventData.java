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

import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.dto.WeatherDTO;
import it.polimi.meteocal.util.Site;
import it.polimi.meteocal.util.Visibility;

import java.util.List;
import java.util.Objects;

/**
 * Class that the extends the DefaultScheduleEvent adding the information needed
 * in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class WeatherScheduleEventData {

    /**
     * Full Constructor
     *
     * @param location the location of the event
     * @param site the site of the event
     * @param visibility the visibility of the event
     * @param eoId the id of the owner of the event 
     * @param listParticipantAndInvitedUsers the list of participated and invited users
     * @param eventParticipants the list of the event participants
     * @param invitedUsers the list of the invited user
     * @param weather the weather of the event
     */
    public WeatherScheduleEventData(String location, Site site, Visibility visibility, String eoId, List<UserDTO> listParticipantAndInvitedUsers, List<UserDTO> eventParticipants, List<UserDTO> invitedUsers, WeatherDTO weather) {
        this.location = location;
        this.eoId = eoId;
        this.listParticipantAndInvitedUsers = listParticipantAndInvitedUsers;
        this.eventParticipants = eventParticipants;
        this.invitedUsers = invitedUsers;
        this.weather = weather;
        this.site = site.name();
        this.visibility = visibility.name();

    }

    /**
     * Default Constructor
     */
    public WeatherScheduleEventData() {
    }

    /**
     *
     * @param eoId the id of the owner of the event
     */
    public WeatherScheduleEventData(String eoId) {
        this.eoId = eoId;
    }

    private String location;

    private String eoId;

    private List<UserDTO> listParticipantAndInvitedUsers;

    private List<UserDTO> eventParticipants;

    private List<UserDTO> invitedUsers;

    private WeatherDTO weather;

    private String site;

    private String visibility;

    /**
     *
     * @return the id of the event organizer
     */
    public String getEoId() {
        return eoId;
    }

    /**
     *
     * @param eoId the id of the event organizer to set
     */
    public void setEoId(String eoId) {
        this.eoId = eoId;
    }

    /**
     *
     * @return the list of partipants and invited users of the event
     */
    public List<UserDTO> getListParticipantAndInvitedUsers() {
        return listParticipantAndInvitedUsers;
    }

    /**
     *
     * @param listParticipantAndInvitedUsers the list of partipants and invited users to set
     */
    public void setListParticipantAndInvitedUsers(List<UserDTO> listParticipantAndInvitedUsers) {
        this.listParticipantAndInvitedUsers = listParticipantAndInvitedUsers;
    }

    /**
     *
     * @return the visibility of the event
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     *
     * @param visibility the visibility to set
     */
    public void setVisibility(String visibility) {
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
     * @param invitedUsers the invited user to set
     */
    public void setInvitedUsers(List<UserDTO> invitedUsers) {
        this.invitedUsers = invitedUsers;
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
    public String getSite() {
        return site;
    }

    /**
     *
     * @param site the site to set
     */
    public void setSite(String site) {
        this.site = site;
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
     * @return the list of event participants
     */
    public List<UserDTO> getEventParticipants() {
        return eventParticipants;
    }

    /**
     *
     * @param eventParticipants the list of event participants to set
     */
    public void setEventParticipants(List<UserDTO> eventParticipants) {
        this.eventParticipants = eventParticipants;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "WeatherScheduleEvent{" + "location=" + location + ", eoId=" + eoId + ", listParticipantAndInvitedUsers=" + listParticipantAndInvitedUsers + ", eventParticipants=" + eventParticipants + ", invitedUsers=" + invitedUsers + ", weather=" + weather + ", site=" + site + ", visibility=" + visibility + '}';
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.location);
        hash = 23 * hash + Objects.hashCode(this.eoId);
        hash = 23 * hash + Objects.hashCode(this.listParticipantAndInvitedUsers);
        hash = 23 * hash + Objects.hashCode(this.eventParticipants);
        hash = 23 * hash + Objects.hashCode(this.invitedUsers);
        hash = 23 * hash + Objects.hashCode(this.weather);
        hash = 23 * hash + Objects.hashCode(this.site);
        hash = 23 * hash + Objects.hashCode(this.visibility);
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WeatherScheduleEventData other = (WeatherScheduleEventData) obj;
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        if (!Objects.equals(this.eoId, other.eoId)) {
            return false;
        }
        if (!Objects.equals(this.listParticipantAndInvitedUsers, other.listParticipantAndInvitedUsers)) {
            return false;
        }
        if (!Objects.equals(this.eventParticipants, other.eventParticipants)) {
            return false;
        }
        if (!Objects.equals(this.invitedUsers, other.invitedUsers)) {
            return false;
        }
        if (!Objects.equals(this.weather, other.weather)) {
            return false;
        }
        if (!Objects.equals(this.site, other.site)) {
            return false;
        }
        return Objects.equals(this.visibility, other.visibility);
    }

}
