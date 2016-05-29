package net.nonylene.gorokus.model

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import java.util.*

open class Goroku : RealmObject() {
    @PrimaryKey
    open var id = 0
    @Index
    open var text = ""
    @Index
    open var categoryPath = ""
    @Index
    open var count = 0
    @Index
    open var lastUsed = Date(0)
}

@Synchronized
fun newGorokuId(realm: Realm) : Int {
    return realm.where(Goroku::class.java).max("id").toInt() + 1
}
