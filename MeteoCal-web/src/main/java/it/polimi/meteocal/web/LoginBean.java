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
import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.context.RequestContext;

/**
 * Class that handle the login and registration dialog in the index of the web
 * site
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Named
@RequestScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LogManager.getLogger(LoginBean.class.getName());

    @EJB
    private HandleUser handleUser;

    private UserDTO loginUser;

    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {

    }

    /**
     *
     * @return the login user
     */
    public UserDTO getLoginUser() {
        return loginUser;
    }

    /**
     *
     * @param loginUser the login user to set
     */
    public void setLoginUser(UserDTO loginUser) {
        this.loginUser = loginUser;
    }

    /**
     * PostConstruct that initialize the class
     */
    @PostConstruct
    public void dialogPostConstruct() {
        loginUser = new UserDTO();
    }

    /**
     * Method that login the user in the system
     *
     */
    public void doLogin() {
        RequestContext context = RequestContext.getCurrentInstance();
        LOGGER.log(Level.INFO, "Login User " + loginUser);
        boolean loggedIn = handleUser.doLogin(loginUser);
        loginUser = new UserDTO();
        if (loggedIn) {
            try {
                context.addCallbackParam("loggedIn", loggedIn);
                FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("calendar/calendar.xhtml");
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, e);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Redirect Failed"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Loggin Error", "Invalid credentials"));
        }
        context.addCallbackParam("loggedIn", loggedIn);
    }

    /**
     * Method that invalidate the user session
     * 
     * @return redirect to index
     */
    public String logout() {
        LOGGER.log(Level.INFO, "LOGOUT");
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index.xhtml?faces-redirect=true";
    }

    /**
     * Redirect method
     * 
     * @param outcome where redirects
     */
    public void redirect(String outcome) {
         try {
            FacesContext.getCurrentInstance().getExternalContext()
                    .redirect(outcome);
        } catch (IOException ex) {
            LOGGER.log(Level.ERROR, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Logout Error", "Redirect Failed"));

        }
    }

}
