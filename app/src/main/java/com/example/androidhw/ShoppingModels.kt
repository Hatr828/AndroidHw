package com.example.androidhw

data class ShoppingList(
    val id: Long,
    val name: String,
    val date: Long,
    val description: String?
)

data class ShoppingListSummary(
    val id: Long,
    val name: String,
    val date: Long,
    val description: String?,
    val totalCount: Int,
    val boughtCount: Int
)

data class TypeUnit(
    val id: Long,
    val label: String,
    val rule: String
)

data class ProductItem(
    val id: Long,
    val listId: Long,
    val name: String,
    val quantity: Double?,
    val type: TypeUnit,
    val isBought: Boolean
)
