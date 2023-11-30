package com.blueduck.easydentist.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.blueduck.easydentist.model.AppUser
import com.blueduck.easydentist.network.NetworkRepository
import com.blueduck.easydentist.network.NetworkRepositoryImpl
import com.blueduck.easydentist.preferences.getUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


// This code defines a Dagger module called AppModule, which provides various instances of Firebase services that can be injected into other classes in the app.
// The @InstallIn(SingletonComponent::class) annotation specifies that the dependencies provided by this module will be available for the entire lifetime of the app, and will be instantiated only once.
// The @Singleton annotation is used to ensure that only one instance of each service is created and shared among all the objects that require it.

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    // The provideFirebaseStorage() function provides an instance of FirebaseStorage
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    // provideFirestore() function provides an instance of FirebaseFirestore
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


    // provideFirebaseAuth() function provides an instance of FirebaseAuth.
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()


    // Lastly, the providesNetworkRepositoryImpl() function provides an instance of the NetworkRepository interface which is implemented by NetworkRepositoryImpl class, and requires an instance of FirebaseFirestore which is passed as an argument.
    @Provides
    @Singleton
    fun providesNetworkRepositoryImpl( networkDatabase: FirebaseFirestore): NetworkRepository {
        return NetworkRepositoryImpl(networkDatabase)
    }
}
