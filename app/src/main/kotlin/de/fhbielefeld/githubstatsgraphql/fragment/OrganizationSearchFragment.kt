package de.fhbielefeld.githubstatsgraphql.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.activity.OrganizationStatsActivity
import de.fhbielefeld.githubstatsgraphql.adapter.OrganizationAdapter
import de.fhbielefeld.githubstatsgraphql.entity.api.PageInfo
import de.fhbielefeld.githubstatsgraphql.result.organization.search.OrganizationSearchResult.OrganizationSearchData
import de.fhbielefeld.githubstatsgraphql.task.OrganizationSearchTask
import de.fhbielefeld.githubstatsgraphql.task.OrganizationSearchTask.OrganizationSearchInput
import de.fhbielefeld.githubstatsgraphql.task.framework.CachedTask
import de.fhbielefeld.githubstatsgraphql.task.framework.ListenableTask
import de.fhbielefeld.githubstatsgraphql.task.framework.ListeningTask
import de.fhbielefeld.githubstatsgraphql.util.EndlessRecyclerOnScrollListener
import de.fhbielefeld.githubstatsgraphql.util.KotterKnife
import de.fhbielefeld.githubstatsgraphql.util.bindView

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class OrganizationSearchFragment : Fragment() {

    companion object {
        fun newInstance(): OrganizationSearchFragment {
            return OrganizationSearchFragment()
        }
    }

    private val successCallback = { it: OrganizationSearchData ->
        pageInfo = it.pageInfo

        adapter.append(it.organizations)
    }

    private val exceptionCallback = { it: Exception ->
        Snackbar.make(root, it.message ?: "Unknown error", Snackbar.LENGTH_LONG).show()
    }

    private val task = constructTask()
    private var pageInfo = PageInfo("", true)
    private var query: String = ""

    private val root by bindView<ViewGroup>(R.id.root)
    private val progress: SwipeRefreshLayout by bindView(R.id.progress)
    private val list: RecyclerView by bindView(R.id.list)

    private lateinit var adapter: OrganizationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        setHasOptionsMenu(true)

        adapter = OrganizationAdapter()
        adapter.onOrganizationClickListener = {
            OrganizationStatsActivity.navigateTo(activity, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.activity_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        if (query.isNotBlank()) {
            searchItem.expandActionView()
            searchView.post { searchView.setQuery(query, false) }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String): Boolean {
                reset()

                query = newText

                task.execute(OrganizationSearchInput(query, pageInfo.endCursor))

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_organization_search, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent)
        progress.isEnabled = false

        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
        list.addOnScrollListener(object : EndlessRecyclerOnScrollListener(list.layoutManager) {
            override fun onLoadMore() {
                if (pageInfo.hasNextPage && !task.isWorking) {
                    task.reset()
                    task.execute(OrganizationSearchInput(query, pageInfo.endCursor))
                }
            }
        })
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

    private fun reset() {
        task.reset()
        adapter.clear()
        pageInfo = PageInfo("", true)
        query = ""
    }

    private fun setRefreshing(enable: Boolean) {
        progress.isEnabled = enable
        progress.isRefreshing = enable
    }

    private fun constructTask(): ListenableTask<OrganizationSearchInput, OrganizationSearchData> {
        return ListeningTask(CachedTask(OrganizationSearchTask().onStart {
            setRefreshing(true)
        }.onFinish {
            setRefreshing(false)
        }), successCallback, exceptionCallback)
    }
}