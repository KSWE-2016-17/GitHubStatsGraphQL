package de.fhbielefeld.githubstatsgraphql.entity.api.organisation.stats

/**
 * Representation of a single commit.
 *
 * @author Ruben Gees
 */
data class Commit(val author: CommitAuthor) {
}