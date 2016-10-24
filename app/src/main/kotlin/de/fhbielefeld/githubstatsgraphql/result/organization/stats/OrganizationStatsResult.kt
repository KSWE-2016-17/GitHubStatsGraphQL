package de.fhbielefeld.githubstatsgraphql.result.organization.stats

import com.squareup.moshi.Json
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.stats.Organization
import de.fhbielefeld.githubstatsgraphql.network.GitHubError
import de.fhbielefeld.githubstatsgraphql.network.GitHubResult
import de.fhbielefeld.githubstatsgraphql.result.organization.stats.OrganizationStatsResult.OrganizationStatsData

/**
 * Result of a request for the organization statistics
 *
 * @author Ruben Gees
 */
class OrganizationStatsResult(override val data: OrganizationStatsData?, errors: Array<GitHubError>?) :
        GitHubResult<OrganizationStatsData>(errors) {

    class OrganizationStatsData(@Json(name = "node") val organization: Organization)

}