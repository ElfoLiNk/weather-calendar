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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Entity that rappresent the Notification of the User in MeteoCal
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Entity
@Table(name = "notification")
@NamedQueries({
    @NamedQuery(name = Notification.FIND_BY_USER, query = "SELECT n FROM Notification n WHERE n.user = :user"),})
public abstract class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Name for the NamedQuery
     */
    public static final String FIND_BY_USER = "Notification.FIND_BY_USER";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @NotNull
    private User user;

    /**
     *
     * @return the user of the notification
     */
    public User getUser() {
        return user;
    }

    /**
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    @Column(length = 500)
    private String message;

    /**
     *
     * @return the message of the notification
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return the id of the notification
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

}
