package de.fhbielefeld.githubstatsgraphql.result

import de.fhbielefeld.githubstatsgraphql.entity.Viewer
import de.fhbielefeld.githubstatsgraphql.network.GitHubError
import de.fhbielefeld.githubstatsgraphql.network.GitHubResult

/**
 * Result of a request for the username.
 *
 * @author Ruben Gees
 */
class UsernameResult(override val data: Viewer?, errors: Array<GitHubError>?) :
        GitHubResult<Viewer>(errors) {
}