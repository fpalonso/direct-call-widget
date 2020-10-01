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

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.blaxsoftware.directcallwidget.R
import com.blaxsoftware.directcallwidget.databinding.FragmentWidgetConfigBinding
import com.blaxsoftware.directcallwidget.viewmodel.ViewModelFactory
import com.blaxsoftware.directcallwidget.viewmodel.WidgetConfigViewModel

@Suppress("unused")
class WidgetConfigFragment : Fragment() {

    private val viewModel by activityViewModels<WidgetConfigViewModel> {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val pickImage = registerForActivityResult(GetContent()) { uri: Uri? ->
        viewModel.onPictureSelected(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentWidgetConfigBinding.inflate(inflater)
        with(binding) {
            viewModel = this@WidgetConfigFragment.viewModel
            lifecycleOwner = this@WidgetConfigFragment.viewLifecycleOwner

            topAppBar.setNavigationOnClickListener {
                this@WidgetConfigFragment.viewModel.onCancel()
            }

            topAppBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.ok -> {
                        this@WidgetConfigFragment.viewModel.onAccept()
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }

            changePictureButton.setOnClickListener {
                pickImage.launch("image/*")
            }

            return root
        }
    }
}
