package net.nonylene.gorokus

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmConfiguration

import net.nonylene.gorokus.databinding.MainActivityBinding
import net.nonylene.gorokus.model.Category
import net.nonylene.gorokus.model.newCategoryId

class MainActivity : AppCompatActivity() {

    private val adapter = GorokuRecyclerAdapter()

    /**
     * call binding after [onCreate] due to lazy initialize
     */
    val binding: MainActivityBinding by lazy {
        DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.main_activity)
    }

    val realm by lazy {
        Realm.getInstance(RealmConfiguration.Builder(this).build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm.executeTransactionAsync(Realm.Transaction { realm ->
            val category = realm.createObject(Category::class.java)
            category.id = newCategoryId(realm)
            category.name = "Hoge"
            val category2 = realm.createObject(Category::class.java)
            category2.id = newCategoryId(realm)
            category2.name = "HogePiyo"
            val category3 = realm.createObject(Category::class.java)
            category3.id = newCategoryId(realm)
            category3.name = "HogePoPoPoe"
        }, Realm.Transaction.OnSuccess {
            adapter.categoryList = realm.where(Category::class.java).findAll()
            adapter.notifyDataSetChanged()
        })

        adapter.setHasStableIds(true)
        with(binding.recyclerView) {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        adapter.notifyDataSetChanged()
    }
}
