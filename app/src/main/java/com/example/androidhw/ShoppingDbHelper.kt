package com.example.androidhw

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ShoppingDbHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE Lists (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                date INTEGER NOT NULL,
                description TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE Type (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                label TEXT NOT NULL,
                rule TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE Product (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                list_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                quantity REAL,
                type_id INTEGER NOT NULL,
                is_bought INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(list_id) REFERENCES Lists(_id) ON DELETE CASCADE,
                FOREIGN KEY(type_id) REFERENCES Type(_id)
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE Suggestion (
                name TEXT PRIMARY KEY
            )
            """.trimIndent()
        )
        seedTypes(db)
        seedSuggestions(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Product")
        db.execSQL("DROP TABLE IF EXISTS Lists")
        db.execSQL("DROP TABLE IF EXISTS Type")
        db.execSQL("DROP TABLE IF EXISTS Suggestion")
        onCreate(db)
    }

    private fun seedTypes(db: SQLiteDatabase) {
        val types = listOf(
            Pair("шт", "int"),
            Pair("кг", "float"),
            Pair("л", "float")
        )
        types.forEach { (label, rule) ->
            db.execSQL(
                "INSERT INTO Type (label, rule) VALUES (?, ?)",
                arrayOf(label, rule)
            )
        }
    }

    private fun seedSuggestions(db: SQLiteDatabase) {
        DEFAULT_SUGGESTIONS.forEach { name ->
            db.execSQL(
                "INSERT OR IGNORE INTO Suggestion (name) VALUES (?)",
                arrayOf(name)
            )
        }
    }

    companion object {
        private const val DB_NAME = "shopping.db"
        private const val DB_VERSION = 1

        val DEFAULT_SUGGESTIONS = listOf(
            "Milk",
            "Bread",
            "Eggs",
            "Butter",
            "Cheese",
            "Yogurt",
            "Chicken",
            "Beef",
            "Pork",
            "Fish",
            "Rice",
            "Pasta",
            "Flour",
            "Sugar",
            "Salt",
            "Pepper",
            "Olive oil",
            "Vinegar",
            "Tomato",
            "Potato",
            "Onion",
            "Garlic",
            "Carrot",
            "Cucumber",
            "Lettuce",
            "Apple",
            "Banana",
            "Orange",
            "Grapes",
            "Lemon",
            "Coffee",
            "Tea",
            "Juice",
            "Water",
            "Soda",
            "Cereal",
            "Oats",
            "Beans",
            "Lentils",
            "Nuts",
            "Chocolate",
            "Cookies",
            "Chips",
            "Ice cream",
            "Soap",
            "Shampoo",
            "Toothpaste",
            "Toothbrush",
            "Detergent",
            "Sponge",
            "Paper towels",
            "Toilet paper",
            "Trash bags",
            "Batteries",
            "Light bulbs",
            "Notebook",
            "Pen",
            "Pencil",
            "Tape",
            "Glue",
            "Dish soap",
            "Hand sanitizer",
            "Wet wipes",
            "Facial tissues",
            "Deodorant",
            "Shaving cream",
            "Razor",
            "Shower gel",
            "Conditioner",
            "Laundry softener",
            "Bleach",
            "All-purpose cleaner",
            "Glass cleaner",
            "Floor cleaner",
            "Trash bin liners",
            "Pet food",
            "Canned beans",
            "Canned tuna",
            "Tomato sauce",
            "Pasta sauce",
            "Ketchup",
            "Mustard",
            "Mayonnaise",
            "Honey",
            "Jam",
            "Peanut butter",
            "Crackers",
            "Granola bars",
            "Frozen vegetables",
            "Frozen pizza",
            "Sausage",
            "Ham",
            "Bacon",
            "Cooking oil",
            "Sugar free gum",
            "Mineral water",
            "Sparkling water",
            "Tea bags"
        )
    }
}
