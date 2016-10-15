package de.fhbielefeld.githubstatsgraphql.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import butterknife.bindView
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.application.MainApplication

/**
 * The main Activity. All UI work happens here.
 *
 * @author Ruben Gees
 */
class MainActivity : AppCompatActivity() {

    private val result: TextView by bindView(R.id.result)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Todo: Replace with a useful request
        MainApplication.api.username {
            if (it.errors != null) {
                result.text = it.errors.joinToString { it.message }
            } else {
                it.data?.let {
                    result.text = it.user.login
                }
            }
        }
    }

}
