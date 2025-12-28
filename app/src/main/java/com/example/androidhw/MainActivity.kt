package com.example.androidhw

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private val avatarIds = listOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4,
        R.drawable.avatar_5
    )

    private val firstNames = listOf(
        "Олег", "Ірина", "Марія", "Андрій", "Наталія",
        "Сергій", "Юлія", "Дмитро", "Олена", "Віктор"
    )

    private val lastNames = listOf(
        "Шевченко", "Коваленко", "Бондар", "Ткаченко", "Кравченко",
        "Олійник", "Мельник", "Коваль", "Поліщук", "Савчук"
    )

    private val countries = listOf(
        Country("Україна", listOf("Київ", "Львів", "Одеса", "Харків", "Дніпро")),
        Country("Польща", listOf("Варшава", "Краків", "Гданськ", "Вроцлав", "Познань")),
        Country("Німеччина", listOf("Берлін", "Гамбург", "Мюнхен", "Кельн", "Франкфурт"))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val users = generateUsers(100)
        val adapter = UserAdapter(this, users)

        val listView = findViewById<ListView>(R.id.list_users)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val user = adapter.getItem(position)
            if (user != null) {
                Toast.makeText(
                    this,
                    getString(R.string.toast_user_selected, user.fullName(), user.age),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun generateUsers(count: Int): List<UserModel> {
        val users = ArrayList<UserModel>(count)
        repeat(count) {
            val avatar = avatarIds.random()
            val firstName = firstNames.random()
            val lastName = lastNames.random()
            val age = Random.nextInt(14, 100)
            val country = countries.random()
            val city = country.cities.random()
            users.add(UserModel(avatar, firstName, lastName, age, country.name, city))
        }
        return users
    }

    private data class Country(val name: String, val cities: List<String>)
}
