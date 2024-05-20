package ua.kpi.its.lab.security.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "train")
class Train(
    @Column
    var model: String,

    @Column
    var manufacturer: String,

    @Column
    var type: String,

    @Column
    var dateOfCommissioning: Date,

    @Column
    var numberOfSeats: Int,

    @Column
    var weight: Int,

    @Column
    var isAC: Boolean,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "route_id", referencedColumnName = "id")
    var route: Route
) : Comparable<Train> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Train): Int {
        val equal = this.model == other.model && this.dateOfCommissioning.time == other.dateOfCommissioning.time
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Train(model=$model, manufactureDate=$dateOfCommissioning, route=$route)"
    }
}

@Entity
@Table(name = "routes")
class Route(
    @Column
    var source: String,

    @Column
    var destination: String,

    @Column
    var date: Date,

    @Column
    var mileage: Int,

    @Column
    var price: Int,

    @Column
    var roundTrip: Boolean,

    @OneToOne(mappedBy = "route")
    var train: Train? = null,
): Comparable<Route> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Route): Int {
        val equal = this.destination == other.destination && this.source == other.source
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Route(source=$source, destination=$destination)"
    }
}