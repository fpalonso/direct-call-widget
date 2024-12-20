/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2024 Fer P. A.
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

package com.blaxsoftware.directcallwidget.analytics

object Analytics {

    object Event {
        // Call
        const val START_CALL = "start_call"

        // Permissions
        const val GRANT_CALL_PERMISSION = "grant_call_permission"
        const val DENY_CALL_PERMISSION = "deny_call_permission"
        const val GRANT_CONTACT_PERMISSION = "grant_contact_permission"
        const val DENY_CONTACT_PERMISSION = "deny_contact_permission"

        // Settings
        const val SETTING_ON_TAP_CLICK = "setting_on_tap_click"
        const val SETTING_ON_TAP_CHANGED = "setting_on_tap_changed"
        const val SETTING_BETA_CLICK = "setting_beta_click"
        const val SETTING_CONTRIBUTE_CLICK = "setting_beta_click"

        // Setup
        const val PICK_CONTACT = "pick_contact"
        const val PICK_CONTACT_CANCEL = "pick_contact_cancel"
        const val CHANGE_PICTURE_CLICK = "change_picture_click"
        const val TAKE_PICTURE_CLICK = "take_picture_click"
        const val PICK_IMAGE_CLICK = "take_picture_click"
        const val PICK_IMAGE = "pick_image"
        const val PICK_IMAGE_CANCEL = "pick_image_cancel"
        const val TAKE_PICTURE = "take_picture"
        const val TAKE_PICTURE_CANCEL = "take_picture_cancel"
        const val CANCEL_SETUP = "cancel_setup"
        const val SAVE_WIDGET = "save_widget"
        const val WIDGET_CLICK = "widget_click"
    }

    object Param {
        const val ACTION = "action"
    }

    object ParamValue {
        const val SETTING_ON_TAP_DIAL = "dial"
        const val SETTING_ON_TAP_CALL = "call"
        const val ACTION_DIAL = "dial"
        const val ACTION_CALL = "call"
    }
}