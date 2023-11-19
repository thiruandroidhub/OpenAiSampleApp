import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay

class SplashScreen: Screen {
    @Composable
    override fun Content() {
        var readyToLaunch by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(5000)  // 5 seconds delay
            readyToLaunch = true

        }
        val navigator = LocalNavigator.currentOrThrow
        if(readyToLaunch) navigator.push(BuilderScreen())
        Text("_____ SPLASH SCREEN _____")

    }




}