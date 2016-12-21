package de.fhbielefeld.githubstatsgraphql.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.fragment.OrganizationSearchFragment

/**
 * The main Activity in which the organizations are presented. All UI work happens here.
 *
 * @author Ruben Gees
 */
class OrganizationSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container,
                    OrganizationSearchFragment.newInstance()).commitNow()
        }
    }
}
