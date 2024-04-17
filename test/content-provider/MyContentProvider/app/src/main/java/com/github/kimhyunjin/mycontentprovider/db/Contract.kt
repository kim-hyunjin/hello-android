package com.github.kimhyunjin.myapplication.db

import android.provider.BaseColumns

object PersonContract {
    object PersonEntry : BaseColumns {
        const val TABLE_NAME = "person"
        const val PERSON_NAME = "name"
        const val PERSON_AGE = "age"
        const val PERSON_MOBILE = "mobile"
    }
}