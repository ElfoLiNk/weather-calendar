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
package it.polimi.meteocal.ejb;

import it.polimi.meteocal.dto.EventDTO;
import it.polimi.meteocal.dto.ResultDTO;
import it.polimi.meteocal.exception.ErrorRequestException;
import java.util.List;
import javax.ejb.Local;

/**
 * Class that handle the event in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 *
 */
@Local
public interface HandleEvent {

    /**
     * Method that create a new event in the DB setting the owner the user and
     * adding it to the user list of owned event.
     *
     * @param userID the user id that create the new event
     * @param event the new event to create in the DB
     * @return the id of the new event in the DB
     * @throws ErrorRequestException if the user dosen't exists or the invited
     * user to the event doesn't exist.
     */
    long addEvent(Long userID, EventDTO event) throws ErrorRequestException;

    /**
     * Method that updates the event of the user modifing the information of the
     * event in the DB
     *
     * @param userID the user id of the logged user
     * @param event the updated event
     * @return the id of the updated event
     * @throws ErrorRequestException
     */
    long updateEvent(Long userID, EventDTO event) throws ErrorRequestException;

    /**
     * Method that returns all the event of the user
     *
     * @param userID the id of the user to retrive the events
     * @return the list of the events organized or participated by the user
     * @throws ErrorRequestException
     */
    List<EventDTO> getEvents(Long userID) throws ErrorRequestException;

    /**
     * Method that return the event corrisponding the given event id
     *
     * @param userID the id of the logged user
     * @param eventId the id of the event to retrive in the DB
     * @return EventDTO the event information
     * @throws ErrorRequestException if the event doesn't exist
     */
    EventDTO getEvent(Long userID, String eventId) throws ErrorRequestException;

    /**
     * Method that safetly remove the event from the DB
     *
     * @param userID the id of the logged user (EO)
     * @param event the event to be removed
     * @throws ErrorRequestException if there is error in the remove procedure
     */
    void removeEvent(Long userID, EventDTO event) throws ErrorRequestException;

    /**
     * Method that cancel the user parteciaption to the event
     *
     * @param userID the id of the logged user (EP)
     * @param event the event to cancel the partecipation
     * @throws ErrorRequestException
     */
    void cancelEvent(Long userID, EventDTO event) throws ErrorRequestException;

    /**
     * Method that search the public event that match the given query on event's
     * name.
     *
     * @param query
     * @return the list of the result that match the query
     */
    List<ResultDTO> search(String query);

    /**
     * Method that move the event using the deltas
     *
     * @param id the event id to be moved
     * @param dayDelta the delta of the day modification
     * @param minuteDelta the delta of the munute modification
     * @throws ErrorRequestException
     */
    void moveEvent(String id, int dayDelta, int minuteDelta) throws ErrorRequestException;

    /**
     * Method that add the selected user to the invited user of the event
     *
     * @param eventId the id of the event to add the user
     * @param selectedResult the user result of the selection of the logged user
     * @throws ErrorRequestException
     */
    void addParticipant(String eventId, ResultDTO selectedResult) throws ErrorRequestException;

    /**
     * Method that check all the event of the logged user from today to three
     * days and send the need notification to the EO in case of bad weather
     * condition
     *
     * @param userId the id of the user to check the event
     */
    void checkEventWeatherCondition(long userId);

    /**
     * Method that resize the event using the deltas
     *
     * @param id the event id to be moved
     * @param dayDelta the delta of the day modification
     * @param minuteDelta the delta of the munute modification
     * @throws ErrorRequestException
     */
    void resizeEvent(String id, int dayDelta, int minuteDelta) throws ErrorRequestException;
}
