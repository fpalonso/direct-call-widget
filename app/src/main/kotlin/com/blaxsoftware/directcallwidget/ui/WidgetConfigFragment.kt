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
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.analytics.Analytics
import com.blaxsoftware.directcallwidget.analytics.AnalyticsHelper
import com.blaxsoftware.directcallwidget.databinding.FragmentWidgetConfigBinding
import com.blaxsoftware.directcallwidget.viewmodel.WidgetConfigViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint

@Suppress("unused")
@AndroidEntryPoint
class WidgetConfigFragment : Fragment() {

    private val viewModel: WidgetConfigViewModel by activityViewModels()

    // TODO inject with hilt
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onAttach(context: Context) {
        super.onAttach(context)
        firebaseAnalytics = AnalyticsHelper(context).firebaseAnalytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentWidgetConfigBinding.inflate(inflater)
        with(binding) {
            viewModel = this@WidgetConfigFragment.viewModel
            lifecycleOwner = this@WidgetConfigFragment.viewLifecycleOwner

            topAppBar.setNavigationOnClickListener {
                firebaseAnalytics.logEvent(Analytics.Event.CANCEL_SETUP, null)
                this@WidgetConfigFragment.viewModel.onCancel()
            }

            topAppBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.ok -> {
                        firebaseAnalytics.logEvent(Analytics.Event.SAVE_WIDGET, null)
                        this@WidgetConfigFragment.viewModel.onAccept()
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }

            changePictureButton.setOnClickListener {
                firebaseAnalytics.logEvent(Analytics.Event.CHANGE_PICTURE_CLICK, null)
                ChangePictureOptionsDialog().show(parentFragmentManager, "changePictureOptions")
            }

            return root
        }
    }

    companion object {
        const val TAG = "WidgetConfigFragment"
    }
}

class ChangePictureOptionsDialog : DialogFragment() {

    interface ChangePictureListener {
        fun onTakePictureClick()
        fun onPickImageFromGalleryClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.change_picture)
                .setItems(R.array.change_picture_options) { _, which ->
                    (activity as? ChangePictureListener)?.let {
                        when (which) {
                            TAKE_PICTURE_INDEX -> it.onTakePictureClick()
                            PICK_FROM_GALLERY_INDEX -> it.onPickImageFromGalleryClick()
                        }
                    }
                }
                .create()
    }

    companion object {
        const val TAKE_PICTURE_INDEX = 0
        const val PICK_FROM_GALLERY_INDEX = 1
    }
}