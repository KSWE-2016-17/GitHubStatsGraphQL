package de.fhbielefeld.githubstatsgraphql.task

import de.fhbielefeld.githubstatsgraphql.application.MainApplication
import de.fhbielefeld.githubstatsgraphql.entity.api.GitHubException
import de.fhbielefeld.githubstatsgraphql.result.organization.search.OrganizationSearchResult.OrganizationSearchData
import de.fhbielefeld.githubstatsgraphql.task.OrganizationSearchTask.OrganizationSearchInput
import de.fhbielefeld.githubstatsgraphql.task.framework.BaseListenableTask
import okhttp3.Call

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class OrganizationSearchTask(successCallback: ((OrganizationSearchData) -> Unit)? = null,
                             exceptionCallback: ((Exception) -> Unit)? = null) :
        BaseListenableTask<OrganizationSearchInput, OrganizationSearchData>(successCallback,
                exceptionCallback) {

    override val isWorking: Boolean
        get() = call != null

    private var call: Call? = null

    override fun execute(input: OrganizationSearchInput) {
        start {
            call = MainApplication.api.organizationSearch(input.query, input.cursor, {
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

    class OrganizationSearchInput(val query: String, val cursor: String)
}