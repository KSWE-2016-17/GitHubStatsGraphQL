package de.fhbielefeld.githubstatsgraphql.entity.api

import android.os.Parcel
import android.os.Parcelable

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
data class PageInfo(val endCursor: String, val hasNextPage: Boolean) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<PageInfo> = object : Parcelable.Creator<PageInfo> {
            override fun createFromParcel(source: Parcel): PageInfo = PageInfo(source)
            override fun newArray(size: Int): Array<PageInfo?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readString(), 1 == source.readInt())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(endCursor)
        dest?.writeInt((if (hasNextPage) 1 else 0))
    }
}