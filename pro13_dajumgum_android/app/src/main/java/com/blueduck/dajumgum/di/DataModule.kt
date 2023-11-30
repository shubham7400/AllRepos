package com.blueduck.dajumgum.di

import android.content.Context
import androidx.room.Room
import com.blueduck.dajumgum.DajumgumApp
import com.blueduck.dajumgum.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent



/**
 * SharedModule class providing Dagger Hilt dependency injection module for shared dependencies.
 * This module provides dependencies related to TagDatabase, TagDao, FirebaseService, FirebaseAuth, and FirebaseFirestore.
 */

@InstallIn(ViewModelComponent::class)
@Module
class SharedModule {
    @Provides
    fun provideFirebaseService(firestore: FirebaseFirestore): FirebaseService {
        return FirebaseService(firestore)
    }

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

}