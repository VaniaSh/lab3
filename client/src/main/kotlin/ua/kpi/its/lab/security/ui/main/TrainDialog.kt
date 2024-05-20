package ua.kpi.its.lab.security.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
    onConfirm: (TrainRequest) -> Unit
) {
    var model by remember { mutableStateOf(train?.model ?: "") }
    var manufacturer by remember { mutableStateOf(train?.manufacturer ?: "") }
    var type by remember { mutableStateOf(train?.type ?: "") }
    var dateOfCommissioning by remember { mutableStateOf(train?.dateOfCommissioning ?: "") }
    var numberOfSeats by remember { mutableStateOf(train?.numberOfSeats?.toString() ?: "") }
    var weight by remember { mutableStateOf(train?.weight?.toString() ?: "") }
    var isAC by remember { mutableStateOf(train?.isAC ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp).wrapContentSize()) {
            Column(
                modifier = Modifier.padding(16.dp, 8.dp).width(IntrinsicSize.Max).verticalScroll(
                    rememberScrollState()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (train == null) {
                    Text("Create train")
                } else {
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                try {
                                    val request = TrainRequest(
                                        model, manufacturer, type, dateOfCommissioning,
                                        numberOfSeats.toInt(), weight.toInt(), isAC, null
                                    )
                                    onConfirm(request)
                                } catch (e: Exception) {
                                    val msg = e.toString()
                                    onError(msg)
                                }
                            }
                        }
                    ) {
                        if (train == null) {
                            Text("Next")
                        } else {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RouteDialog(
    train: TrainResponse?,
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    onDismiss: () -> Unit,
    onError: (String) -> Unit,
    onConfirm: (RouteRequest) -> Unit
) {
    val route = train?.route
    var source by remember { mutableStateOf(route?.source ?: "") }
    var destination by remember { mutableStateOf(route?.destination ?: "") }
    var date by remember { mutableStateOf(route?.date ?: "") }
    var mileage by remember { mutableStateOf(route?.mileage?.toString() ?: "") }
    var price by remember { mutableStateOf(route?.price?.toString() ?: "") }
    var roundTrip by remember { mutableStateOf(route?.roundTrip ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp).wrapContentSize()) {
            Column(
                modifier = Modifier.padding(16.dp, 8.dp).width(IntrinsicSize.Max).verticalScroll(
                    rememberScrollState()
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Route info")
                TextField(source, { source = it }, label = { Text("Source") })
                TextField(destination, { destination = it }, label = { Text("Destination") })
                TextField(date, { date = it }, label = { Text("Date") })
                TextField(mileage, { mileage = it }, label = { Text("Mileage") })
                TextField(price, { price = it }, label = { Text("Price") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(roundTrip, { roundTrip = it })
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
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                try {
                                    val request = RouteRequest(
                                        source, destination, date, mileage.toInt(), price.toInt(), roundTrip
                                    )
                                    onConfirm(request)
                                } catch (e: Exception) {
                                    val msg = e.toString()
                                    onError(msg)
                                }
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
