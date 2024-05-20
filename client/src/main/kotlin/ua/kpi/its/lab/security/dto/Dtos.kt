package ua.kpi.its.lab.security.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrainRequest(
    var model: String,
    var manufacturer: String,
    var type: String,
    var dateOfCommissioning: String,
    var numberOfSeats: Int,
    var weight: Int,
    var isAC: Boolean,
    var route: RouteRequest
)

@Serializable
data class TrainResponse(
    var id: Long,
    var model: String,
    var manufacturer: String,
    var type: String,
    var dateOfCommissioning: String,
    var numberOfSeats: Int,
    var weight: Int,
    var isAC: Boolean,
    var route: RouteResponse
)

@Serializable
data class RouteRequest(
    var source: String,
    var destination: String,
    var date: String,
    var mileage: Int,
    var price: Int,
    var roundTrip: Boolean,
)

@Serializable
data class RouteResponse(
    var id: Long,
    var source: String,
    var destination: String,
    var date: String,
    var mileage: Int,
    var price: Int,
    var roundTrip: Boolean,
)
