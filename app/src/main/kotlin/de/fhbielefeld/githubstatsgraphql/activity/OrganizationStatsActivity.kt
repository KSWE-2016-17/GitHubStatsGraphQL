package de.fhbielefeld.githubstatsgraphql.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.bindView
import com.github.mikephil.charting.charts.BarChart
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.adapter.RepositoryAdapter
import de.fhbielefeld.githubstatsgraphql.application.MainApplication
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.Organization
import de.fhbielefeld.githubstatsgraphql.logic.Analyzer
import de.fhbielefeld.githubstatsgraphql.util.ChartUtils
import okhttp3.Call

class OrganizationStatsActivity : AppCompatActivity() {

    companion object {

        private const val ORGANIZATION_EXTRA = "organization_extra"

        fun navigateTo(context: Activity, organization: Organization) {
            context.startActivity(Intent(context, OrganizationStatsActivity::class.java).apply {
                putExtra(ORGANIZATION_EXTRA, organization)
            })
        }
    }

    val organization: Organization
        get() = intent.getParcelableExtra(ORGANIZATION_EXTRA)

    private var task: Call? = null
    private var data: de.fhbielefeld.githubstatsgraphql.entity.api.organisation.stats.Organization? = null
    private lateinit var adapter: RepositoryAdapter

    private val root: ViewGroup by bindView(R.id.root)
    private val empty: TextView by bindView(R.id.empty)
    private val refreshLayout: SwipeRefreshLayout by bindView(R.id.refreshLayout)
    private val commitContainer: ViewGroup by bindView(R.id.commitContainer)
    private val commits: BarChart by bindView(R.id.commits)
    private val repositoryStatsList: RecyclerView by bindView(R.id.repositoryStatsList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_stats)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        ChartUtils.styleBarChart(commits)

        hideProgress()

        adapter = RepositoryAdapter()

        repositoryStatsList.isNestedScrollingEnabled = false
        repositoryStatsList.layoutManager = LinearLayoutManager(this)
        repositoryStatsList.adapter = adapter

        if (data == null) {
            load()
        } else {
            show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        cancel()

        super.onDestroy()
    }

    private fun load() {
        showProgress()

        task = MainApplication.api.organizationStats(organization.id, {
            cancel()

            if (it.data != null) {
                data = it.data!!.organization

                show()
            } else if (it.errors != null) {
                Snackbar.make(root, it.errors.joinToString { it.message }, Snackbar.LENGTH_LONG)
                        .show()
            }

            hideProgress()
        })
    }

    private fun show() {
        data?.let {
            if (it.repositories.isEmpty()) {
                empty.visibility = View.VISIBLE
                commitContainer.visibility = View.GONE
            } else {
                ChartUtils.populateCommitChart(commits, Analyzer.commitsPerUser(it))
                adapter.replace(it.repositories)

                empty.visibility = View.GONE
                commitContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun cancel() {
        task?.cancel()
        task = null
    }

    private fun showProgress() {
        refreshLayout.isEnabled = true
        refreshLayout.isRefreshing = true
    }

    private fun hideProgress() {
        refreshLayout.isRefreshing = false
        refreshLayout.isEnabled = false
    }
}
