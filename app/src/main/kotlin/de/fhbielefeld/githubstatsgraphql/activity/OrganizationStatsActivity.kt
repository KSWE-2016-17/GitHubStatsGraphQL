package de.fhbielefeld.githubstatsgraphql.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import butterknife.bindView
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.Organization
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

    private val root: ViewGroup by bindView(R.id.root)
    private val refreshLayout: SwipeRefreshLayout by bindView(R.id.refreshLayout)
    private val list: RecyclerView by bindView(R.id.list)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organization_stats)


    }
}
