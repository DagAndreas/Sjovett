
Installasjonsguide:

     1. Klon GitHub-prosjektet / Unzip prosjektmappen.
     2. Åpne prosjektet i Android Studio.
     3. Naviger deg til README.md.
     4. Kopier de tre nøklene og lim dem inn nederst i local.properties.
     5. Kopier dependencies og lim dem inn i build.gradle (Module: App). Du kan lime over andre dependencies som allerede ligger der.
     6. Trykk på Sync Now i meldingen som kommer over kodeboksen.
     7. Åpne Device Manager, trykk på Create device og velg Pixel 2 med API-level 28.
     8. Start emulatoren, åpne Extended controls (tre prikker over hverandre)
     9. Under Location dra kartet til Norge og trykk på et sted langs kysten. Trykk på SAVE POINT og deretter Set location.
    10. Start appen med å trykke på den grønne pilen i menyen øverst.

legg inn i local.properties:

    GOOGLE_MAPS_API_KEY = AIzaSyCtUtAv6RFDzClFOc8LbxK5B5cRybtb1KI
    MET_KEY = dc1732ae-a8a0-4dd5-8052-26094bfbca11
    GEO_KEY = "Ef8bkbpLK+TeaAk43qgYqw==mZBU9A3ckObEAYY7"
    SEAORLAND_KEY = "6e4ec9b570msh7ec67a9af607dbdp1fce72jsnddb99294423f"


gradle.build:

    dependencies {
        implementation "androidx.core:core-ktx:1.9.0"
        implementation "androidx.core:core:1.9.0"
        implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.5.1'
        implementation 'androidx.activity:activity-compose:1.6.1'
        // Need this or MapEffect throws exception.
        implementation "androidx.appcompat:appcompat:1.5.1"

        // Compose
        // From https://www.jetpackcomposeversion.com/
        implementation "androidx.compose.ui:ui:1.3.2"
        implementation "androidx.compose.material:material:1.3.1"

        // Google maps
        implementation 'com.google.android.gms:play-services-maps:18.1.0'
        implementation 'com.google.android.gms:play-services-location:21.0.1'
        // Google maps for compose
        implementation 'com.google.maps.android:maps-compose:2.8.0'

        // KTX for the Maps SDK for Android
        implementation 'com.google.maps.android:maps-ktx:3.2.1'
        // KTX for the Maps SDK for Android Utility Library
        implementation 'com.google.maps.android:maps-utils-ktx:3.2.1'

        // Hilt
        implementation "com.google.dagger:hilt-android:2.42"
        implementation 'junit:junit:4.12'
        implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
        kapt "com.google.dagger:hilt-compiler:2.42"

        //Ikoner
        implementation "androidx.compose.material:material-icons-extended:$compose_version"
        implementation "androidx.compose.material:material-icons-core"

        //Navigasjon
        implementation "androidx.navigation:navigation-compose:2.4.0-alpha06"

        implementation 'androidx.core:core-ktx:1.7.0'
        implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.3.1'
        implementation 'androidx.activity:activity-compose:1.3.1'
        implementation "androidx.compose.ui:ui:$compose_version"
        implementation "androidx.compose.ui:ui-tooling-preview:$compose_version"
        implementation 'androidx.compose.material3:material3:1.0.0-alpha11'
        testImplementation 'junit:junit:4.13.2'
        androidTestImplementation 'androidx.test.ext:junit:1.1.5'
        androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
        androidTestImplementation "androidx.compose.ui:ui-test-junit4:$compose_version"
        debugImplementation "androidx.compose.ui:ui-tooling:$compose_version"
        debugImplementation "androidx.compose.ui:ui-test-manifest:$compose_version"

        implementation 'com.google.android.gms:play-services-maps:18.1.0'


        //imports for viewmodel
        implementation"androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"
        implementation"androidx.compose.runtime:runtime-livedata:$compose_version"

        implementation 'com.google.android.material:material:1.5.0'
        implementation "androidx.compose.material3:material3:1.1.0-alpha03" // Material 3

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")


        def ktor_version = "2.2.3"
        implementation"io.ktor:ktor-client-android:$ktor_version"
        implementation"io.ktor:ktor-client-content-negotiation:$ktor_version"
        implementation"io.ktor:ktor-serialization-gson:$ktor_version"
        implementation "io.ktor:ktor-serialization-kotlinx-json:$ktor_version"

        implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"

    }
