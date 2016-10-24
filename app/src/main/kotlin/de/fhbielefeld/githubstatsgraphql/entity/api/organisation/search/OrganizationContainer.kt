package de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search

import android.os.Parcel
import android.os.Parcelable

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
data class OrganizationContainer(val node: Organization) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<OrganizationContainer> = object : Parcelable.Creator<OrganizationContainer> {
            override fun createFromParcel(source: Parcel): OrganizationContainer = OrganizationContainer(source)
            override fun newArray(size: Int): Array<OrganizationContainer?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(Organization.CREATOR.createFromParcel(source))

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(node, flags)
    }
}