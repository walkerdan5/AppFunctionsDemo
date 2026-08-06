package com.mantelgroup.appfunctionsdemo.di

import com.mantelgroup.appfunctionsdemo.data.repository.CartRepository
import com.mantelgroup.appfunctionsdemo.data.repository.DefaultCartRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: DefaultCartRepository
    ): CartRepository
}
