package com.ibd.dcdown.hilt

import com.ibd.dcdown.login.repository.LoginRepository
import com.ibd.dcdown.login.repository.LoginRepositoryImpl
import com.ibd.dcdown.main.repository.ExternalStorageRepository
import com.ibd.dcdown.main.repository.ExternalStorageRepositoryImpl
import com.ibd.dcdown.repository.ConRepository
import com.ibd.dcdown.repository.ConRepositoryImpl
import com.ibd.dcdown.repository.DataStoreRepository
import com.ibd.dcdown.repository.DataStoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun provideDataStoreRepositoryImpl(repositoryImpl: DataStoreRepositoryImpl): DataStoreRepository

    @Binds
    fun provideConRepositoryImpl(repositoryImpl: ConRepositoryImpl): ConRepository

    @Binds
    fun provideExternalStorageRepositoryImpl(repositoryImpl: ExternalStorageRepositoryImpl): ExternalStorageRepository

    @Binds
    fun provideLoginRepositoryImpl(repositoryImpl: LoginRepositoryImpl): LoginRepository
}