package de.fhbielefeld.githubstatsgraphql.entity.api

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class GitHubException(messages: List<String>) :
        Exception(messages.joinToString(separator = "\n", transform = { it }))