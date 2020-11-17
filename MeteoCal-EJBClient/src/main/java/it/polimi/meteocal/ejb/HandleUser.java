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

import it.polimi.meteocal.dto.EventNotificationDTO;
import it.polimi.meteocal.dto.NotificationDTO;
import it.polimi.meteocal.dto.ResultDTO;
import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.exception.ErrorRequestException;
import it.polimi.meteocal.util.Visibility;
import java.util.List;

import javax.ejb.Local;

/**
 * Class tha handle the user and user's notifications in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Local
public interface HandleUser {

    /**
     * Method that check the user credential and return the id of the logged
     * user otherwise throw an exception to signal an error in the
     * authentication phase.
     *
     * @param username Username of the user
     * @param password Password of the user
     * @return id of the logged user
     * @throws ErrorRequestException expection throw if errors in the
     * authentication phase
     */
    long checkAccessCredential(String username, String password)
            throws ErrorRequestException;

    /**
     * Method that find and return the data of the user id passed via parameter.
     *
     * @param userID id of the user connected to MeteoCal
     * @return UserDTO the user data object
     * @throws ErrorRequestException if there is a problem retriving the user
     * info
     */
    UserDTO getUser(long userID)
            throws ErrorRequestException;

    /**
     * Method that add a new user to the MeteoCal application
     *
     * @param newUser the new user to add in MeteoCal
     * @throws ErrorRequestException
     */
    void addUser(UserDTO newUser) throws ErrorRequestException;

    /**
     * Method that handle the login of the user in MeteoCal
     *
     * @param loginUser the user that whant to login in MeteoCal
     * @return true if the login process was succesful; false otherwise.
     */
    boolean doLogin(UserDTO loginUser);

    /**
     * Method that return the owner of a calendar
     *
     * @param calendarId the calendar id of the user returned
     * @return the owner of the calendar; null otherwise;
     */
    UserDTO getOwner(String calendarId);

    /**
     * Method that search the user with a PUBLIC Calendar that match the given
     * query
     *
     * @param query
     * @return the list of the result that match the query
     */
    List<ResultDTO> search(String query);

    /**
     * Method that search the user that match the given query
     *
     * @param query
     * @return the list of the result that match the query
     */
    List<ResultDTO> searchUser(String query);

    /**
     * Method that change the user settings
     *
     * @param loggedUser the user with the new settings
     * @throws ErrorRequestException
     */
    void changeSettings(UserDTO loggedUser) throws ErrorRequestException;

    /**
     * Method that return the visibility of the given calendar id
     *
     * @param calendarId
     * @return Visibility.PUBLIC or Visibility.PRIVATE for the given calendarId
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    Visibility getCalendarVisibility(String calendarId) throws ErrorRequestException;

    /**
     * Method that change the calendar visibility of the current logged user
     *
     * @param visibility the wanted visibility for the current logged calendar
     * user
     * @throws it.polimi.meteocal.exception.ErrorRequestException
     */
    void changeCalendarVisibility(Visibility visibility) throws ErrorRequestException;

    /**
     * Method that save the notification in the DB
     *
     * @param notification the notification to save in the DB
     * @return true if the notification is added, false otherwise
     */
    boolean addNotification(EventNotificationDTO notification);

    /**
     * Method that handle the user accepts to a notification
     *
     * @param selectedNotification the notification that the user have accepted
     */
    void acceptNotification(NotificationDTO selectedNotification);

    /**
     * Method that handle the user declines to a notification
     *
     * @param selectedNotification the notification that the user have declined
     */
    void declineNotification(NotificationDTO selectedNotification);

    /**
     * Method that add the calendar to the logged user prefered
     *
     * @param calendarId the id of the prefered calendar to add
     */
    void addPreferedCalendar(String calendarId);

    /**
     * Method that remove the calendar from the logged user prefered
     *
     * @param calendarId the id of the prefered calendar to remove
     */
    void delPreferedCalendar(String calendarId);

    /**
     * Method that remove outdated notification from the user that refers to
     * passed event
     */
    void removeOldNotification();

}
