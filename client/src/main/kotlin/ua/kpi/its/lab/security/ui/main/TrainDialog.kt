
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import ua.kpi.its.lab.security.dto.RouteRequest
import ua.kpi.its.lab.security.dto.TrainRequest
import ua.kpi.its.lab.security.dto.TrainResponse

@Composable
fun TrainDialog(
    train: TrainResponse?,
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    onDismiss: () -> Unit,
    onError: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    val route = train?.route

    var model by remember { mutableStateOf(train?.model ?: "") }
    var manufacturer by remember { mutableStateOf(train?.manufacturer ?: "") }
    var type by remember { mutableStateOf(train?.type ?: "") }
    var dateOfCommissioning by remember { mutableStateOf(train?.dateOfCommissioning ?: "") }
    var numberOfSeats by remember { mutableStateOf(train?.numberOfSeats?.toString() ?: "") }
    var weight by remember { mutableStateOf(train?.weight.toString()) }
    var isAC by remember { mutableStateOf(train?.isAC ?: false) }
    var routeSource by remember { mutableStateOf(route?.source ?: "") }
    var routeDestination by remember { mutableStateOf(route?.destination ?: "") }
    var routeDate by remember { mutableStateOf(route?.date ?: "") }
    var routeMileage by remember { mutableStateOf(route?.mileage.toString()) }
    var routePrice by remember { mutableStateOf(route?.price.toString()) }
    var isRoundTrip by remember { mutableStateOf(route?.roundTrip ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp).wrapContentSize()) {
            Column(
                modifier = Modifier.padding(16.dp, 8.dp).width(IntrinsicSize.Max).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (train == null) {
                    Text("Create train")
                }
                else {
                    Text("Update train")
                }

                HorizontalDivider()
                Text("Train info")
                TextField(model, { model = it }, label = { Text("Model") })
                TextField(manufacturer, { manufacturer = it }, label = { Text("Manufacturer") })
                TextField(type, { type = it }, label = { Text("Type") })
                TextField(dateOfCommissioning, { dateOfCommissioning = it }, label = { Text("Date Of Commissioning") })
                TextField(numberOfSeats, { numberOfSeats = it }, label = { Text("Number Of Seats") })
                TextField(weight, { weight = it }, label = { Text("Weight") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(isAC, { isAC = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("AC")
                }

                HorizontalDivider()
                Text("Route info")
                TextField(routeSource, { routeSource = it }, label = { Text("Source") })
                TextField(routeDestination, { routeDestination = it }, label = { Text("Destination") })
                TextField(routeDate, { routeDate = it }, label = { Text("Date") })
                TextField(routeMileage, { routeMileage = it }, label = { Text("Mileage") })
                TextField(routePrice, { routePrice = it }, label = { Text("Price") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(isRoundTrip, { isRoundTrip = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Is Round Trip")
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.fillMaxWidth(0.1f))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                try {
                                    val request = TrainRequest(
                                        model, manufacturer, type, dateOfCommissioning, numberOfSeats.toInt(), weight.toInt(), isAC,
                                        RouteRequest(
                                            routeSource, routeDestination, routeDate, routeMileage.toInt(),
                                            routePrice.toInt(),isRoundTrip
                                        )
                                    )
                                    val response = if (train == null) {
                                        client.post("http://localhost:8080/trains") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    } else {
                                        client.put("http://localhost:8080/trains/${train.id}") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    }
                                    require(response.status.isSuccess())
                                    onConfirm()
                                }
                                catch (e: Exception) {
                                    val msg = e.toString()
                                    onError(msg)
                                }
                            }
                        }
                    ) {
                        if (train == null) {
                            Text("Create")
                        }
                        else {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}
