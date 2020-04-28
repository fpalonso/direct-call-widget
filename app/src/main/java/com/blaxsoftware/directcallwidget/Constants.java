/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2020 Fer P. A.
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.blaxsoftware.directcallwidget;

public class Constants {

    // shared preferences constants
    public static final String SHAREDPREF_WIDGET = "widget_data";
    public static final String SHAREDPREF_WIDGET_DISPLAY_NAME = "name_";
    public static final String SHAREDPREF_WIDGET_PHONE = "phone_";
    public static final String SHAREDPREF_WIDGET_PHONE_TYPE = "phone_type_";
    public static final String SHAREDPREF_WIDGET_PHOTO_URL = "pic_";

    public static final String PICTURES_DIRECTORY = "pics";

    public static final int REQUEST_READ_CONTACTS_PERMISSION = 0;
    public static final int REQUEST_CALL_PERMISSION = 1;
    public static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 2;
}
