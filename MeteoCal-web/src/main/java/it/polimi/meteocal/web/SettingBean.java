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

import it.polimi.meteocal.auth.User;
import it.polimi.meteocal.dto.UserDTO;
import it.polimi.meteocal.ejb.HandleUser;
import it.polimi.meteocal.exception.ErrorRequestException;
import it.polimi.meteocal.util.Visibility;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.UploadedFile;

/**
 * Class that handle the settings page in the web site and the user settings
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@Named(value = "settingBean")
@RequestScoped
public class SettingBean {

    private static final Logger LOGGER = LogManager.getLogger(SettingBean.class.getName());

    private UserDTO loggedUser;
    private String newpassword;
    private String renewpassword;

    private Visibility calendarVisibility;

    private UploadedFile uploadedFile;

    @EJB
    HandleUser handleUser;

    /**
     * Creates a new instance of SettingBean
     */
    public SettingBean() {
    }

    /**
     *
     * @return the user calendar visibility
     */
    public Visibility getCalendarVisibility() {
        return calendarVisibility;
    }

    /**
     *
     * @param calendarVisibility the user calendar visibility to set
     */
    public void setCalendarVisibility(Visibility calendarVisibility) {
        this.calendarVisibility = calendarVisibility;
    }

    /**
     *
     * @return the new password of the user
     */
    public String getNewpassword() {
        return newpassword;
    }

    /**
     *
     * @param newpassword the newpassword to set
     */
    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    /**
     *
     * @return the confirm password of the new password
     */
    public String getRenewpassword() {
        return renewpassword;
    }

    /**
     *
     * @param renewpassword the confirm password to set
     */
    public void setRenewpassword(String renewpassword) {
        this.renewpassword = renewpassword;
    }

    /**
     *
     * @return the uploaded file of the user
     */
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     *
     * @param uploadedFile the uploaded file to set
     */
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    /**
     *
     * @return the logged user
     */
    public UserDTO getLoggedUser() {
        return loggedUser;
    }

    /**
     *
     * @param loggedUser the logged user to set
     */
    public void setLoggedUser(UserDTO loggedUser) {
        this.loggedUser = loggedUser;
    }

    /**
     *
     * @return
     */
    public HandleUser getHandleUser() {
        return handleUser;
    }

    /**
     *
     * @param handleUser
     */
    public void setHandleUser(HandleUser handleUser) {
        this.handleUser = handleUser;
    }

    /**
     * PostConstruct that initialize the class
     */
    @PostConstruct
    public void init() {
        // LOAD LOGGED USER 
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context
                .getExternalContext().getRequest();
        HttpSession session = request.getSession();

        User authUser = null;
        if (session != null) {
            authUser = (User) session.getAttribute(User.AUTH_KEY);
        } else {
            LOGGER.log(Level.ERROR, "no active user session");
            loggedUser = new UserDTO();
        }

        if (authUser != null) {
            try {
                LOGGER.log(Level.INFO, handleUser.getUser(
                        authUser.getUserID()).toString());
                loggedUser = handleUser.getUser(
                        authUser.getUserID());

            } catch (ErrorRequestException e) {
                LOGGER.log(Level.ERROR, e);
            }

        } else {
            LOGGER.log(Level.ERROR, "no user authUser == null");
            loggedUser = new UserDTO();
        }
        try {
            // GET CALENDAR VISIBILITY
            calendarVisibility = handleUser.getCalendarVisibility(loggedUser.getCalendarId());
        } catch (ErrorRequestException ex) {
            LOGGER.log(Level.ERROR, ex);
        }

        //SETTING TIME STAMP
        LOGGER.log(Level.INFO, loggedUser.getSetting().getTimeZone().getDisplayName());
    }

    /**
     * Method that change the setting of the user and his personal information
     */
    public void changeSetting() {
        long id = 0;
        try {
            // VALIDATE PASSWORD
            id = handleUser.checkAccessCredential(loggedUser.getId(), loggedUser.getPassword());
        } catch (ErrorRequestException ex) {
            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Error", ex.getMessage()));
            LOGGER.log(Level.ERROR, ex);
        }
        // UPLOAD IMAGE
        if (uploadedFile != null && id != 0 && uploadedFile.getSize() > 0) {
            String filename = FilenameUtils.getName(uploadedFile.getFileName());
            try {
                InputStream input = uploadedFile.getInputstream();
                File file = new File(System.getProperty("com.sun.aas.instanceRoot") + "/var/webapp/images", filename);
                OutputStream output = new FileOutputStream(file);
                LOGGER.log(Level.INFO, file.getAbsolutePath());
                loggedUser.setAvatar("/images/" + file.getName());
                IOUtils.copy(input, output);
                IOUtils.closeQuietly(input);
                IOUtils.closeQuietly(output);
            } catch (IOException ex) {
                LOGGER.log(Level.ERROR, ex);
            }

        } else {
            LOGGER.log(Level.ERROR, "IMAGE NULL");
        }
        if (id != 0) {
            // CHECK NEW PASSWORD
            if (newpassword != null && newpassword.equals(renewpassword)) {
                loggedUser.setPassword(newpassword);

            }
            try {
                handleUser.changeSettings(loggedUser);
                handleUser.changeCalendarVisibility(calendarVisibility);
            } catch (ErrorRequestException ex) {
                FacesContext.getCurrentInstance()
                        .addMessage(
                                null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                        "Error", ex.getMessage()));
                LOGGER.log(Level.ERROR, ex);
            }

            FacesContext.getCurrentInstance()
                    .addMessage(
                            null,
                            new FacesMessage(FacesMessage.SEVERITY_INFO,
                                    "Settings", "Sccessfully Updated"));
        }
    }

}
