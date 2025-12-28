package com.example.androidhw

import android.app.Application

class CarApp : Application() {
    val cars: List<CarModel> by lazy { buildCars() }

    private fun buildCars(): List<CarModel> {
        return listOf(
            CarModel(
                1,
                "BMW",
                "M3",
                2018,
                "Black BMW M3 coupe with sporty stance.",
                48000,
                R.drawable.car_bmw_m3
            ),
            CarModel(
                2,
                "BMW",
                "M4",
                2017,
                "Teal BMW M4 coupe, performance tuned.",
                52000,
                R.drawable.car_bmw_m4
            ),
            CarModel(
                3,
                "Audi",
                "A5",
                2019,
                "Red Audi A5 coupe with sleek lines.",
                35000,
                R.drawable.car_audi_a5
            ),
            CarModel(
                4,
                "Land Rover",
                "Defender 110",
                2022,
                "White Defender with roof tent, ready for trips.",
                75000,
                R.drawable.car_defender
            ),
            CarModel(
                5,
                "Hyundai",
                "Tucson",
                2021,
                "Black Tucson crossover for family use.",
                28000,
                R.drawable.car_tucson
            ),
            CarModel(
                6,
                "Lynk & Co",
                "01",
                2020,
                "Black Lynk & Co 01 SUV with modern styling.",
                32000,
                R.drawable.car_lynkco01
            )
        )
    }
}
