package dev.ferp.dcw.core.androidutil

import android.database.MatrixCursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CursorExtTest {

    private lateinit var cursor: MatrixCursor

    @Before
    fun setUp() {
        cursor = MatrixCursor(arrayOf("col1", "col2", "col3"))
        cursor.addRow(arrayOf("val1", "val2", 3))
        cursor.moveToFirst()
    }

    @Test
    fun `getStringOrNull returns string if column exists`() {
        assertThat(cursor.getString("col2")).isEqualTo("val2")
    }

    @Test
    fun `getStringOrNull returns null if column does not exist`() {
        assertThat(cursor.getString("col4")).isNull()
    }

    @Test
    fun `getIntOrNull returns Int if column exists`() {
        assertThat(cursor.getInt("col3")).isEqualTo(3)
    }

    @Test
    fun `getIntOrNull returns null if column does not exist`() {
        assertThat(cursor.getInt("col4")).isNull()
    }
}