package de.fhbielefeld.githubstatsgraphql.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import butterknife.bindView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.application.MainApplication
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.Organization
import okhttp3.Call
import java.util.*

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

    private val root: ViewGroup by bindView(R.id.root)
    private val refreshLayout: SwipeRefreshLayout by bindView(R.id.refreshLayout)
    private val commits: BarChart by bindView(R.id.commits)
    private val userList: RecyclerView by bindView(R.id.list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_stats)

        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark)
        hideProgress()

        if (data == null) {
            load()
        } else {
            show()
        }
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
        commits.description = null
        commits.legend.isEnabled = false
        commits.xAxis.isEnabled = false
        commits.axisRight.isEnabled = false
        commits.data = commitsPerUser().toBarData()
        commits.setVisibleXRangeMaximum(5.toFloat())
        commits.animateY(800)
    }

    private fun commitsPerUser(): HashMap<String, Int> {
        val result = HashMap<String, Int>()

        data!!.repositories.forEach {
            it.branch?.commits?.forEach {
                val user = it.author.user

                if (user != null) {
                    result.put(user.login, result.getOrElse(user.login, { 0 }) + 1)
                }
            }
        }

        return result
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

    private fun HashMap<String, Int>.toBarData(): BarData {
        val list = flatMap { listOf(it).asIterable() }.sortedByDescending { it.value }
        var index = 0

        return BarData(BarDataSet(list.map {
            val result = it.toBarEntry(index)

            index++
            result
        }, null).apply {
            valueTextSize = 10f

            setColors(*ColorTemplate.MATERIAL_COLORS)
            setValueFormatter { value, entry, i, viewPortHandler ->
                list[entry.x.toInt()].key
            }
        })
    }

    private fun Map.Entry<String, Int>.toBarEntry(position: Int): BarEntry {
        return BarEntry(position.toFloat(), value.toFloat())
    }
}
