package com.example.wehearintershipwork.di

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.wehearintershipwork.R
import com.example.wehearintershipwork.firebase.FirebaseAuthSource
import com.example.wehearintershipwork.firebase.FirestoreUserSource
import com.example.wehearintershipwork.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuthSource():FirebaseAuthSource{
        return FirebaseAuthSource()
    }
    @Singleton
    @Provides
    fun provideFirestoreUserSource(): FirestoreUserSource {
        return FirestoreUserSource()
    }

    @ExperimentalCoroutinesApi
    @Singleton
    @Provides
    fun provideAuthRepository(
        firebaseAuthSource: FirebaseAuthSource,
        firestoreUserSource: FirestoreUserSource

    ): AuthRepository {
        return AuthRepository(firebaseAuthSource, firestoreUserSource)
    }

    @Singleton
    @Provides
    fun provideGlideRequestOptions(): RequestOptions {
        return RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .error(R.drawable.ic_launcher_background)
    }

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context,
        requestOptions: RequestOptions
    ): RequestManager {
        return Glide
            .with(context)
            .setDefaultRequestOptions(requestOptions)
    }


}