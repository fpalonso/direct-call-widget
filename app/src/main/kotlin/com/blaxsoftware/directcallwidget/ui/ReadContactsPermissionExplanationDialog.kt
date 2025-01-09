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

package com.blaxsoftware.directcallwidget.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.blaxsoftware.directcallwidget.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A dialog informing the user why they need to grant the READ_CONTACT permission.
 */
class ReadContactsPermissionExplanationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.request_readContacts_permission_explanation)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    (activity as? Callback)?.onReadContactExplanationClosed()
                }
                .create()
    }

    interface Callback {
        fun onReadContactExplanationClosed()
    }
}
