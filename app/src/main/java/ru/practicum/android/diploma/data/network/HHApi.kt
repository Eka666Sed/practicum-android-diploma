package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.dto.areas.AreasDto
import ru.practicum.android.diploma.data.dto.details.VacancyDetailsResponse
import ru.practicum.android.diploma.data.dto.industry.IndustryDto
import ru.practicum.android.diploma.data.dto.vacancy.VacancyResponse

interface HHApi {

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: DiplomPracticumWithHH"
    )
    @GET("/vacancies/")
    suspend fun searchVacancies(
        @QueryMap params: HashMap<String, String>
    ): VacancyResponse

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: DiplomPracticumWithHH"
    )
    @GET("/vacancies/{vacancy_id}")
    suspend fun searchVacancyDetails(@Path("vacancy_id") id: String): VacancyDetailsResponse

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: DiplomPracticumWithHH"
    )
    @GET("/industries/")
    suspend fun getIndustries(
        @Query("locale") locale: String,
        @Query("host") host: String
    ): List<IndustryDto>

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "HH-User-Agent: DiplomPracticumWithHH"
    )
    @GET("/areas/")
    suspend fun getAreas(
        @Query("locale") locale: String,
        @Query("host") host: String
    ): List<AreasDto>

}
