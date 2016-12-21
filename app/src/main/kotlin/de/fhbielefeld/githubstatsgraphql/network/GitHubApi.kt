package de.fhbielefeld.githubstatsgraphql.network

import android.content.Context
import android.os.Handler
import android.support.annotation.RawRes
import com.squareup.moshi.Moshi
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.result.organization.search.OrganizationSearchResult
import de.fhbielefeld.githubstatsgraphql.result.organization.search.OrganizationSearchResult.OrganizationSearchData
import de.fhbielefeld.githubstatsgraphql.result.organization.stats.OrganizationStatsResult
import de.fhbielefeld.githubstatsgraphql.result.organization.stats.OrganizationStatsResult.OrganizationStatsData
import okhttp3.Call
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * The access point for all GitHub APIs.
 *
 * @author Ruben Gees
 */
class GitHubApi(private val context: Context) {

    private companion object {
        private const val BRANCH = "master"
    }

    private val handler = Handler(context.mainLooper)
    private val gitHubHttpClient = GitHubHttpClient()
    private val parser: Moshi = Moshi.Builder().build()

    fun organizationStats(id: String, callback: (GitHubResult<OrganizationStatsData>) -> Unit): Call {
        return gitHubHttpClient.request(buildQuery(R.raw.query_stats, id, BRANCH),
                parseCallback = {
                    deliverOnMainThread(parser.adapter(OrganizationStatsResult::class.java)
                            .fromJson(it), callback)
                },
                errorCallback = {
                    deliverOnMainThread(OrganizationStatsResult(null, arrayOf(GitHubError(it))),
                            callback)
                })
    }

    fun organizationSearch(query: String, cursor: String?,
                           callback: (GitHubResult<OrganizationSearchData>) -> Unit): Call {
        val finalCursor = if (cursor.isNullOrBlank()) "" else "after:\\\"$cursor\\\""

        return gitHubHttpClient.request(buildQuery(R.raw.query_search_organization, query, finalCursor),
                parseCallback = {
                    deliverOnMainThread(parser.adapter(OrganizationSearchResult::class.java)
                            .fromJson(it).init(), callback)
                },
                errorCallback = {
                    deliverOnMainThread(OrganizationSearchResult(null, arrayOf(GitHubError(it))),
                            callback)
                })
    }

    private fun <T> deliverOnMainThread(result: GitHubResult<T>,
                                        callback: ((GitHubResult<T>) -> Unit)? = null) {
        handler.post {
            callback?.invoke(result)
        }
    }

    private fun buildQuery(@RawRes queryResource: Int, vararg arguments: Any?): String {
        val queryContainer = readResource(R.raw.query)
        val query = readResource(queryResource)

        return queryContainer.format(query.format(*arguments))
    }

    private fun readResource(@RawRes resource: Int): String {
        val reader = BufferedReader(InputStreamReader(context.resources.openRawResource(resource)))
        val result = StringBuffer()
        var line = reader.readLine()

        while (line != null) {
            result.append(line)

            line = reader.readLine()
        }

        return result.toString()
    }
}