package de.fhbielefeld.githubstatsgraphql.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.charts.BarChart
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.stats.Repository
import de.fhbielefeld.githubstatsgraphql.logic.Analyzer
import de.fhbielefeld.githubstatsgraphql.util.ChartUtils
import de.fhbielefeld.githubstatsgraphql.util.bindView

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class RepositoryAdapter : GitHubAdapter<Repository>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_repository, parent, false))
    }

    override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean {
        return oldItem.id == newItem.id
    }

    inner class RepositoryViewHolder(itemView: View) : GitHubViewHolder<Repository>(itemView) {

        private val name: TextView by bindView(R.id.name)
        private val commits: BarChart by bindView(R.id.commits)

        init {
            ChartUtils.styleBarChart(commits)
        }

        override fun bind(item: Repository) {
            name.text = item.name

            ChartUtils.populateCommitChart(commits, Analyzer.commitsPerUser(item))
        }
    }
}