package de.fhbielefeld.githubstatsgraphql.entity.api

import com.squareup.moshi.Json

/**
 * Representation of an organization.
 *
 * @author Ruben Gees
 */
class Organization(@Json(name = "repositories") private val repositoryCollection:
                   RepositoryCollection) {

    val repositories: List<Repository>
        get() = repositoryCollection.edges.map { it.repository }

    class RepositoryCollection(val edges: Array<RepositoryNode>)

    class RepositoryNode(@Json(name = "node") val repository: Repository)

}