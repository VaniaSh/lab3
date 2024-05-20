package ua.kpi.its.lab.security.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.kpi.its.lab.security.dto.TrainRequest
import ua.kpi.its.lab.security.dto.TrainResponse
import ua.kpi.its.lab.security.svc.TrainService
import java.time.Instant

@RestController
@RequestMapping("/trains")
class TrainController @Autowired constructor(
    private val trainService: TrainService
) {
    /**
     * Gets the list of all vehicles
     *
     * @return: List of VehicleResponse
     */
    @GetMapping(path = ["", "/"])
    fun vehicles(): List<TrainResponse> = trainService.read()

    /**
     * Reads the vehicle by its id
     *
     * @param id: id of the vehicle
     * @return: VehicleResponse for the given id
     */
    @GetMapping("{id}")
    fun readTrain(@PathVariable("id") id: Long): ResponseEntity<TrainResponse> {
        return wrapNotFound { trainService.readById(id) }
    }

    /**
     * Creates a new vehicle instance
     *
     * @param vehicle: VehicleRequest with set properties
     * @return: VehicleResponse for the created vehicle
     */
    @PostMapping(path = ["", "/"])
    fun createTrain(@RequestBody train: TrainRequest): TrainResponse {
        return trainService.create(train)
    }

    /**
     * Updates existing vehicle instance
     *
     * @param vehicle: VehicleRequest with properties set
     * @return: VehicleResponse of the updated vehicle
     */
    @PutMapping("{id}")
    fun updateTrain(
        @PathVariable("id") id: Long,
        @RequestBody traine: TrainRequest
    ): ResponseEntity<TrainResponse> {
        return wrapNotFound { trainService.updateById(id, traine)}
    }

    /**
     * Deletes existing vehicle instance
     *
     * @param id: id of the vehicle
     * @return: VehicleResponse of the deleted vehicle
     */
    @DeleteMapping("{id}")
    fun deleteTrain(
        @PathVariable("id") id: Long
    ): ResponseEntity<TrainResponse> {
        return wrapNotFound { trainService.deleteById(id) }
    }

    fun <T>wrapNotFound(call: () -> T): ResponseEntity<T> {
        return try {
            // call function for result
            val result = call()
            // return "ok" response with result body
            ResponseEntity.ok(result)
        }
        catch (e: IllegalArgumentException) {
            // catch not-found exception
            // return "404 not-found" response
            ResponseEntity.notFound().build()
        }
    }
}


@RestController
@RequestMapping("/auth")
class AuthenticationTokenController @Autowired constructor(
    private val encoder: JwtEncoder
) {
    private val authTokenExpiry: Long = 3600L // in seconds

    @PostMapping("token")
    fun token(auth: Authentication): String {
        val now = Instant.now()
        val scope = auth
            .authorities
            .joinToString(" ", transform = GrantedAuthority::getAuthority)
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(authTokenExpiry))
            .subject(auth.name)
            .claim("scope", scope)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
