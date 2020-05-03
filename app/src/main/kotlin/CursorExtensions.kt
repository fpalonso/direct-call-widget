import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri

fun Cursor.getString(columnName: String): String? {
    return getString(getColumnIndex(columnName))
}

fun Cursor.getUri(columnName: String): Uri? {
    return getString(columnName)?.toUri()
}

fun Cursor.getInt(columnName: String): Int? {
    return getInt(getColumnIndex(columnName))
}
