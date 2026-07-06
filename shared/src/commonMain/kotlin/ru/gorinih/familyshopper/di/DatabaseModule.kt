package ru.gorinih.familyshopper.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.koin.dsl.module
import ru.gorinih.familyshopper.data.db.DatabaseRepositoryImpl
import ru.gorinih.familyshopper.data.db.ShopperDatabase
import ru.gorinih.familyshopper.data.db.dao.DictionaryDao
import ru.gorinih.familyshopper.data.db.dao.ListsDao
import ru.gorinih.familyshopper.data.db.dao.UserDao
import ru.gorinih.familyshopper.domain.DatabaseRepository

/**
 * Created by Igor Abdulganeev on 03.07.2026
 */

val databaseModule = module {

    single<ShopperDatabase> {
        val builder = get<RoomDatabase.Builder<ShopperDatabase>>()
        builder.setDriver(BundledSQLiteDriver())
            .build()
    }

    single<DictionaryDao> { get<ShopperDatabase>().dictionaryDao() }
    single<ListsDao> { get<ShopperDatabase>().listDao() }
    single<UserDao> { get<ShopperDatabase>().userDao() }

    single<DatabaseRepository> {
        DatabaseRepositoryImpl(
            dictionaryDao = get(),
            listsDao = get(),
            userDao = get()
        )
    }

}