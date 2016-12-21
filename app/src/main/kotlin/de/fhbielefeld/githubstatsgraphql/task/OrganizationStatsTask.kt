package de.fhbielefeld.githubstatsgraphql.task

import de.fhbielefeld.githubstatsgraphql.application.MainApplication
import de.fhbielefeld.githubstatsgraphql.entity.api.GitHubException
import de.fhbielefeld.githubstatsgraphql.result.organization.stats.OrganizationStatsResult.OrganizationStatsData
import de.fhbielefeld.githubstatsgraphql.task.framework.BaseListenableTask
import okhttp3.Call

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class OrganizationStatsTask(successCallback: ((OrganizationStatsData) -> Unit)? = null,
                            exceptionCallback: ((Exception) -> Unit)? = null) :
        BaseListenableTask<String, OrganizationStatsData>(successCallback, exceptionCallback) {

    override val isWorking: Boolean
        get() = call != null

    private var call: Call? = null

    override fun execute(input: String) {
        start {
            call = MainApplication.api.organizationStats(input, {
                cancel()

                if (it.errors != null) {
                    finishWithException(GitHubException(it.errors.map { it.message }))
                } else {
                    finishSuccessful(it.data!!)
                }
            })
        }
    }

    override fun cancel() {
        call?.cancel()
        call = null
    }

    override fun reset() {
        cancel()
    }
}