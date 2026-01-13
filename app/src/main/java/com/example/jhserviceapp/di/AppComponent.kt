package com.example.jhserviceapp.di

import android.content.Context
import com.example.jhserviceapp.presentation.addreport.AddReportFragment
import com.example.jhserviceapp.presentation.main.MainFragment
import com.example.jhserviceapp.presentation.settings.SettingsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ReportModule::class, SharedPrefModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(fragment: AddReportFragment)
    fun inject(fragment: MainFragment)
    fun inject(fragment: SettingsFragment)
}