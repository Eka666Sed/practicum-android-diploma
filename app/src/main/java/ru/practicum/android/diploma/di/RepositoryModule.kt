package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.search.impl.VacancyRepositoryImpl
import ru.practicum.android.diploma.domain.search.VacancyRepository
import ru.practicum.android.diploma.data.db.FavoriteVacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.db.FavoriteVacanciesRepository

val repositoryModule = module {

    factory<VacancyRepository> {
        VacancyRepositoryImpl(get(), get())
    }

    single<FavoriteVacanciesRepository> {
        FavoriteVacanciesRepositoryImpl(get(), get())
    }

}
