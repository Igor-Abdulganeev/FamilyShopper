package ru.gorinih.familyshopper.di

import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.gorinih.familyshopper.data.remote.RemoteRepositoryImpl
import ru.gorinih.familyshopper.data.remote.createHttpClient
import ru.gorinih.familyshopper.domain.RemoteRepository

/**
 * Created by Igor Abdulganeev on 25.06.2026
 */

val networkModule = module {
    single<HttpClient> {
        val baseUrl = get<String>(named("BASE_URL"))
        createHttpClient(baseUrl)
    }

    single<RemoteRepository> { RemoteRepositoryImpl(client = get(), pref = get()) }
}