package de.fhbielefeld.githubstatsgraphql.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.ViewGroup
import butterknife.bindView
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.adapter.OrganizationAdapter
import de.fhbielefeld.githubstatsgraphql.application.MainApplication
import de.fhbielefeld.githubstatsgraphql.entity.api.PageInfo
import de.fhbielefeld.githubstatsgraphql.util.EndlessRecyclerOnScrollListener
import okhttp3.Call

/**
 * The main Activity in which the organizations are presented. All UI work happens here.
 *
 * @author Ruben Gees
 */
class OrganizationSearchActivity : AppCompatActivity() {

    private companion object {
        private const val QUERY_STATE = "query"
        private const val PAGE_INFO_STATE = "pageInfo"
    }

    private lateinit var adapter: OrganizationAdapter

    private var query: String = ""
    private var pageInfo: PageInfo = PageInfo("", true)

    private var task: Call? = null

    private val root: ViewGroup by bindView(R.id.root)
    private val refreshLayout: SwipeRefreshLayout by bindView(R.id.refreshLayout)
    private val list: RecyclerView by bindView(R.id.list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_search)

        adapter = OrganizationAdapter(savedInstanceState)
        adapter.onOrganizationClickListener = {
            OrganizationStatsActivity.navigateTo(this, it)
        }

        savedInstanceState?.let {
            query = it.getString(QUERY_STATE)
            pageInfo = it.getParcelable(PAGE_INFO_STATE)
        }

        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark,
                R.color.colorPrimaryLight)
        hideProgress()

        list.setHasFixedSize(true)
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
        list.addOnScrollListener(object : EndlessRecyclerOnScrollListener(list.layoutManager) {
            override fun onLoadMore() {
                if (task == null && pageInfo.hasNextPage) {
                    load()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setQuery(query, false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String): Boolean {
                reset()
                searchView.setQuery(null, false)

                query = newText
                load()

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(QUERY_STATE, query)
        outState.putParcelable(PAGE_INFO_STATE, pageInfo)
        adapter.saveInstanceState(outState)
    }

    private fun load() {
        showProgress()

        MainApplication.api.organizationSearch(query, pageInfo.endCursor, {
            cancel()

            if (it.data != null) {
                pageInfo = it.data!!.pageInfo
                adapter.add(it.data!!.organizations)
            } else if (it.errors != null) {
                Snackbar.make(root, it.errors.joinToString { it.message }, Snackbar.LENGTH_LONG)
                        .show()
            }

            hideProgress()
        })
    }

    private fun cancel() {
        task?.cancel()
        task = null
    }

    private fun reset() {
        adapter.clear()
        cancel()

        query = ""
        pageInfo = PageInfo("", true)
    }

    fun showProgress() {
        refreshLayout.isEnabled = true
        refreshLayout.isRefreshing = true
    }

    fun hideProgress() {
        refreshLayout.isRefreshing = false
        refreshLayout.isEnabled = false
    }
}
