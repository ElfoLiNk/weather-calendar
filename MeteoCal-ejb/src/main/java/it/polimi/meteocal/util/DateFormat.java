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
package it.polimi.meteocal.util;

/**
 * Enum that rappresent the dateformat of the user date in MeteoCal.
 * 
 * DMY : Date format = "dd/MM/yyyy", Output = 20/02/2005.
 * MDY : Date format = "MM/dd/yyyy", Output = 02/20/2005.
 * YMD : Date format = "yyyy/MM/dd", Output = 2005/02/20.
 *
 * @author Matteo Gazzetta, Alessandro Fato
 */
public enum DateFormat {

    /**
     * DMY : Date format = "dd/MM/yyyy"; Output = 20/02/2005.
     */
    DMY,
    /**
     * MDY : Date format = "MM/dd/yyyy"; Output = 02/20/2005.
     */
    MDY,
    /**
     * YMD : Date format = "yyyy/MM/dd"; Output = 2005/02/20.
     */
    YMD
}
