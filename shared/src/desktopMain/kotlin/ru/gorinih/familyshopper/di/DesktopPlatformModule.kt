package ru.gorinih.familyshopper.di

import androidx.room.Room
import org.koin.dsl.module
import ru.gorinih.familyshopper.data.db.ShopperDatabase
import ru.gorinih.familyshopper.data.db.ShopperDatabaseConstructor
import java.io.File

/**
 * Created by Igor Abdulganeev on 26.06.2026
 */

actual val platformModule = module {

    single {
        val dbFile = File(System.getProperty("user.home"), ".family_shopper/family_shopper.db")
        if (!dbFile.parentFile.exists()) {
            dbFile.parentFile.mkdirs()
        }
        Room.databaseBuilder<ShopperDatabase>(
            name = dbFile.absolutePath,
            factory = { ShopperDatabaseConstructor.initialize() }
        )
    }
}