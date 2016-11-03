package de.fhbielefeld.githubstatsgraphql.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.bindView
import com.github.mikephil.charting.charts.BarChart
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.stats.Repository
import de.fhbielefeld.githubstatsgraphql.logic.Analyzer
import de.fhbielefeld.githubstatsgraphql.util.ChartUtils
import java.util.*

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class RepositoryAdapter : RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    private val list = ArrayList<Repository>()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repository,
                parent, false))
    }

    fun replace(items: Collection<Repository>) {
        list.clear()
        list.addAll(items)

        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView by bindView(R.id.name)
        private val commits: BarChart by bindView(R.id.commits)

        init {
            ChartUtils.styleBarChart(commits)
        }

        fun bind(item: Repository) {
            name.text = item.name

            ChartUtils.populateCommitChart(commits, Analyzer.commitsPerUser(item))
        }
    }
}