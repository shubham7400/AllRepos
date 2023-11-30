package com.blueduck.dajumgum.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.blueduck.dajumgum.model.User
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.CATEGORY_TAGS
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.ID
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.INSPECTION_TAGS
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.KEY_TIMESTAMP
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.POSITION_TAGS
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.TEMPERATURE_TAGS
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.USER_DOB
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.USER_EMAIL
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.USER_NAME
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.USER_PHONE_NUMBER
import com.blueduck.dajumgum.preferences.DataStoreManager.PreferencesKeys.USER_TITLE
import com.blueduck.dajumgum.preferences.DataStoreManager.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

/**
 * DataStoreManager object providing functions to manage and access data stored in DataStore.
 */

object DataStoreManager {
    // DataStore instance for preferences
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "local_datastore")

    // Saves the login status to DataStore.
    suspend fun saveLoginStatus(context: Context, isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    // Retrieves the login status from DataStore.
    fun isLoggedIn(context: Context): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }


    // Saves the status of tags fetched before to DataStore.
    suspend fun setTagsFetchedBefore(context: Context, status: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_TAGS_FETCHED_BEFORE] = status
        }
    }

    // Retrieves the status of tags fetched before from DataStore.
    fun getTagsFetchedBefore(context: Context): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.KEY_TAGS_FETCHED_BEFORE] ?: false
    }

    // Saves a timestamp to DataStore.
    suspend fun saveTimestamp(context: Context, timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TIMESTAMP] = timestamp
        }
    }

    // Retrieves a timestamp from DataStore.
    fun getTimestamp(context: Context): Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_TIMESTAMP] ?: 0
    }

    // Saves user information to DataStore.
    suspend fun saveAppUser(context: Context, user: User) {
        context.dataStore.edit { preferences ->
            preferences[ID] = user.id
            preferences[USER_NAME] = user.name
            preferences[USER_EMAIL] = user.email
            preferences[USER_PHONE_NUMBER] = user.phone
            preferences[USER_DOB] = user.dob.toString()
            preferences[USER_TITLE] = user.title
        }
    }

    // Retrieves user information from DataStore.
    fun getUser(context: Context): Flow<User> = context.dataStore.data
        .map { preferences ->
            val id = preferences[ID] ?: ""
            val name = preferences[USER_NAME] ?: ""
            val email = preferences[USER_EMAIL] ?: ""
            val phone = preferences[USER_PHONE_NUMBER] ?: ""
            val dob = preferences[USER_DOB] ?: "0"
            val title = preferences[USER_TITLE] ?: ""
            //val customers = preferences[USER_CUSTOMER] ?: ""
            User(id, name, email,phone, dob.toLong(), title/*, arrayListOf()*/)
        }


    suspend fun saveCategoryTag(context: Context, newTag: String) {
        context.dataStore.edit { preferences ->
            val tags = preferences[CATEGORY_TAGS] ?: emptySet()
            if (!tags.contains(newTag)){
                preferences[CATEGORY_TAGS] = tags + newTag
            }
        }
    }
    fun getCategoryTags(context: Context): Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[CATEGORY_TAGS] ?: emptySet()
        }
        .map { stringSet ->
            stringSet.toList()
        }

    suspend fun savePositionTag(context: Context, newTag: String) {
        context.dataStore.edit { preferences ->
            val tags = preferences[POSITION_TAGS] ?: emptySet()
            if (!tags.contains(newTag)){
                preferences[POSITION_TAGS] = tags + newTag
            }
        }
    }
    fun getPositionTags(context: Context): Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[POSITION_TAGS] ?: emptySet()
        }
        .map { stringSet ->
            stringSet.toList()
        }

    suspend fun saveTemperatureTag(context: Context, newTag: String) {
        context.dataStore.edit { preferences ->
            val tags = preferences[TEMPERATURE_TAGS] ?: emptySet()
            if (!tags.contains(newTag)){
                preferences[TEMPERATURE_TAGS] = tags + newTag
            }
        }
    }
    fun getTemperatureTags(context: Context): Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[TEMPERATURE_TAGS] ?: emptySet()
        }
        .map { stringSet ->
            stringSet.toList()
        }

    suspend fun saveInspectionTag(context: Context, newTag: String) {
        context.dataStore.edit { preferences ->
            val tags = preferences[INSPECTION_TAGS] ?: emptySet()
            if (!tags.contains(newTag)){
                preferences[INSPECTION_TAGS] = tags + newTag
            }
        }
    }
    fun getInspectionTags(context: Context): Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            preferences[INSPECTION_TAGS] ?: emptySet()
        }
        .map { stringSet ->
            stringSet.toList()
        }

    fun getStatusTags(context: Context): Flow<List<String>>  {
        return flowOf(listOf("점검", "재점검", "완료", "삭제"))
    }

    suspend fun deleteCategoryTag(context: Context, tag: String) {
        context.dataStore.edit { preferences ->
            val tags = preferences[CATEGORY_TAGS] ?: emptySet()
            if (tags.contains(tag)) {
                preferences[CATEGORY_TAGS] = tags - tag
            }
        }
    }

    suspend fun deletePositionTag(context: Context, tag: String) {
        context.dataStore.edit { preferences ->
            val tags = preferences[POSITION_TAGS] ?: emptySet()
            if (tags.contains(tag)) {
                preferences[POSITION_TAGS] = tags - tag
            }
        }
    }

    suspend fun deleteInspectionTag(context: Context, tag: String) {
        context.dataStore.edit { preferences ->
            val tags = preferences[INSPECTION_TAGS] ?: emptySet()
            if (tags.contains(tag)) {
                preferences[INSPECTION_TAGS] = tags - tag
            }
        }
    }

    suspend fun deleteTemperatureTag(context: Context, tag: String) {
        context.dataStore.edit { preferences ->
            val tags = preferences[TEMPERATURE_TAGS] ?: emptySet()
            if (tags.contains(tag)) {
                preferences[TEMPERATURE_TAGS] = tags - tag
            }
        }
    }


    suspend fun logout(context: Context){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
            preferences[CATEGORY_TAGS] = emptySet()
            preferences[TEMPERATURE_TAGS] = emptySet()
            preferences[INSPECTION_TAGS] = emptySet()
            preferences[POSITION_TAGS] = emptySet()
        }
    }


    // Object containing keys for preferences in DataStore.
    private object PreferencesKeys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_TAGS_FETCHED_BEFORE = booleanPreferencesKey("tags_fetched_before")
        val KEY_TIMESTAMP = longPreferencesKey("key_timestamp")
        val USER_NAME = stringPreferencesKey("user_name")
        val ID = stringPreferencesKey("user_id")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE_NUMBER = stringPreferencesKey("user_phone_number")
        val USER_DOB = stringPreferencesKey("user_dob")
        val USER_TITLE = stringPreferencesKey("user_title")
        val CATEGORY_TAGS = stringSetPreferencesKey("category_tags")
        val POSITION_TAGS = stringSetPreferencesKey("position_tags")
        val INSPECTION_TAGS = stringSetPreferencesKey("inspection_tags")
        val TEMPERATURE_TAGS = stringSetPreferencesKey("temperature_tags")
    }
}