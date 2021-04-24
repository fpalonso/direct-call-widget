/*
 * Direct Call Widget - The widget that makes contacts accessible
 * Copyright (C) 2021 Fer P. A.
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

package dev.fpral.modelresolver

import android.database.MatrixCursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.fpral.modelresolver.annotations.CursorColumn
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CursorExtTest {

    private val testObjectCursor = MatrixCursor(arrayOf(
            "STRING", "INT", "DOUBLE", "FLOAT", "LONG", "SHORT"
    )).apply {
        addRow(arrayOf("a string", 123, 218.999999991, 312.17f, 87L, 93))
    }

    private val usersCursor = MatrixCursor(arrayOf(
            "NAME", "AGE", "PREF_LANG"
    )).apply {
        addRow(arrayOf("Alice", 31, "Dart"))
        addRow(arrayOf("Bob", 35, "Kotlin"))
        addRow(arrayOf("Charlie", 29, "Go"))
    }

    @Test
    fun get_returnsColumnValue() {
        with(testObjectCursor) {
            moveToFirst()
            val string: String? = get("STRING")
            val int: Int? = get("INT")
            val double: Double? = get("DOUBLE")
            val float: Float? = get("FLOAT")
            val long: Long? = get("LONG")
            val short: Short? = get("SHORT")

            Assert.assertEquals("a string", string)
            Assert.assertEquals(123, int)
            Assert.assertEquals(218.999999991, double)
            Assert.assertEquals(312.17f, float)
            Assert.assertEquals(87L, long)
            Assert.assertEquals(93.toShort(), short)
        }
    }

    @Test
    fun read_returnsObject() {
        testObjectCursor.moveToFirst()
        val testObject = testObjectCursor.read<TestObject>()

        val expectedObject = TestObject(
                string = "a string",
                int = 123,
                double = 218.999999991,
                float = 312.17f,
                long = 87L,
                short = 93
        )
        Assert.assertEquals(expectedObject, testObject)
    }

    @Test
    fun getAll_returnsAllObjects() {
        val users = usersCursor.getAll<User>()

        Assert.assertEquals(3, users.size)
        Assert.assertEquals(User("Alice", 31, "Dart"), users[0])
        Assert.assertEquals(User("Bob", 35, "Kotlin"), users[1])
        Assert.assertEquals(User("Charlie", 29, "Go"), users[2])
    }
}

data class TestObject(
        @CursorColumn("STRING") var string: String? = null,
        @CursorColumn("INT") var int: Int? = null,
        @CursorColumn("DOUBLE") var double: Double? = null,
        @CursorColumn("FLOAT") var float: Float? = null,
        @CursorColumn("LONG") var long: Long? = null,
        @CursorColumn("SHORT") var short: Short? = null
)

data class User(
        @CursorColumn("NAME") var name: String? = null,
        @CursorColumn("AGE") var age: Int? = null,
        @CursorColumn("PREF_LANG") var preferredLang: String? = null
)