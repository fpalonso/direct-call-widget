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

import android.content.Context
import android.util.Log
import com.blaxsoftware.directcallwidget.R
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class AnalyticsHelper(context: Context) {

    val firebaseAnalytics by lazy {
        Firebase.analytics.apply {
            val collectAnalytics = context.resources.getBoolean(R.bool.collect_analytics)
            Log.d(TAG, "firebaseAnalytics: collecting analytics: $collectAnalytics")
            setAnalyticsCollectionEnabled(collectAnalytics)
        }
    }

    companion object {
        const val TAG = "AnalyticsHelper"
    }
}