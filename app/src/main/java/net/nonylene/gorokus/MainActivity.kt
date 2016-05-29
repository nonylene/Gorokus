package net.nonylene.gorokus

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
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

        // initialize test data
        if (realm.where(Category::class.java).findAll().isEmpty()) {
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
        }

        adapter.setHasStableIds(true)
        adapter.categoryListener = { category ->
            // or copy to clipboard?
            if (intent.getStringExtra("replace_key") == null) {
                Toast.makeText(this, category.name, Toast.LENGTH_LONG).show()
            } else {
                setResult(RESULT_OK, Intent().putExtra("replace_key", category.name))
                finish()
            }
        }
        adapter.gorokuListener = { category ->
        }

        with(binding.recyclerView) {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        adapter.categoryList = realm.where(Category::class.java).findAll()
        adapter.notifyDataSetChanged()

        binding.searchEditText.setText(intent.getStringExtra("replace_key"))
    }
}
