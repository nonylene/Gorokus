package net.nonylene.gorokus.model

import java.util.Date

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

open class Category : RealmObject() {
    @PrimaryKey
    var id = 0
    @Index
    var name = ""
    var childCategories = RealmList<Category>()
    var childGorokus = RealmList<Goroku>()
    @Index
    var count = 0
    @Index
    var lastUsed = Date(0)
}

@Synchronized
fun newCategoryId(realm: Realm): Int {
    return realm.where(Category::class.java).max("id").toInt() + 1
}
