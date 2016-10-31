package de.fhbielefeld.githubstatsgraphql.adapter

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.fhbielefeld.githubstatsgraphql.R
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.Organization
import de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search.OrganizationContainer
import java.util.*

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
class OrganizationAdapter(savedInstanceState: Bundle?) : RecyclerView.Adapter<OrganizationAdapter.ViewHolder>() {

    private companion object {
        private const val LIST_STATE = "organization_adapter_list"
    }

    val list: ArrayList<OrganizationContainer>
    var onOrganizationClickListener: ((Organization) -> Unit)? = null

    init {
        if (savedInstanceState == null) {
            list = ArrayList()
        } else {
            list = savedInstanceState.getParcelableArrayList(LIST_STATE)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position].node)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_organization, parent, false))
    }

    fun add(items: Collection<OrganizationContainer>) {
        val sizeBefore = list.size

        list.addAll(items)
        notifyItemRangeInserted(sizeBefore, items.size)
    }

    fun clear() {
        val sizeBefore = list.size

        list.clear()
        notifyItemRangeRemoved(0, sizeBefore)
    }

    fun saveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(LIST_STATE, list)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView by bindView(R.id.name)
        private val image: ImageView by bindView(R.id.image)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onOrganizationClickListener?.invoke(list[adapterPosition].node)
                }
            }
        }

        fun bind(item: Organization) {
            name.text = if (item.name?.isBlank() ?: true) item.login else item.name

            Glide.with(image.context)
                    .load(item.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(image)
        }
    }
}