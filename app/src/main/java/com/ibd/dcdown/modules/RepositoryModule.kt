package com.ibd.dcdown.modules

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
    fun provideDataStoreRepositoryImpl(repository: DataStoreRepositoryImpl): DataStoreRepository
    @Binds
    fun provideConRepositoryImpl(repositoryImpl: ConRepositoryImpl): ConRepository
}