package com.example.app.plugins

import com.example.features.applicationkinds.ApplicationKindsRepository
import com.example.features.applicationkinds.ApplicationKindsRoutes
import com.example.features.applicationkinds.ApplicationKindsService
import com.example.features.applications.ApplicationsRepository
import com.example.features.applications.ApplicationsRoutes
import com.example.features.applications.ApplicationsService
import com.example.features.applicationstatuses.ApplicationStatusesRepository
import com.example.features.applicationstatuses.ApplicationStatusesRoutes
import com.example.features.applicationstatuses.ApplicationStatusesService
import com.example.features.auth.AuthRepository
import com.example.features.auth.AuthRoutes
import com.example.features.auth.AuthService
import com.example.features.auth.JwtTokenService
import com.example.features.cities.CitiesRepository
import com.example.features.cities.CitiesRoutes
import com.example.features.cities.CitiesService
import com.example.features.directions.DirectionsRepository
import com.example.features.directions.DirectionsRoutes
import com.example.features.directions.DirectionsService
import com.example.features.skills.SkillsRepository
import com.example.features.skills.SkillsRoutes
import com.example.features.skills.SkillsService
import com.example.features.themes.ThemesRepository
import com.example.features.themes.ThemesRoutes
import com.example.features.themes.ThemesService
import com.example.features.universities.UniversitiesRepository
import com.example.features.universities.UniversitiesRoutes
import com.example.features.universities.UniversitiesService
import com.example.features.userprofiles.UserProfilesRepository
import com.example.features.userprofiles.UserProfilesRoutes
import com.example.features.userprofiles.UserProfilesService
import com.example.features.users.UsersRepository
import com.example.features.users.UsersRoutes
import com.example.features.users.UsersService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val jwtConfig = jwtConfig()
    val authRepository = AuthRepository()
    val jwtTokenService = JwtTokenService(jwtConfig)
    val authService = AuthService(authRepository, jwtTokenService, jwtConfig.accessTokenTtlSeconds)
    val citiesRepository = CitiesRepository()
    val citiesService = CitiesService(citiesRepository)
    val directionsRepository = DirectionsRepository()
    val directionsService = DirectionsService(directionsRepository)
    val skillsRepository = SkillsRepository()
    val skillsService = SkillsService(skillsRepository)
    val applicationKindsRepository = ApplicationKindsRepository()
    val applicationKindsService = ApplicationKindsService(applicationKindsRepository)
    val applicationStatusesRepository = ApplicationStatusesRepository()
    val applicationStatusesService = ApplicationStatusesService(applicationStatusesRepository)
    val universitiesRepository = UniversitiesRepository()
    val universitiesService = UniversitiesService(universitiesRepository)
    val themesRepository = ThemesRepository()
    val themesService = ThemesService(themesRepository)
    val userProfilesRepository = UserProfilesRepository()
    val userProfilesService = UserProfilesService(userProfilesRepository, directionsRepository)
    val applicationsRepository = ApplicationsRepository()
    val applicationsService = ApplicationsService(
        applicationsRepository = applicationsRepository,
        themesRepository = themesRepository,
        applicationKindsRepository = applicationKindsRepository,
        applicationStatusesRepository = applicationStatusesRepository,
    )
    val usersRepository = UsersRepository()
    val usersService = UsersService(usersRepository)

    routing {
        AuthRoutes(authService)
        CitiesRoutes(citiesService)
        DirectionsRoutes(directionsService)
        SkillsRoutes(skillsService)
        ApplicationKindsRoutes(applicationKindsService)
        ApplicationStatusesRoutes(applicationStatusesService)
        UniversitiesRoutes(universitiesService)
        ThemesRoutes(themesService)
        UserProfilesRoutes(userProfilesService)
        ApplicationsRoutes(applicationsService)
        UsersRoutes(usersService)
    }
}