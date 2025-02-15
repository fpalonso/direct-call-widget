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

package dev.ferp.dcw.data.phones

import dev.ferp.dcw.core.di.qualifiers.DeviceDataSource
import dev.ferp.dcw.data.phones.source.PhoneDataSource
import javax.inject.Inject

class DevicePhoneRepository @Inject constructor(
    @DeviceDataSource private val dataSource: PhoneDataSource
) : PhoneRepository {

    override suspend fun getPhoneListByLookUpKey(lookUpKey: String): List<Phone> {
        return try {
            dataSource.getPhoneList(lookUpKey)
        } catch (e: Throwable) {
            emptyList()
        }
    }
}