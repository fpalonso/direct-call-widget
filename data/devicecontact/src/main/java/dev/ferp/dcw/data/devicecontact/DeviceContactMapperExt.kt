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

package dev.ferp.dcw.data.devicecontact

import dev.ferp.dcw.core.domain.data.PhoneType
import dev.ferp.dcw.core.domain.data.devicecontact.DeviceContact
import dev.ferp.dcw.core.domain.data.devicecontact.DevicePhone
import dev.ferp.dcw.data.devicecontact.source.local.LocalContact
import dev.ferp.dcw.data.devicecontact.source.local.LocalPhone
import dev.ferp.dcw.data.devicecontact.source.local.LocalPhoneType

internal fun LocalContact.toDomain() = DeviceContact(
    displayName = displayName,
    pictureUri = pictureUri,
    phones = phones.toDomain()
)

private fun LocalPhone.toDomain() = DevicePhone(
    number = number,
    type = type.toDomain()
)

private fun List<LocalPhone>.toDomain() = map {
    localPhone -> localPhone.toDomain()
}

private fun LocalPhoneType.toDomain() = when (this) {
    LocalPhoneType.MOBILE -> PhoneType.MOBILE
    LocalPhoneType.HOME -> PhoneType.HOME
    LocalPhoneType.UNKNOWN -> PhoneType.UNKNOWN
}