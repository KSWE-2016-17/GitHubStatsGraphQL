package de.fhbielefeld.githubstatsgraphql.application

import android.app.Application
import de.fhbielefeld.githubstatsgraphql.network.GitHubApi

/**
 * The main Application. Library configurations are done here.
 *
 * @author Ruben Gees
 */
class MainApplication : Application() {

    companion object {
        lateinit var api: GitHubApi
            private set
    }

    override fun onCreate() {
        super.onCreate()

        api = GitHubApi(this)
    }
}