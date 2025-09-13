package dev.ferp.dcw.core.analytics

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

interface ContactConfigLogger {
    // Feature
    fun logInit()
    fun logSave()
    fun logDismiss()

    // Contact picker
    fun logPickContactClick()
    fun logContactPermissionGranted()
    fun logContactPermissionDenied()
    fun logContactPickerLaunched()
    fun logContactPicked()
    fun logContactPickerDismissed()

    // Picture
    fun logPicturePickerLaunched()
    fun logPicturePicked()
    fun logPicturePickerDismissed()
}

internal class DefaultContactConfigLogger @Inject constructor(
    private val analytics: FirebaseAnalytics
) : ContactConfigLogger {

    override fun logInit() {
        val params = bundleOf(
            FirebaseAnalytics.Param.SCREEN_NAME to "Widget Setup",
            FirebaseAnalytics.Param.SCREEN_CLASS to "OneContactConfigScreen"
        )
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
    }

    override fun logSave() {
        analytics.logEvent("contact_config_save", null)
    }

    override fun logDismiss() {
        analytics.logEvent("contact_config_dismiss", null)
    }

    override fun logPickContactClick() {
        analytics.logEvent("pick_contact_click", null)
    }

    override fun logContactPermissionGranted() {
        analytics.logEvent("contact_permission_granted", null)
    }

    override fun logContactPermissionDenied() {
        analytics.logEvent("contact_permission_denied", null)
    }

    override fun logContactPickerLaunched() {
        analytics.logEvent("contact_picker_launched", null)
    }

    override fun logContactPicked() {
        analytics.logEvent("contact_picked", null)
    }

    override fun logContactPickerDismissed() {
        analytics.logEvent("contact_picker_dismissed", null)
    }

    override fun logPicturePickerLaunched() {
        analytics.logEvent("picture_picker_launched", null)
    }

    override fun logPicturePicked() {
        analytics.logEvent("picture_picked", null)
    }

    override fun logPicturePickerDismissed() {
        analytics.logEvent("picture_picker_dismissed", null)
    }
}