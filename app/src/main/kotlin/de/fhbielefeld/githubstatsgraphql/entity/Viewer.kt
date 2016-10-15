package de.fhbielefeld.githubstatsgraphql.entity

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

/**
 * Entity, holding info of the current viewer (logged in user)
 *
 * @author Ruben Gees
 */
data class Viewer(@Json(name = "viewer") val user: User) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Viewer> = object : Parcelable.Creator<Viewer> {
            override fun createFromParcel(source: Parcel): Viewer = Viewer(source)
            override fun newArray(size: Int): Array<Viewer?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(source.readParcelable<User>(User::class.java.classLoader))

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeParcelable(user, 0)
    }
}