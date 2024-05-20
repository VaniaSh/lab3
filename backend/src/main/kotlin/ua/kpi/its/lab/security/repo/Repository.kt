package ua.kpi.its.lab.security.repo

import org.springframework.data.jpa.repository.JpaRepository
import ua.kpi.its.lab.security.entity.Route
import ua.kpi.its.lab.security.entity.Train

interface TrainRepository : JpaRepository<Train, Long>

interface RouteRepository : JpaRepository<Route, Long>