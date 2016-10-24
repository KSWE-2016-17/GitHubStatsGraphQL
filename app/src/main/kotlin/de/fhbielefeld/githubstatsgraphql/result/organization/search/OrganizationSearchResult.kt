package de.fhbielefeld.githubstatsgraphql.result.organization.search

import de.fhbielefeld.githubstatsgraphql.entity.api.PageInfo
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.OrganizationContainer
import de.fhbielefeld.githubstatsgraphql.network.GitHubError
import de.fhbielefeld.githubstatsgraphql.network.GitHubResult
import de.fhbielefeld.githubstatsgraphql.result.organization.search.OrganizationSearchResult.OrganizationSearchData

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class OrganizationSearchResult(override val data: OrganizationSearchData?, errors: Array<GitHubError>?) :
        GitHubResult<OrganizationSearchData>(errors) {

    class OrganizationSearchData(private val search: OrganizationSearch) {
        val pageInfo: PageInfo
            get() = search.pageInfo

        val organizations: List<OrganizationContainer>
            get() = search.edges

        fun init() {
            search.edges = search.edges.filter { !it.node.name.isNullOrBlank() }
        }

        class OrganizationSearch(val pageInfo: PageInfo, var edges: List<OrganizationContainer>)
    }

    fun init(): OrganizationSearchResult {
        data?.init()

        return this
    }

}