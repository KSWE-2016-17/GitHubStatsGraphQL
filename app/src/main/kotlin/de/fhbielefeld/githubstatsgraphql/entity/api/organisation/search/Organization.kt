package de.fhbielefeld.githubstatsgraphql.entity.api.organisation.search

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * TODO: Describe class
 *
 * @author Ruben Gees
 */
data class Organization(val id: String, val login: String, val name: String?,
                        @Json(name = "avatarURL") val avatar: String) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Organization> = object : Parcelable.Creator<Organization> {
            override fun createFromParcel(source: Parcel): Organization = Organization(source)
            override fun newArray(size: Int): Array<Organization?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readString(), source.readString(),
            source.readString(), source.readString())

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(login)
        dest.writeString(name)
        dest.writeString(avatar)
    }
}