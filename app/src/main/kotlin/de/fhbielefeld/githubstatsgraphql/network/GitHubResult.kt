package de.fhbielefeld.githubstatsgraphql.network

/**
 * The data of an request done in the [GitHubApi] class.
 *
 * @author Ruben Gees
 */
abstract class GitHubResult<T>(val errors: Array<GitHubError>?) {

    abstract val data: T?
}