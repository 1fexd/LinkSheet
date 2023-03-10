package fe.linksheet

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import fe.linksheet.module.database.linkSheetDatabaseModule
import fe.linksheet.module.preference.preferenceRepositoryModule
import fe.linksheet.module.redirectresolver.redirectResolverModule
import fe.linksheet.module.request.requestModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class LinkSheet : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        startKoin {
            androidLogger()
            androidContext(this@LinkSheet)
            modules(
                linkSheetDatabaseModule,
                preferenceRepositoryModule,
                requestModule,
                redirectResolverModule
            )
        }
    }
}