package de.fhbielefeld.githubstatsgraphql.network

import de.fhbielefeld.githubstatsgraphql.BuildConfig
import okhttp3.*
import java.io.IOException

/**
 * Entry point for raw HTTP calls to the GitHub API.
 * Takes care of authentication and HTTP error handling.
 *
 * @author Ruben Gees
 */
class GitHubHttpClient {

    companion object {
        private val MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8")
        private val URL = HttpUrl.Builder()
                .scheme("https")
                .host("api.github.com")
                .addPathSegment("graphql")
                .build()
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                it.proceed(it.request().newBuilder()
                        .header("Authorization", Credentials.basic(BuildConfig.GITHUB_USERNAME,
                                BuildConfig.GITHUB_PASSWORD))
                        .build())
            }.build()

    fun request(query: String, parseCallback: ((String) -> Unit)?,
                errorCallback: ((String) -> Unit)? = null): Call {
        val call = okHttpClient.newCall(Request.Builder()
                .url(URL)
                .post(RequestBody.create(MEDIA_TYPE_JSON, query))
                .build())

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, exception: IOException) {
                errorCallback?.invoke(exception.message ?: "Something went wrong")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    parseCallback?.invoke(response.body().string())
                } else {
                    errorCallback?.invoke(response.message())
                }

                response.close()
            }
        })

        return call
    }
}