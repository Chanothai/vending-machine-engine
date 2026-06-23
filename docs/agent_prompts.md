Prompt Create script for create structure project.

Act as an Expert Android Architect. I want to scaffold a new Android project using Multi-module, Clean Architecture, MVI pattern, and Jetpack Compose.
Please generate a Bash script (init_project.sh) that creates the complete directory structure and placeholder Kotlin files for a feature named 'Login'.
The structure should include:
1. Core modules (:shared:api, :shared:core, :shared:feature)
2. Feature module (:feature)
3. Foundation module (:foundation)
Make sure the script creates the necessary build.gradle.kts files with basic placeholder setups.


Act as an Expert Android Architect and DevOps Engineer. Your task is to write a robust Bash script named `create_feature.sh` that scaffolds a new feature module in an Android project.

The script must follow the "Package-by-Feature" architectural style (1 feature = 1 module, separated by packages inside).

**Requirements & Best Practices:**
1. **Parameter Validation:** The script must accept exactly two argument (the module type and feature name). If missing, print a clear error message and exit.
2. **Naming Conventions:** Automatically handle string manipulation:
    - Convert the feature name to lowercase for folder and package names (e.g., "Payment" -> "payment").
    - Convert the feature name to Capitalized for Kotlin class names (e.g., "payment" -> "Payment").
3. **Directory Structure:** Create the standard Android Library structure:
    - Base directory: `feature/<feature_lower>`
    - Source code path: `src/main/java/com/yourcompany/app/feature/<feature_lower>`
4. **Clean Architecture + MVI Packages:** Inside the source directory, create the following empty packages:
    - `domain/entity`, `domain/usecase`, `domain/repository`
    - `data/repository`, `data/remote`
    - `presentation/mvi`, `presentation/ui`
5. **Auto-Generate Files:**
    - A standard `build.gradle.kts` for an Android library module.
    - An MVI Contract file (`<FeatureCap>Contract.kt`) containing an empty State data class, an Intent sealed interface, and an Effect sealed interface.
    - A ViewModel file (`<FeatureCap>ViewModel.kt`) that extends `ViewModel` and imports the MVI Contract.
6. **User Guidance:** Print a success message at the end, explicitly reminding the user to add `include(":feature:<feature_lower>")` to their `settings.gradle.kts` file.
7. **Register new modules in settings.gradle**

Please output ONLY the Bash script code block, complete with clear comments explaining each section.
