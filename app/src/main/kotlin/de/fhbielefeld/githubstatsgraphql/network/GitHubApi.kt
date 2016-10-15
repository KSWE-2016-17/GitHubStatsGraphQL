package de.fhbielefeld.githubstatsgraphql.network

import android.content.Context
import android.os.Handler
import android.support.annotation.RawRes
import com.squareup.moshi.Moshi
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.entity.Viewer
import de.fhbielefeld.githubstatsgraphql.result.UsernameResult
import okhttp3.Call
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * The access point for all GitHub APIs.
 *
 * @author Ruben Gees
 */
class GitHubApi(private val context: Context) {

    private val handler = Handler(context.mainLooper)
    private val gitHubHttpClient = GitHubHttpClient()
    private val parser: Moshi = Moshi.Builder().build()

    fun username(callback: (GitHubResult<Viewer>) -> Unit): Call {
        return gitHubHttpClient.request(readResource(R.raw.query_username),
                parseCallback = {
                    deliverOnMainThread(parser.adapter(UsernameResult::class.java).fromJson(it),
                            callback)
                },
                errorCallback = {
                    deliverOnMainThread(UsernameResult(null, arrayOf(GitHubError(it))), callback)
                })
    }

    private fun <T> deliverOnMainThread(result: GitHubResult<T>,
                                        callback: ((GitHubResult<T>) -> Unit)? = null) {
        handler.post {
            callback?.invoke(result)
        }
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