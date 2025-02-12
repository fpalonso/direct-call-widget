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

@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
include(":data:contacts:api")
include(":data:contacts:impl:default")
include(":data:phones:api")
include(":data:phones:impl:device")
include(":data:pictures:api")
include(":data:pictures:impl:default")
include(":data:pictures:impl:mock")
include(":data:onecontactwidget:api")
include(":data:onecontactwidget:impl:default")
include(":data:onecontactwidget:source:sharedprefs:api")
include(":data:onecontactwidget:source:sharedprefs:impl:default")
include(":data:onecontactwidget:source:sharedprefs:impl:mock")
include(":core:di")
include(":core:util")
include(":core:androidutil")
include(":core:analytics")
