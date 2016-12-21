package de.fhbielefeld.githubstatsgraphql.adapter

import android.support.annotation.CallSuper
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import de.fhbielefeld.githubstatsgraphql.adapter.GitHubAdapter.GitHubViewHolder
import java.util.*

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
abstract class GitHubAdapter<T> : RecyclerView.Adapter<GitHubViewHolder<T>>() {

    protected var list = ArrayList<T>()

    override fun onBindViewHolder(holder: GitHubViewHolder<T>, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    fun isEmpty() = list.isEmpty()

    open fun insert(items: Iterable<T>) {
        doUpdates(items.plus(list.filterNot { oldItem ->
            items.find { areItemsTheSame(oldItem, it) } != null
        }))
    }

    fun insert(items: Array<T>) {
        return insert(items.asIterable())
    }

    open fun append(items: Iterable<T>) {
        doUpdates(list.filterNot { oldItem ->
            items.find { areItemsTheSame(oldItem, it) } != null
        }.plus(items))
    }

    fun append(items: Array<T>) {
        append(items.asIterable())
    }

    fun replace(items: Iterable<T>) {
        doUpdates(items.toList())
    }

    fun replace(items: Array<T>) {
        doUpdates(items.toList())
    }

    @CallSuper
    open fun remove(item: T) {
        doUpdates(list.minus(item))
    }

    @CallSuper
    open fun clear() {
        doUpdates(ArrayList<T>(0))
    }

    protected fun doUpdates(newList: List<T>) {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areItemsTheSame(list[oldItemPosition], newList[newItemPosition])
            }

            override fun getOldListSize(): Int {
                return list.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return areContentsTheSame(list[oldItemPosition], newList[newItemPosition])
            }
        })

        list.clear()
        list.addAll(newList)

        result.dispatchUpdatesTo(this)
    }

    open protected fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    open protected fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    abstract class GitHubViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        open fun bind(item: T) {

        }

    }
}