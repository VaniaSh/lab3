package ua.kpi.its.lab.security.svc.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.kpi.its.lab.security.dto.RouteResponse
import ua.kpi.its.lab.security.dto.TrainRequest
import ua.kpi.its.lab.security.dto.TrainResponse
import ua.kpi.its.lab.security.entity.Route
import ua.kpi.its.lab.security.entity.Train
import ua.kpi.its.lab.security.repo.TrainRepository
import ua.kpi.its.lab.security.svc.TrainService
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class TrainServiceImpl @Autowired constructor(
    private val repository: TrainRepository
): TrainService {
    override fun create(train: TrainRequest): TrainResponse {
        val route = train.route
        val newRoute = Route(
            source = route.source,
            destination = route.destination,
            date = route.date,
            price = route.price,
            mileage = route.mileage,
            roundTrip = route.roundTrip
        )
        var newTrain = Train(
        model=train.model,
            manufacturer=train.manufacturer,
            type=train.type,
            dateOfCommissioning=train.dateOfCommissioning,
            numberOfSeats=train.numberOfSeats,
            weight=train.weight,
            isAC=train.isAC,
            route = newRoute
        )

        newTrain = this.repository.save(newTrain)
        val vehicleResponse = this.vehicleEntityToDto(newTrain)
        return vehicleResponse
    }

    override fun read(): List<TrainResponse> {
        return this.repository.findAll().map(this::vehicleEntityToDto)
    }

    override fun readById(id: Long): TrainResponse {
        val vehicle = this.getVehicleById(id)
        val vehicleResponse = this.vehicleEntityToDto(vehicle)
        return vehicleResponse
    }

    override fun updateById(id: Long, train: TrainRequest): TrainResponse {
        val oldVehicle = this.getVehicleById(id)
        val route = train.route

        oldVehicle.apply {
            model = train.model
            type = train.type
            manufacturer = train.manufacturer
            dateOfCommissioning = train.dateOfCommissioning
            weight = train.weight
            numberOfSeats = train.numberOfSeats
            isAC = train.isAC
        }
        oldVehicle.route.apply {
            source = route.source
            destination = route.destination
            date = route.date
            mileage = route.mileage
            price = route.price
            roundTrip = route.roundTrip
        }
        val newVehicle = this.repository.save(oldVehicle)
        val vehicleResponse = this.vehicleEntityToDto(newVehicle)
        return vehicleResponse
    }

    override fun deleteById(id: Long): TrainResponse {
        val vehicle = this.getVehicleById(id)
        this.repository.delete(vehicle)
        val vehicleResponse = vehicleEntityToDto(vehicle)
        return vehicleResponse
    }

    private fun getVehicleById(id: Long): Train {
        return this.repository.findById(id).getOrElse {
            throw IllegalArgumentException("Vehicle not found by id = $id")
        }
    }

    private fun vehicleEntityToDto(train: Train): TrainResponse {
        return TrainResponse(
            id = train.id,
            model = train.model,
            type = train.type,
            manufacturer = train.manufacturer,
            dateOfCommissioning = train.dateOfCommissioning,
            weight = train.weight,
            numberOfSeats = train.numberOfSeats,
            isAC = train.isAC,
            route = this.routeEntityToDto(train.route)
        )
    }

    private fun routeEntityToDto(route: Route): RouteResponse {
        return RouteResponse(
            id = route.id,
            source = route.source,
            destination = route.destination,
            date = route.date,
            mileage = route.mileage,
            price = route.price,
            roundTrip = route.roundTrip,
        )
    }

    private fun dateToString(date: Date): String {
        val instant = date.toInstant()
        val dateTime = instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }

    private fun stringToDate(date: String): Date {
        val dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val instant = dateTime.toInstant(ZoneOffset.UTC)
        return Date.from(instant)
    }

    private fun priceToString(price: BigDecimal): String = price.toString()

    private fun stringToPrice(price: String): BigDecimal = BigDecimal(price)
}