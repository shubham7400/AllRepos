package com.blueduck.annotator.di

import android.app.Application
import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.blueduck.annotator.R
import com.blueduck.annotator.data.googleauth.AuthRepository
import com.blueduck.annotator.data.googleauth.AuthRepositoryImpl
import com.blueduck.annotator.data.local.MyFileDatabase
import com.blueduck.annotator.data.network.NetworkRepository
import com.blueduck.annotator.data.network.NetworkRepositoryImpl
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOneTapClient(@ApplicationContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    fun provideSignInRequest(app: Application): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(app.getString(R.string.web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()
    }

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseStorageDatabase(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun providesRepositoryImpl(database: FirebaseFirestore, firebaseAuth: FirebaseAuth, signInClient: SignInClient, beginSignInRequest: BeginSignInRequest,): AuthRepository {
        return AuthRepositoryImpl(database, firebaseAuth, signInClient, beginSignInRequest)
    }


    @Provides
    @Singleton
    fun providesNetworkRepositoryImpl(localDatabase: MyFileDatabase,  networkDatabase: FirebaseFirestore, context: Context): NetworkRepository {
        return NetworkRepositoryImpl(localDatabase, networkDatabase, context)
    }

}