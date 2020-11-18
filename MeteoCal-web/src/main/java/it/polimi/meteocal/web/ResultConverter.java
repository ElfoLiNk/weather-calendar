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

import it.polimi.meteocal.dto.ResultDTO;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Converter that converts the result of the search bar
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
@FacesConverter("resultConverter")
public class ResultConverter implements Converter<ResultDTO> {
    
    @Inject
    SearchBean searchBean;
    
    private static final Logger LOGGER = LogManager.getLogger(ResultConverter.class.getName());

    /**
     *
     * @param fc context
     * @param uic user interface component
     * @param value the id of the object
     * @return the corresponding object of the id
     */
    @Override
    public ResultDTO getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                LOGGER.log(Level.INFO, "CONVERTER VALUE TO GET OBJECT " + value);
                if ("searchBar".equals(uic.getId())) {
                    return searchBean.search(value).get(0);
                }
                if ("searchBarParticipant".equals(uic.getId())) {
                    return searchBean.searchUser(value).get(0);
                }

            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid result."));
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     *
     * @param fc context
     * @param uic user interface component
     * @param object the object
     * @return the name of the object
     */
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, ResultDTO object) {
        if (object != null) {
            return String.valueOf((object).getName());
        } else {
            return null;
        }
    }
}
