package de.fhbielefeld.githubstatsgraphql.entity.api

import com.squareup.moshi.Json

/**
 * Representation of a single branch.
 *
 * @author Ruben Gees
 */
class Branch(private val history: BranchHistory) {

    val commits: List<Commit>
        get() = history.edges.map { it.commit }

    class BranchHistory(val edges: Array<CommitNode>)

    class CommitNode(@Json(name = "node") val commit: Commit)

}