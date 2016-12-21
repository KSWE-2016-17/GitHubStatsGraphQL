package de.fhbielefeld.githubstatsgraphql.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.adapter.RepositoryAdapter
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.Organization
import de.fhbielefeld.githubstatsgraphql.logic.Analyzer
import de.fhbielefeld.githubstatsgraphql.result.organization.stats.OrganizationStatsResult.OrganizationStatsData
import de.fhbielefeld.githubstatsgraphql.task.OrganizationStatsTask
import de.fhbielefeld.githubstatsgraphql.task.framework.CachedTask
import de.fhbielefeld.githubstatsgraphql.task.framework.ListenableTask
import de.fhbielefeld.githubstatsgraphql.task.framework.ListeningTask
import de.fhbielefeld.githubstatsgraphql.util.ChartUtils
import de.fhbielefeld.githubstatsgraphql.util.KotterKnife
import de.fhbielefeld.githubstatsgraphql.util.bindView

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class OrganizationStatsFragment : Fragment() {

    companion object {
        private const val EXTRA_ORGANIZATION = "extra_organization"

        fun newInstance(organization: Organization): OrganizationStatsFragment {
            return OrganizationStatsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_ORGANIZATION, organization)
                }
            }
        }
    }

    private val successCallback = { it: OrganizationStatsData ->
        if (it.organization.repositories.isEmpty()) {
            empty.visibility = View.VISIBLE
            commitContainer.visibility = View.GONE
        } else {
            ChartUtils.populateCommitChart(commits, Analyzer.commitsPerUser(it.organization))
            adapter.replace(it.organization.repositories)

            empty.visibility = View.GONE
            commitContainer.visibility = View.VISIBLE
        }
    }

    private val exceptionCallback = { it: Exception ->
        Snackbar.make(root, it.message ?: "Unknown error", Snackbar.LENGTH_LONG).show()
    }

    private val organization: Organization
        get() = arguments.getParcelable(EXTRA_ORGANIZATION)

    private val task = constructTask()

    private lateinit var adapter: RepositoryAdapter

    private val root by bindView<ViewGroup>(R.id.root)
    private val progress: SwipeRefreshLayout by bindView(R.id.progress)
    private val list: RecyclerView by bindView(R.id.list)
    private val commitContainer: ViewGroup by bindView(R.id.commitContainer)
    private val commits: BarChart by bindView(R.id.commits)
    private val empty: TextView by bindView(R.id.empty)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        adapter = RepositoryAdapter()
    }

    override fun onStart() {
        super.onStart()

        task.execute(organization.id)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_organization_stats, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        progress.isEnabled = false

        list.isNestedScrollingEnabled = false
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter

        ChartUtils.styleBarChart(commits)
    }

    override fun onDestroyView() {
        list.adapter = null
        list.layoutManager = null

        KotterKnife.reset(this)

        super.onDestroyView()
    }

    override fun onDestroy() {
        task.destroy()

        super.onDestroy()
    }

    private fun setRefreshing(enable: Boolean) {
        progress.isEnabled = enable
        progress.isRefreshing = enable
    }

    private fun constructTask(): ListenableTask<String, OrganizationStatsData> {
        return ListeningTask(CachedTask(OrganizationStatsTask().onStart {
            setRefreshing(true)
        }.onFinish {
            setRefreshing(false)
        }), successCallback, exceptionCallback)
    }
}