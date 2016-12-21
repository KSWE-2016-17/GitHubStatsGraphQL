package de.fhbielefeld.githubstatsgraphql.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.Organization
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.OrganizationContainer
import de.fhbielefeld.githubstatsgraphql.util.bindView

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class OrganizationAdapter : GitHubAdapter<OrganizationContainer>() {

    var onOrganizationClickListener: ((Organization) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrganizationViewHolder {
        return OrganizationViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_organization, parent, false))
    }

    override fun areItemsTheSame(oldItem: OrganizationContainer,
                                 newItem: OrganizationContainer): Boolean {
        return oldItem.node.id == newItem.node.id
    }

    inner class OrganizationViewHolder(itemView: View) :
            GitHubViewHolder<OrganizationContainer>(itemView) {

        private val name: TextView by bindView(R.id.name)
        private val image: ImageView by bindView(R.id.image)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onOrganizationClickListener?.invoke(list[adapterPosition].node)
                }
            }
        }

        override fun bind(item: OrganizationContainer) {
            name.text = if (item.node.name?.isBlank() ?: true) item.node.login else item.node.name

            Glide.with(image.context)
                    .load(item.node.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(image)
        }
    }
}