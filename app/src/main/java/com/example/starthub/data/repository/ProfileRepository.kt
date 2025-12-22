package com.example.starthub.data.repository

import android.content.Context
import com.example.starthub.data.local.database.ProjectDatabase
import com.example.starthub.data.local.model.ProfileEntity
import com.example.starthub.data.remote.dto.UserProfileDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(private val context: Context) {
    private val profileDao = ProjectDatabase.getDatabase(context).profileDao()
    private val gson = Gson()

    suspend fun saveProfile(dto: UserProfileDto) {
        profileDao.insertProfile(
            ProfileEntity(
                id = dto.id,
                first_name = dto.first_name,
                last_name = dto.last_name,
                description = dto.description,
                email = dto.email,
                picture = dto.picture,
                phone_numbers = gson.toJson(dto.phone_numbers)
            )
        )
    }

    fun getProfile(): Flow<UserProfileDto?> {
        return profileDao.getProfile().map { entity ->
            entity?.let {
                val phones = try {
                    gson.fromJson(it.phone_numbers, Array<String>::class.java).toList()
                } catch (e: Exception) {
                    emptyList()
                }
                UserProfileDto(
                    id = it.id,
                    first_name = it.first_name,
                    last_name = it.last_name,
                    description = it.description,
                    email = it.email,
                    picture = it.picture,
                    phone_numbers = phones
                )
            }
        }
    }
}
