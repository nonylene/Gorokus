package net.nonylene.gorokus

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import net.nonylene.gorokus.model.Category

class GorokuApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initRealm()
    }

    fun initRealm() {
        val realm = Realm.getInstance(RealmConfiguration.Builder(this).build())
        if (realm.where(Category::class.java).findAll().isEmpty()) {
            // add root category
            // use main thread, to prevent conflict.
            realm.beginTransaction()
            with(realm.createObject(Category::class.java)) {
                id = 1
                name = "Root"
            }
            realm.commitTransaction()
        }
    }
}