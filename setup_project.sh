#!/bin/bash

# Configuration
PACKAGE_NAME="com.example.aidevelopment"
PACKAGE_PATH="com/example/aidevelopment"

echo "🚀 Scaffolding Multi-module Clean Architecture + MVI Project..."

# Modules to create
modules=(
    "shared/core"
    "shared/api"
    "shared/feature"
    "foundation"
    "feature"
)

# 1. Create directory structures and AndroidManifests
for module in "${modules[@]}"; do
    mkdir -p "$module/src/main/kotlin/$PACKAGE_PATH"
    cat <<EOF > "$module/src/main/AndroidManifest.xml"
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
EOF
done

# 2. Create build.gradle.kts for Core Modules
cat <<EOF > core/ui/build.gradle.kts
plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "$PACKAGE_NAME.core.ui"
    compileSdk = 35
    buildFeatures { compose = true }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
}
EOF

cat <<EOF > core/network/build.gradle.kts
plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "$PACKAGE_NAME.core.network"
    compileSdk = 35
}
EOF

# 3. Create build.gradle.kts for Login Feature
cat <<EOF > feature/login/domain/build.gradle.kts
plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "$PACKAGE_NAME.feature.login.domain"
    compileSdk = 35
}
EOF

cat <<EOF > feature/login/data/build.gradle.kts
plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "$PACKAGE_NAME.feature.login.data"
    compileSdk = 35
}

dependencies {
    implementation(project(":feature:login:domain"))
    implementation(project(":core:network"))
}
EOF

cat <<EOF > feature/login/ui/build.gradle.kts
plugins {
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "$PACKAGE_NAME.feature.login.ui"
    compileSdk = 35
    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":feature:login:domain"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.core.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
}
EOF

# 4. Create MVI Components
UI_DIR="feature/login/ui/src/main/kotlin/$PACKAGE_PATH"

cat <<EOF > "$UI_DIR/LoginContract.kt"
package $PACKAGE_NAME.feature.login.ui

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

sealed class LoginIntent {
    data class LoginClicked(val username: String, val password: String) : LoginIntent()
}

sealed class LoginEffect {
    data class ShowError(val message: String) : LoginEffect()
    object NavigateToHome : LoginEffect()
}
EOF

cat <<EOF > "$UI_DIR/LoginViewModel.kt"
package $PACKAGE_NAME.feature.login.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.LoginClicked -> performLogin(intent.username, intent.password)
        }
    }

    private fun performLogin(username: String, psw: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // TODO: Call use case from domain layer
            _state.update { it.copy(isLoading = false, isSuccess = true) }
            _effect.emit(LoginEffect.NavigateToHome)
        }
    }
}
EOF

cat <<EOF > "$UI_DIR/LoginScreen.kt"
package $PACKAGE_NAME.feature.login.ui

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Login Screen", style = MaterialTheme.typography.headlineMedium)
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        Button(onClick = { viewModel.handleIntent(LoginIntent.LoginClicked(\"user\", \"pass\")) }) {
            Text(\"Login\")
        }
    }
}
EOF

# 5. Register modules in settings.gradle.kts
echo "" >> settings.gradle.kts
for module in "${modules[@]}"; do
    gradle_path=":${module//\//:}"
    grep -q "$gradle_path" settings.gradle.kts || echo "include(\"$gradle_path\")" >> settings.gradle.kts
done

echo "✅ Scaffolding complete! Run 'Gradle Sync' in Android Studio."
