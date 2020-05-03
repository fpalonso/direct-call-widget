package com.blaxsoftware.directcallwidget.ui

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.viewmodel.ViewModelFactory
import com.blaxsoftware.directcallwidget.viewmodel.WidgetConfigViewModel
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
