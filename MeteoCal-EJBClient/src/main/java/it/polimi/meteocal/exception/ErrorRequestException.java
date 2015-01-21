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
package it.polimi.meteocal.exception;

/**
 * Exception to handle logic error in MeteoCal
 * 
 * @author Matteo Gazzetta, Alessandro Fato
 */
public class ErrorRequestException extends Exception {

    private static final long serialVersionUID = 1L;
    private final boolean manyRequest;

    /**
     * Default Constructor 
     * 
     * @param msg the message of the exception
     * @param manyRequest true if the user is doing to many request to the API or to the system, false otherwise
     */
    public ErrorRequestException(String msg, boolean manyRequest) {
        super(msg);
        this.manyRequest = manyRequest;
    }

    /**
     *
     * @return true if the user is doing to many request to the API or to the system, false otherwise
     */
    public boolean isLimitRequestExceeded() {
        return manyRequest;
    }

}
