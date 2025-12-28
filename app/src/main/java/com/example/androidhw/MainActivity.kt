package com.example.androidhw

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit

class MainActivity : FragmentActivity() {
    private val nameModels = listOf(
        NameModel("Андрій", "13 грудня", "Сміливий, сильний духом."),
        NameModel("Олена", "21 травня", "Світла, лагідна."),
        NameModel("Марія", "4 серпня", "Бажана, улюблена."),
        NameModel("Ігор", "18 червня", "Воїн, захисник."),
        NameModel("Софія", "30 вересня", "Мудрість."),
        NameModel("Дмитро", "8 листопада", "Посвячений Деметрі."),
        NameModel("Катерина", "24 листопада", "Чиста, непорочна."),
        NameModel("Олександр", "12 вересня", "Захисник людей."),
        NameModel("Наталія", "8 вересня", "Рідна, народжена."),
        NameModel("Віктор", "11 листопада", "Переможець.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, NameGridFragment())
            }
        }
    }

    fun findNameModel(name: String): NameModel? = nameModels.firstOrNull { it.name == name }

    fun getAllNames(): List<String> = nameModels.map { it.name }
}
