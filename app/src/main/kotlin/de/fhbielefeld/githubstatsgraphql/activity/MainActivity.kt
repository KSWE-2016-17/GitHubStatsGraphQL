package de.fhbielefeld.githubstatsgraphql.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import butterknife.bindView
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.application.MainApplication
import de.fhbielefeld.githubstatsgraphql.entity.api.Commit
import de.fhbielefeld.githubstatsgraphql.entity.api.User
import de.fhbielefeld.githubstatsgraphql.result.OrganizationStatsResult.OrganizationStatsData
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
        private val COMPARATOR = compareByDescending<Map.Entry<User, MutableList<Commit>>>({
            it.value.size
        }).thenBy { it.key.login }
    }

    private val result: TextView by bindView(R.id.result)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MainApplication.api.organizationStats {
            if (it.errors != null) {
                result.text = it.errors.joinToString { it.message }
            } else if (it.data != null) {
                analyze(it.data!!)
            }
        }
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

}
