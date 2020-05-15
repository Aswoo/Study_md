
package com.test.testingsample

import android.content.SharedPreferences
import java.util.Calendar

/**
 * Helper class to manage access to [SharedPreferences].
 *
 * @param sharedPreferences The injected [SharedPreferences] that will be used in this DAO.
 */
class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) {

    /**
     * Retrieves the [SharedPreferenceEntry] containing the user's personal information from
     * [SharedPreferences].
     *
     * @return the Retrieved [SharedPreferenceEntry].
     */
    // Get data from the SharedPreferences.
    // Create and fill a SharedPreferenceEntry model object.
    fun getPersonalInfo(): SharedPreferenceEntry {
        val name = sharedPreferences.getString(KEY_NAME, "")
        val dobMillis = sharedPreferences.getLong(KEY_DOB, Calendar.getInstance().timeInMillis)
        val dateOfBirth = Calendar.getInstance().apply { timeInMillis = dobMillis }
        val email = sharedPreferences.getString(KEY_EMAIL, "")
        return SharedPreferenceEntry(name, dateOfBirth, email)
    }

    /**
     * Saves the given [SharedPreferenceEntry] that contains the user's settings to
     * [SharedPreferences].
     *
     * @param sharedPreferenceEntry contains data to save to [SharedPreferences].
     * @return `true` if writing to [SharedPreferences] succeeded, `false` otherwise.
     */
    fun savePersonalInfo(sharedPreferenceEntry: SharedPreferenceEntry): Boolean {
        // Start a SharedPreferences transaction.
        val editor = sharedPreferences.edit().apply() {
            putString(KEY_NAME, sharedPreferenceEntry.name)
            putLong(KEY_DOB, sharedPreferenceEntry.dateOfBirth.timeInMillis)
            putString(KEY_EMAIL, sharedPreferenceEntry.email)
        }

        // Commit changes to SharedPreferences.
        return editor.commit()
    }

    companion object {
        // Keys for saving values in SharedPreferences.
        internal val KEY_NAME = "key_name"
        internal val KEY_DOB = "key_dob_millis"
        internal val KEY_EMAIL = "key_email"
    }
}
