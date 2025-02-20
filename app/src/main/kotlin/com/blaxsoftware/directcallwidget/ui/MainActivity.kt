/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2025 Fer P. A.
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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.blaxsoftware.directcallwidget.DirectCallWidgetApp
import com.blaxsoftware.directcallwidget.ui.theme.DirectCallWidgetTheme
import dagger.hilt.android.AndroidEntryPoint
import dev.ferp.dcw.core.androidutil.AndroidVersions
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var androidVersions: AndroidVersions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appVersionName = (application as DirectCallWidgetApp).appVersionName
            DirectCallWidgetTheme {
                if (androidVersions.isAtLeastO()) {
                    MainNavHost(
                        modifier = Modifier.fillMaxSize(),
                        startDestination = WidgetList,
                        appVersionName = appVersionName
                    )
                } else {
                    MainNavHost(
                        modifier = Modifier.fillMaxSize(),
                        startDestination = Settings(appVersionName),
                        appVersionName = appVersionName
                    )
                }
            }
        }
    }
}