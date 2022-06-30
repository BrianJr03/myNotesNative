package jr.brian.mynotesnative.notes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val title: String,
    val body: String,
    val date: String,
    val passcode: String,
    val bodyFontSize: Double = 11.0,
    val textColor: Int,
    val isStarred: Boolean,
    val isLocked: Boolean,
    val index: Int
) : Parcelable