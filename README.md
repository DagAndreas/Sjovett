legg inn i local.properties:
{

    GOOGLE_MAPS_API_KEY=AIzaSyCtUtAv6RFDzClFOc8LbxK5B5cRybtb1KI

}

gradle.build:
{

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

        //coil er for å laste ned bilder
        implementation("io.coil-kt:coil:2.2.2")
        implementation("io.coil-kt:coil-compose:2.2.2")
        implementation 'com.google.android.material:material:1.5.0'
        implementation "androidx.compose.material3:material3:1.1.0-alpha03" // Material 3

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")


        def ktor_version = "2.2.3"
        implementation"io.ktor:ktor-client-android:$ktor_version"
        implementation"io.ktor:ktor-client-content-negotiation:$ktor_version"
        implementation"io.ktor:ktor-serialization-gson:$ktor_version"
        implementation "io.ktor:ktor-serialization-kotlinx-json:$ktor_version"
    }

}

# In2000-Prosjekt
Lag og push alltid til en egen branch. Aldri til main. På den måten holder vi main-branchen fri for bugs.
Det anbefales å bruke ssh-nøkkel: [Guide til ssh-nøkkel og github her](https://www.uio.no/tjenester/it/maskin/filer/versjonskontroll/github.html#ssh-nokler)


Steg:
1. git clone [repo](https://github.uio.no/dafolvel/In2000-Prosjekt.git)
2. git branch [branch_navn] //uten anførselstegn
3. git checkout [branch_navn]
4. // TO DO: gjør koden
5. git add .
6. git commit -m "[kort beskrivelse av endringene]"
7. git push --set-upstream origin [branch_navn]
8. [Åpne Prosjektet](https://github.uio.no/dafolvel/In2000-Prosjekt)
9. trykk på "Pull requests"
10. "New Pull request"
11. Base: main <- Compare: [branch_navn]
12. Create pull request

13. Skriv kommentar av endringene du gjorde
14. Assign deg og de som gjorde enrdingene
15. Reviewers for de som skal se gjennom og godkjenne
16. Create pull request
17. Ta en paus
