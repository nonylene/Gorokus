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
    open var kana = ""
    @Index
    open var count = 0
    @Index
    open var lastUsed = Date(0)

    fun includes(query: String): Boolean {
        return kanaList().plus(text).any{ it.contains(query, true) }
    }

    fun addKana(text: String) {
        kana += "," + text
    }

    fun kanaList(): List<String> = kana.split(",")
}


@Synchronized
fun newGorokuId(realm: Realm) : Int {
    return realm.where(Goroku::class.java).max("id").toInt() + 1
}
