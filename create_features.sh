#!/bin/bash

# ==============================================================================
# Android Feature Scaffolder (Clean Architecture + MVI)
# ==============================================================================
# run prompt chmod +x [file name.sh] before run script #
# run script ./[file name.sh] [module type] [feature name]

# 1. Parameter Validation
if [ "$#" -ne 2 ]; then
    echo "❌ Error: Missing parameters."
    echo "Usage: ./create_feature.sh <ModuleType> <FeatureName>"
    echo "Example: ./create_feature.sh feature Payment"
    echo "Example: ./create_feature.sh core Network"
    exit 1
fi

MODULE_TYPE=$1
FEATURE_NAME=$2

# 2. Naming Conventions
# Convert names to lowercase for folders and packages
MODULE_LOWER=$(echo "$MODULE_TYPE" | tr '[:upper:]' '[:lower:]')
FEATURE_LOWER=$(echo "$FEATURE_NAME" | tr '[:upper:]' '[:lower:]')
# Convert feature name to Capitalized for Kotlin classes
FEATURE_CAP="$(echo "${FEATURE_LOWER:0:1}" | tr '[:lower:]' '[:upper:]')${FEATURE_LOWER:1}"

# Configuration: Update BASE_PACKAGE to match your project
BASE_PACKAGE="com.example.aidevelopment"
PACKAGE_NAME="$BASE_PACKAGE.$MODULE_LOWER.$FEATURE_LOWER"
PACKAGE_PATH=$(echo "$PACKAGE_NAME" | tr '.' '/')

# 3. Directory Structure
MODULE_DIR="$MODULE_LOWER/$FEATURE_LOWER"
SRC_DIR="$MODULE_DIR/src/main/kotlin/$PACKAGE_PATH"

echo "🚀 Scaffolding Module: :$MODULE_LOWER:$FEATURE_LOWER (Class: $FEATURE_CAP)..."

# 4. Create Clean Architecture + MVI Package Structure
mkdir -p "$SRC_DIR/domain/entity"
mkdir -p "$SRC_DIR/domain/usecase"
mkdir -p "$SRC_DIR/domain/repository"
mkdir -p "$SRC_DIR/data/repository"
mkdir -p "$SRC_DIR/data/remote"
mkdir -p "$SRC_DIR/presentation/mvi"
mkdir -p "$SRC_DIR/presentation/ui"

# Create standard Android resource directory
mkdir -p "$MODULE_DIR/src/main/res"

# 5. Auto-Generate Files

# build.gradle.kts
cat <<EOF > "$MODULE_DIR/build.gradle.kts"
plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "$PACKAGE_NAME"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}
EOF

# AndroidManifest.xml
cat <<EOF > "$MODULE_DIR/src/main/AndroidManifest.xml"
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
</manifest>
EOF

# MVI Contract file
cat <<EOF > "$SRC_DIR/presentation/mvi/${FEATURE_CAP}Contract.kt"
package $PACKAGE_NAME.presentation.mvi

data class ${FEATURE_CAP}State(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ${FEATURE_CAP}Intent {
    object Init : ${FEATURE_CAP}Intent
}

sealed interface ${FEATURE_CAP}Effect {
    data class ShowError(val message: String) : ${FEATURE_CAP}Effect
}
EOF

# ViewModel file
cat <<EOF > "$SRC_DIR/presentation/${FEATURE_CAP}ViewModel.kt"
package $PACKAGE_NAME.presentation

import androidx.lifecycle.ViewModel
import $PACKAGE_NAME.presentation.mvi.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ${FEATURE_CAP}ViewModel : ViewModel() {

    private val _state = MutableStateFlow(${FEATURE_CAP}State())
    val state: StateFlow<${FEATURE_CAP}State> = _state.asStateFlow()

    fun handleIntent(intent: ${FEATURE_CAP}Intent) {
        when (intent) {
            is ${FEATURE_CAP}Intent.Init -> {
                // TODO: Implement initialization logic
            }
        }
    }
}
EOF

# 6. Register module in settings.gradle.kts
GRADLE_PATH=":$MODULE_LOWER:$FEATURE_LOWER"
if grep -q "\"$GRADLE_PATH\"" settings.gradle.kts || grep -q "'$GRADLE_PATH'" settings.gradle.kts; then
    echo "ℹ️ Module $GRADLE_PATH already registered in settings.gradle.kts"
else
    echo "include(\"$GRADLE_PATH\")" >> settings.gradle.kts
    echo "✅ Registered $GRADLE_PATH in settings.gradle.kts"
fi

# 7. User Guidance
echo "========================================================================"
echo "✅ Module '$GRADLE_PATH' created successfully!"
echo "------------------------------------------------------------------------"
echo "📂 Path: $MODULE_DIR"
echo "📦 Package: $PACKAGE_NAME"
echo "------------------------------------------------------------------------"
echo "🚀 Success! Run 'Gradle Sync' to start working on your new module."
echo "========================================================================"