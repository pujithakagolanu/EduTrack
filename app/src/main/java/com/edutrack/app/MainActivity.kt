package com.edutrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.edutrack.app.navigation.EduTrackAppContent
import com.edutrack.app.ui.theme.EduTrackTheme
import com.edutrack.app.viewmodel.StudyTrackerViewModel
import com.edutrack.app.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: StudyTrackerViewModel by viewModels {
        val app = application as EduTrackApplication
        ViewModelFactory(app.subjectRepository, app.studySessionRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as EduTrackApplication

        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDark) }

            EduTrackTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    EduTrackAppContent(
                        navController = navController,
                        studyTrackerViewModel = viewModel,
                        application = app,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}
