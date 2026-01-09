package com.example.handmadeexpo.repo

import com.example.handmadeexpo.model.OrderModel

interface OrderRepo {
    fun placeOrder(order: OrderModel, callback: (Boolean, String) -> Unit)
}