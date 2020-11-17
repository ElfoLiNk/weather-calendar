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
package it.polimi.meteocal.web;

import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.ejb.HandleUser;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 * Class that handle the login and registration dialog in the index of the web
 * site
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Named
@RequestScoped
public class RegistrationBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(RegistrationBean.class.getName());

    @EJB
    private HandleUser handleUser;

    private UserDTO registerUser;

    private String passwordCheck;

    /**
     *
     * @return the confirm password 
     */
    public String getPasswordCheck() {
        return passwordCheck;
    }

    /**
     *
     * @param passwordCheck the confirm password to set
     */
    public void setPasswordCheck(String passwordCheck) {
        this.passwordCheck = passwordCheck;
    }

    /**
     * Creates a new instance of RegistrationBean
     */
    public RegistrationBean() {

    }

    /**
     *
     * @return the registered user
     */
    public UserDTO getRegisterUser() {
        return registerUser;
    }

    /**
     *
     * @param registerUser the registered user to set
     */
    public void setRegisterUser(UserDTO registerUser) {
        this.registerUser = registerUser;
    }

    /**
     * PostConstruct to initialize the class
     */
    @PostConstruct
    public void dialogPostConstruct() {
        registerUser = new UserDTO();
    }

    /**
     * Method that register the user in the system
     */
    public void doRegister() {
        boolean registered = false;
        try {
            LOGGER.log(Level.INFO, "Register User " + registerUser);
            handleUser.addUser(registerUser);
            String msg = "User Registered successfully";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
            registerUser = new UserDTO();
            registered = true;
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, e);
            String msg = e.getMessage();
            String componentId = "registrationFormDlg";
            FacesContext.getCurrentInstance().addMessage(componentId,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
        }
        PrimeFaces.current().ajax().addCallbackParam("registered", registered);
    }

    /**
     * Method that show a message to the user with the selected date
     * 
     * @param event the select event of the user
     */
    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

}
