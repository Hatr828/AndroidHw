package com.example.androidhw

import android.content.ContentValues
import android.content.Context

class ShoppingRepository(context: Context) {
    private val dbHelper = ShoppingDbHelper(context.applicationContext)

    fun getListSummaries(): List<ShoppingListSummary> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT l._id, l.name, l.date, l.description,
                   COUNT(p._id) AS total_count,
                   COALESCE(SUM(CASE WHEN p.is_bought = 1 THEN 1 ELSE 0 END), 0) AS bought_count
            FROM Lists l
            LEFT JOIN Product p ON p.list_id = l._id
            GROUP BY l._id
            ORDER BY l.date DESC
        """.trimIndent()
        val result = mutableListOf<ShoppingListSummary>()
        db.rawQuery(query, null).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow("_id")
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            val dateIndex = cursor.getColumnIndexOrThrow("date")
            val descIndex = cursor.getColumnIndexOrThrow("description")
            val totalIndex = cursor.getColumnIndexOrThrow("total_count")
            val boughtIndex = cursor.getColumnIndexOrThrow("bought_count")
            while (cursor.moveToNext()) {
                result.add(
                    ShoppingListSummary(
                        id = cursor.getLong(idIndex),
                        name = cursor.getString(nameIndex),
                        date = cursor.getLong(dateIndex),
                        description = cursor.getString(descIndex),
                        totalCount = cursor.getInt(totalIndex),
                        boughtCount = cursor.getInt(boughtIndex)
                    )
                )
            }
        }
        return result
    }

    fun getList(listId: Long): ShoppingList? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "Lists",
            arrayOf("_id", "name", "date", "description"),
            "_id = ?",
            arrayOf(listId.toString()),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return ShoppingList(
                    id = it.getLong(it.getColumnIndexOrThrow("_id")),
                    name = it.getString(it.getColumnIndexOrThrow("name")),
                    date = it.getLong(it.getColumnIndexOrThrow("date")),
                    description = it.getString(it.getColumnIndexOrThrow("description"))
                )
            }
        }
        return null
    }

    fun insertList(name: String, description: String?): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("date", System.currentTimeMillis())
            put("description", description)
        }
        return db.insert("Lists", null, values)
    }

    fun updateList(listId: Long, name: String, description: String?) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            put("description", description)
        }
        db.update("Lists", values, "_id = ?", arrayOf(listId.toString()))
    }

    fun deleteList(listId: Long) {
        val db = dbHelper.writableDatabase
        db.delete("Lists", "_id = ?", arrayOf(listId.toString()))
    }

    fun getProducts(listId: Long): List<ProductItem> {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT p._id, p.list_id, p.name, p.quantity, p.type_id, p.is_bought,
                   t.label, t.rule
            FROM Product p
            JOIN Type t ON t._id = p.type_id
            WHERE p.list_id = ?
            ORDER BY p._id DESC
        """.trimIndent()
        val result = mutableListOf<ProductItem>()
        db.rawQuery(query, arrayOf(listId.toString())).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow("_id")
            val listIdIndex = cursor.getColumnIndexOrThrow("list_id")
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            val quantityIndex = cursor.getColumnIndexOrThrow("quantity")
            val typeIdIndex = cursor.getColumnIndexOrThrow("type_id")
            val boughtIndex = cursor.getColumnIndexOrThrow("is_bought")
            val labelIndex = cursor.getColumnIndexOrThrow("label")
            val ruleIndex = cursor.getColumnIndexOrThrow("rule")
            while (cursor.moveToNext()) {
                val type = TypeUnit(
                    id = cursor.getLong(typeIdIndex),
                    label = cursor.getString(labelIndex),
                    rule = cursor.getString(ruleIndex)
                )
                val quantity =
                    if (cursor.isNull(quantityIndex)) null else cursor.getDouble(quantityIndex)
                result.add(
                    ProductItem(
                        id = cursor.getLong(idIndex),
                        listId = cursor.getLong(listIdIndex),
                        name = cursor.getString(nameIndex),
                        quantity = quantity,
                        type = type,
                        isBought = cursor.getInt(boughtIndex) == 1
                    )
                )
            }
        }
        return result
    }

    fun getProduct(productId: Long): ProductItem? {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT p._id, p.list_id, p.name, p.quantity, p.type_id, p.is_bought,
                   t.label, t.rule
            FROM Product p
            JOIN Type t ON t._id = p.type_id
            WHERE p._id = ?
        """.trimIndent()
        db.rawQuery(query, arrayOf(productId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                val type = TypeUnit(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("type_id")),
                    label = cursor.getString(cursor.getColumnIndexOrThrow("label")),
                    rule = cursor.getString(cursor.getColumnIndexOrThrow("rule"))
                )
                val quantityIndex = cursor.getColumnIndexOrThrow("quantity")
                val quantity =
                    if (cursor.isNull(quantityIndex)) null else cursor.getDouble(quantityIndex)
                return ProductItem(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
                    listId = cursor.getLong(cursor.getColumnIndexOrThrow("list_id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    quantity = quantity,
                    type = type,
                    isBought = cursor.getInt(cursor.getColumnIndexOrThrow("is_bought")) == 1
                )
            }
        }
        return null
    }

    fun insertProduct(
        listId: Long,
        name: String,
        quantity: Double?,
        typeId: Long,
        isBought: Boolean
    ) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("list_id", listId)
            put("name", name)
            if (quantity == null) {
                putNull("quantity")
            } else {
                put("quantity", quantity)
            }
            put("type_id", typeId)
            put("is_bought", if (isBought) 1 else 0)
        }
        db.insert("Product", null, values)
        ensureSuggestion(name)
    }

    fun updateProduct(productId: Long, name: String, quantity: Double?, typeId: Long, isBought: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", name)
            if (quantity == null) {
                putNull("quantity")
            } else {
                put("quantity", quantity)
            }
            put("type_id", typeId)
            put("is_bought", if (isBought) 1 else 0)
        }
        db.update("Product", values, "_id = ?", arrayOf(productId.toString()))
        ensureSuggestion(name)
    }

    fun deleteProduct(productId: Long) {
        val db = dbHelper.writableDatabase
        db.delete("Product", "_id = ?", arrayOf(productId.toString()))
    }

    fun updateProductBought(productId: Long, isBought: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("is_bought", if (isBought) 1 else 0)
        }
        db.update("Product", values, "_id = ?", arrayOf(productId.toString()))
    }

    fun getTypes(): List<TypeUnit> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<TypeUnit>()
        db.query("Type", arrayOf("_id", "label", "rule"), null, null, null, null, null)
            .use { cursor ->
                while (cursor.moveToNext()) {
                    result.add(
                        TypeUnit(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
                            label = cursor.getString(cursor.getColumnIndexOrThrow("label")),
                            rule = cursor.getString(cursor.getColumnIndexOrThrow("rule"))
                        )
                    )
                }
            }
        return result
    }

    fun getSuggestions(): List<String> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<String>()
        db.query("Suggestion", arrayOf("name"), null, null, null, null, "name ASC")
            .use { cursor ->
                while (cursor.moveToNext()) {
                    result.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
                }
            }
        return result
    }

    fun ensureSuggestion(name: String) {
        val db = dbHelper.writableDatabase
        db.execSQL(
            "INSERT OR IGNORE INTO Suggestion (name) VALUES (?)",
            arrayOf(name)
        )
    }
}
