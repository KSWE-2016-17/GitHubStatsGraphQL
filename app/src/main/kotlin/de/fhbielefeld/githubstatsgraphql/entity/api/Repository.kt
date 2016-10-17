package de.fhbielefeld.githubstatsgraphql.entity.api

/**
 * Representation of a repository.
 *
 * @author Ruben Gees
 */
class Repository(val id: String, val name: String, private val ref: BranchReference) {

    val branch: Branch
        get() = ref.target

    class BranchReference(val target: Branch)
}