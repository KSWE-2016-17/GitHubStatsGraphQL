package de.fhbielefeld.githubstatsgraphql.activity

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import butterknife.bindView
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.application.MainApplication
import de.fhbielefeld.githubstatsgraphql.entity.api.Commit
import de.fhbielefeld.githubstatsgraphql.entity.api.User
import de.fhbielefeld.githubstatsgraphql.result.OrganizationStatsResult.OrganizationStatsData
import okhttp3.Call
import java.util.*
import kotlin.comparisons.compareByDescending
import kotlin.comparisons.thenBy

/**
 * The main Activity. All UI work happens here.
 *
 * @author Ruben Gees
 */
class MainActivity : AppCompatActivity() {

    private companion object {
        private const val ORGANIZATION_ID = "MDEyOk9yZ2FuaXphdGlvbjIyNjM4NDcw"

        private val COMPARATOR = compareByDescending<Map.Entry<User, MutableList<Commit>>>({
            it.value.size
        }).thenBy { it.key.login }
    }

    private var call: Call? = null

    private val refreshLayout: SwipeRefreshLayout by bindView(R.id.refreshLayout)
    private val result: TextView by bindView(R.id.result)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark,
                R.color.colorPrimaryLight)
        refreshLayout.setOnRefreshListener {
            if (call == null) {
                load()
            }
        }

        load()
    }

    override fun onDestroy() {
        call?.cancel()

        super.onDestroy()
    }

    private fun analyze(data: OrganizationStatsData) {
        val map = LinkedHashMap<User, MutableList<Commit>>()

        data.organization.repositories.flatMap { it.branch.commits }
                .filterNot { it.author.user == null }.forEach {
            map.getOrPut(it.author.user!!, { ArrayList<Commit>() }).add(it)
        }

        result.text = map.asSequence().sortedWith(COMPARATOR)
                .joinToString(separator = "\n", transform = { "${it.key.login}: ${it.value.size}" })
    }

    private fun load() {
        refreshLayout.isRefreshing = true

        MainApplication.api.organizationStats(ORGANIZATION_ID) {
            refreshLayout.isRefreshing = false
            call = null

            if (it.errors != null) {
                result.text = it.errors.joinToString { it.message }
            } else if (it.data != null) {
                analyze(it.data!!)
            }
        }
    }
}
