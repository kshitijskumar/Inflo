package org.app.inflo.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.app.inflo.core.constants.DataStoreConstants
import org.app.inflo.core.data.repository.AppRepository
import org.app.inflo.core.data.repository.AppRepositoryImpl
import org.app.inflo.core.data.local.AppLocalDataSource
import org.app.inflo.core.data.local.AppLocalDataSourceImpl
import org.app.inflo.core.data.remote.AppRemoteDataSource
import org.app.inflo.core.data.remote.AppRemoteDataSourceImpl
import org.app.inflo.core.domain.FetchCampaignFeedUseCase
import org.app.inflo.core.domain.ValidatePhoneNumberUseCase
import org.app.inflo.core.utils.TimeUtils
import org.app.inflo.core.utils.TimeUtilsImpl
import org.app.inflo.navigation.InfloNavigationManager
import org.app.inflo.navigation.InfloNavigationManagerImpl
import org.app.inflo.navigation.args.HomeArgsResolver
import org.app.inflo.screens.home.creator.HomeCreatorViewModel
import org.app.inflo.screens.login.LoginViewModel
import org.app.inflo.screens.login.domain.RequestOtpUseCase
import org.app.inflo.screens.login.domain.VerifyLoginUseCase
import org.app.inflo.screens.onboarding.OnboardingViewModel
import org.app.inflo.screens.onboarding.domain.GetOnboardingDetailsUseCase
import org.app.inflo.screens.onboarding.domain.FinishOnboardingUserUseCase
import org.app.inflo.screens.onboarding.domain.ParseUserResponseToProfileUseCase
import org.app.inflo.screens.splash.SplashViewModel
import org.app.inflo.screens.verificationpending.VerificationPendingViewModel
import org.app.inflo.screens.verificationpending.domain.UpdateUserVerificationStatusUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.app.inflo.core.domain.CampaignFeedManager
import org.app.inflo.core.domain.CampaignFeedManagerImpl
import org.app.inflo.screens.home.creator.HomeCreatorTabViewModel
import org.app.inflo.db.AppDatabase
import org.app.inflo.db.AppDao
import app.cash.sqldelight.db.SqlDriver
import org.app.inflo.screens.home.creator.domain.RecordCampaignDecisionUseCase
import org.app.inflo.core.domain.CampaignDecisionSyncManager
import org.app.inflo.core.domain.CampaignDecisionSyncManagerImpl
import org.app.inflo.core.domain.SyncCampaignDecisionsUseCase

fun getSharedCoreModule() = module {
    includes(commonModule(), platformModule())
}

private fun commonModule() = module {
    // Navigation
    singleOf(::InfloNavigationManagerImpl) { bind<InfloNavigationManager>() }
    
    // Repository
    factoryOf(::AppRepositoryImpl) { bind<AppRepository>() }
    
    // Data Sources
    factory<AppLocalDataSource> {
        AppLocalDataSourceImpl(
            dataStore = get(named(DataStoreConstants.APP_DATA_STORE))
        )
    }
    
    factoryOf(::AppRemoteDataSourceImpl) { bind<AppRemoteDataSource>() }
    
    // ViewModels
    factoryOf(::SplashViewModel)
    factory {
        LoginViewModel(
            args = it.get(),
            validatePhoneNumberUseCase = get(),
            requestOtpUseCase = get(),
            verifyLoginUseCase = get(),
            navigationManager = get()
        )
    }
    
    // Use Cases
    factoryOf(::ValidatePhoneNumberUseCase)
    factoryOf(::RequestOtpUseCase)
    factoryOf(::VerifyLoginUseCase)
    factoryOf(::ParseUserResponseToProfileUseCase)

    factoryOf(::GetOnboardingDetailsUseCase)
    factoryOf(::FinishOnboardingUserUseCase)
    factoryOf(::OnboardingViewModel)
    factoryOf(::VerificationPendingViewModel)
    factoryOf(::UpdateUserVerificationStatusUseCase)
    factoryOf(::FetchCampaignFeedUseCase)
    factoryOf(::HomeArgsResolver)
    factoryOf(::RecordCampaignDecisionUseCase)
    factoryOf(::SyncCampaignDecisionsUseCase)

    // Managers
    factoryOf(::CampaignFeedManagerImpl) { bind<CampaignFeedManager>() }
    singleOf(::CampaignDecisionSyncManagerImpl) { bind<CampaignDecisionSyncManager>() }

    // Utils
    singleOf(::TimeUtilsImpl) { bind<TimeUtils>() }

    factoryOf(::HomeCreatorViewModel)
    factoryOf(::HomeCreatorTabViewModel)

    // Database
    single { params ->
        val driver: SqlDriver = get()
        AppDatabase(
            driverFactory = { driver }
        )
    }
    single<AppDao> { get<AppDatabase>().appDao }
}

internal expect fun platformModule(): Module 