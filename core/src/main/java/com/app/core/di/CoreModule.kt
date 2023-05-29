package com.app.core.di

import androidx.room.Room
import com.app.core.data.local.LocalDataSource
import com.app.core.data.local.room.EventDatabase
import com.app.core.data.repository.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    factory { get<EventDatabase>().eventDao() }
    single {
        Room.databaseBuilder(
            androidContext(),
            EventDatabase::class.java, "event.db"
        ).fallbackToDestructiveMigration().build()
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { UserRepository() }
    single { OrganizationRepository() }
    single { EventRepository(get()) }
    single { GoalRepository() }
    single { ArticleRepository() }
    single { ProgramRepository() }
}