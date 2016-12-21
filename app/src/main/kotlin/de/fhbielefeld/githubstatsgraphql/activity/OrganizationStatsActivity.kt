package de.fhbielefeld.githubstatsgraphql.activity

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.Organization
import de.fhbielefeld.githubstatsgraphql.fragment.OrganizationStatsFragment
import org.jetbrains.anko.startActivity

class OrganizationStatsActivity : AppCompatActivity() {

    companion object {

        private const val ORGANIZATION_EXTRA = "organization_extra"

        fun navigateTo(context: Activity, organization: Organization) {
            context.startActivity<OrganizationStatsActivity>(ORGANIZATION_EXTRA to organization)
        }
    }

    val organization: Organization
        get() = intent.getParcelableExtra(ORGANIZATION_EXTRA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        title = organization.name ?: organization.login
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container,
                    OrganizationStatsFragment.newInstance(organization)).commitNow()
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
}
