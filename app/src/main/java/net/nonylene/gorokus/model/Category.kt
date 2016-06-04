package net.nonylene.gorokus.model

import java.util.Date

import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

val ROOT_CATEGORY_ID = 1

// todo: backlinks
open class Category : RealmObject() {
    @PrimaryKey
    var id = 0
    @Index
    var name = ""
    var categories = RealmList<Category>()
    var gorokus = RealmList<Goroku>()
    @Index
    var count = 0
    @Index
    var lastUsed = Date(0)
}

@Synchronized
fun newCategoryId(realm: Realm): Int {
    return realm.where(Category::class.java).max("id").toInt() + 1
}

fun findAllChildGorokus(category: Category) : List<Goroku> {
    return findAllChildCategories(category).plus(category).fold(emptyList()) { list, category ->
        list.plus(category.gorokus)
    }
}

fun findAllChildCategories(category: Category) : List<Category> {
    if (category.categories.isEmpty()) {
        return listOf()
    } else {
        return findAllChildCategoriesInternal(category)
    }
}

private fun findAllChildCategoriesInternal(category: Category) : List<Category> {
  if (category.categories.isEmpty()) {
      return listOf(category)
  } else {
      return category.categories.fold(emptyList()) { list, category ->
          list.plus(findAllChildCategoriesInternal(category))
      }
  }
}

fun rootCategory(realm: Realm): Category {
    return realm.where(Category::class.java).equalTo("id", ROOT_CATEGORY_ID).findFirst()
}
