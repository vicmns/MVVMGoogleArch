package com.lonelystudios.palantir.vo.sources

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.StringDef
import com.google.gson.annotations.SerializedName
import com.lonelystudios.palantir.vo.SortBy
import javax.annotation.Generated

@Generated("com.robohorse.robopojogenerator")
@Entity(tableName = "sources")
data class Source(

        @field:SerializedName("id")
        @PrimaryKey var id: String = "",

        @field:SerializedName("country")
        var country: String? = null,

        var urlToLogo: String? = null,

        var isUrlLogoAvailable: Boolean = true,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("sortBysAvailable")
        var sortBysAvailable: List<String> = emptyList(),

        @field:SerializedName("description")
        var description: String? = null,

        @field:SerializedName("language")
        var language: String? = null,

        @field:SerializedName("category")
        var category: String? = null,

        @field:SerializedName("url")
        var url: String? = null,

        var isUserSelected: Boolean = false


) : Parcelable {
        constructor(source: Parcel) : this(
                source.readString(),
                source.readString(),
                source.readString(),
                1 == source.readInt(),
                source.readString(),
                source.createStringArrayList(),
                source.readString(),
                source.readString(),
                source.readString(),
                source.readString(),
                1 == source.readInt()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
                writeString(id)
                writeString(country)
                writeString(urlToLogo)
                writeInt((if (isUrlLogoAvailable) 1 else 0))
                writeString(name)
                writeStringList(sortBysAvailable)
                writeString(description)
                writeString(language)
                writeString(category)
                writeString(url)
                writeInt((if (isUserSelected) 1 else 0))
        }

        companion object {
                @JvmField
                val CREATOR: Parcelable.Creator<Source> = object : Parcelable.Creator<Source> {
                        override fun createFromParcel(source: Parcel): Source = Source(source)
                        override fun newArray(size: Int): Array<Source?> = arrayOfNulls(size)
                }

                const val ALL = ""
                const val BUSINESS = "business"
                const val ENTERTAINMENT = "entertainment"
                const val GAMING = "gaming"
                const val GENERAL = "general"
                const val HEALTH_AND_MEDICAL = "health-and-medical"
                const val MUSIC = "music"
                const val SCIENCE_AND_NATURE = "science-and-nature"
                const val SPORT = "sport"
                const val TECHNOLOGY = "technology"
        }

        @StringDef(ALL, BUSINESS, ENTERTAINMENT, GAMING, GENERAL, HEALTH_AND_MEDICAL, MUSIC,
                SCIENCE_AND_NATURE, SPORT, TECHNOLOGY)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Category
}