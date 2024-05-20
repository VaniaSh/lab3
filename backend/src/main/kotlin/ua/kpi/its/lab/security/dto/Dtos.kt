package ua.kpi.its.lab.security.dto

import java.util.Date

data class TrainRequest(
    var model: String,
    var manufacturer: String,
    var type: String,
    var dateOfCommissioning: Date,
    var numberOfSeats: Int,
    var weight: Int,
    var isAC: Boolean,
    var route: RouteRequest
)

data class TrainResponse(
    var id: Long,
    var model: String,
    var manufacturer: String,
    var type: String,
    var dateOfCommissioning: Date,
    var numberOfSeats: Int,
    var weight: Int,
    var isAC: Boolean,
    var route: RouteResponse
)

data class RouteRequest(
    var source: String,
    var destination: String,
    var date: Date,
    var mileage: Int,
    var price: Int,
    var roundTrip: Boolean,
    )

data class RouteResponse(
    var id: Long,
    var source: String,
    var destination: String,
    var date: Date,
    var mileage: Int,
    var price: Int,
    var roundTrip: Boolean,
)