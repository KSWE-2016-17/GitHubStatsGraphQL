package de.fhbielefeld.githubstatsgraphql.logic

import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.stats.Organization
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.stats.Repository
import java.util.*

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
object Analyzer {

    fun commitsPerUser(data: Organization): HashMap<String, Int> {
        val result = HashMap<String, Int>()

        data.repositories.forEach {
            commitsPerUser(it).forEach {
                result.put(it.key, result.getOrElse(it.key, { 0 }) + it.value)
            }
        }

        return result
    }

    fun commitsPerUser(data: Repository): HashMap<String, Int> {
        val result = HashMap<String, Int>()

        data.branch?.commits?.forEach {
            val user = it.author.user

            if (user != null) {
                result.put(user.login, result.getOrElse(user.login, { 0 }) + 1)
            }
        }

        return result
    }

}