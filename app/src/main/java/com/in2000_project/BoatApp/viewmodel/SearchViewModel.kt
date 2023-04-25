package com.in2000_project.BoatApp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.in2000_project.BoatApp.data.ApiDataSource
import com.in2000_project.BoatApp.data.GeoCodeUiState
import com.in2000_project.BoatApp.model.geoCode.CityName
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchViewModel: ViewModel() {
    val _dataSource = ApiDataSource()
    private val _geoCodeUiState = MutableStateFlow(GeoCodeUiState())
    val geoCodeUiState = _geoCodeUiState.asStateFlow()

    private val _locationSearch = MutableStateFlow("")
    val locationSearch = _locationSearch.asStateFlow()

    private val _searchInProgress = MutableStateFlow(false)
    val searchInProgress = _searchInProgress.asStateFlow()

    private val _cities = MutableStateFlow(getAllCities(norwegianCities))
    val cities = locationSearch
        .onEach { _searchInProgress.update{true}}
        //.debounce(100L)
        .combine(_cities){ text, cities ->
            if(text.isBlank()){
                cities //viser alle steder om man ikke har begynt søk
            }else{
                delay(500L)
                cities.filter{
                    it.matchesSearch(text)
                }
            }
        }
        .onEach{_searchInProgress.update{false}}
        .stateIn( //for å få stateFlow
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _cities.value
        )


    fun onSearchChange(text: String){
        _locationSearch.value = text.replace("\n", "")
    }


    suspend fun fetchCityData(cityName: String) {
        _locationSearch.update{
            if (cityName != null){
                cityName
            } else {
                ""
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val url = "https://api.api-ninjas.com/v1/geocoding?city=$cityName&country=Norway"
            _geoCodeUiState.update {
                // setter warningList til å være en MetAlertsResponse
                //val deferred = async { (it.copy(cityList = _dataSource.fetchGeoCodeData(url))) }
                //delay(5000)
                //deferred.await()
                it.copy(cityList = _dataSource.fetchGeoCodeData(url))

            }
        }
    }

    fun resetCityData() {
        _geoCodeUiState.update {
            it.copy(cityList = emptyList())
        }
    }
}


//todo: Legge byene i string resources
val norwegianCities = listOf(
    "Reine",
    "Bergsida",
    "Stetteremma",
    "Movatn",
    "Namsskogan Sentrum",
    "Vormsund",
    "Vanse",
    "Orkanger/Fannrem",
    "Hemsedal",
    "Grønlund",
    "Mork",
    "Prestegårdshagen",
    "Li",
    "Tingnes",
    "Barkåker",
    "Skei/Surnadalsøra",
    "Skjervøy",
    "Hermansverk/Leikanger",
    "Austnes",
    "Vikersund",
    "Sekkingstad",
    "Hasle",
    "Mønshaugen-Bjørgum",
    "Lillehammer",
    "Ydstebøhamn",
    "Oddevall/Sjåstad",
    "Ølen",
    "Berlevåg",
    "Tranby",
    "Malme",
    "Svolvær",
    "Talvik",
    "Bjølstad",
    "Ål",
    "Strand",
    "Ågotnes",
    "Leirsund",
    "Roald",
    "Longum",
    "Mandal",
    "Sirevåg",
    "Balestrand",
    "Svennevik",
    "Stanghelle",
    "Leknes",
    "Sjøvegan",
    "Noresund",
    "Vardø",
    "Jørpeland",
    "Ålvik",
    "Karmøy",
    "Malm",
    "Kylstad",
    "Notodden",
    "Spillum",
    "Sørvågen",
    "Bjørnevatn",
    "Konglungen",
    "Sandøya",
    "Reipå",
    "Kristiansand",
    "Tofte",
    "Sem",
    "Flåm",
    "Bruflat",
    "Siljan",
    "Sandeid",
    "Storforshei",
    "Fagerstrand",
    "Stokke",
    "Sander",
    "Å I Åfjord",
    "Midsund",
    "Bekkelaget",
    "Kapp",
    "Hem",
    "Hollingen",
    "Namdalseid",
    "Nesbygda",
    "Sveggen",
    "Prestmoen",
    "Melsomvik",
    "Bergen",
    "Husnes",
    "Grøa",
    "Ploganes",
    "Tolga",
    "Rognan",
    "Årdalstangen",
    "Leinstrand",
    "Bryne",
    "Kjøpsvik",
    "Høyjord",
    "Finnsnes",
    "Hellvik",
    "Husøy",
    "Åsgrenda",
    "Skage",
    "Lunner",
    "Kvalsund",
    "Berg",
    "Kjosen",
    "Halmstad",
    "Foldrøy",
    "Hellesylt",
    "Drøbak",
    "Espeland",
    "Davanger",
    "Lia",
    "Moltustranda",
    "Eidbukta",
    "Neslandsvatn",
    "Heiås",
    "Nordkisa",
    "Steinsåsen",
    "Alternes",
    "Nesjestranda",
    "Norderhov",
    "Klevjer",
    "Stavsjø",
    "Eidsvik",
    "Askgrenda",
    "Storslett",
    "Austreim",
    "Nordkjosbotn",
    "Aursmoen",
    "Stadsbygd",
    "Vikeså",
    "Straumen",
    "Degernes",
    "Feda",
    "Egersund",
    "Råkvågen",
    "Konnerud",
    "Aulifeltet",
    "Kirkebygden",
    "Skarnes",
    "Hjelmelandsvågen",
    "Bodø",
    "Vikedal",
    "Hægeland",
    "Haugsbygda",
    "Aurlandsvangen",
    "Åndalsnes",
    "Rykene",
    "Gjesdal",
    "Skre",
    "Moane",
    "Tresfjord",
    "Skjeberg",
    "Hustadvika",
    "Mo",
    "Veggli",
    "Ålgård/Figgjo",
    "Vikavågen",
    "Vestfossen",
    "Preststranda",
    "Sauda",
    "Solerød",
    "Liknes",
    "Lærdalsøyri",
    "Hyggen",
    "Grodås",
    "Drag",
    "Kjellmyra",
    "Blaker",
    "Leksvik",
    "Lysthaugen",
    "Fetsund-Østersund",
    "Lauvsnes",
    "Sylte",
    "Fredrikstad",
    "Øyenkilen",
    "Hogsetfeltet",
    "Røyrvik",
    "Farsund",
    "Lonevåg",
    "Sysle",
    "Meldal",
    "Kviteseid",
    "Dalemarka",
    "Seim",
    "Ormåsen",
    "Ekeberg",
    "Kaland",
    "Elnesvågen",
    "Rotnes",
    "Skogmo",
    "Ottersøy",
    "Skytterhusfjellet",
    "Tomb",
    "Nodeland-Brennåsen",
    "Rafsbotn",
    "Innbygda",
    "Kjørnes",
    "Larsnes",
    "Sørkjosen",
    "Rensvik",
    "Sula",
    "Nakkerud",
    "Tysvær",
    "Tønsberg",
    "Rakkestad",
    "Hemnesberget",
    "Borkenes",
    "Innvik",
    "Skei",
    "Muruvik",
    "Stamsund",
    "Nordstrand",
    "Skoppum",
    "Vikebukt",
    "Åsen",
    "Langset",
    "Sjøholt",
    "Bleik",
    "Moss",
    "Stange",
    "Drammen",
    "Norheimsund",
    "Moelv",
    "Ålesund",
    "Eggkleiva",
    "Seimsfoss",
    "Øksfjord",
    "Sessvollmoen",
    "Fossnes",
    "Vangsåsen",
    "Lismarka",
    "Alsvåg",
    "Holmestrand",
    "Kvaløysletta",
    "Tveteneåsen",
    "Skotselv",
    "Hovdenakken",
    "Fjellstrand",
    "Hølen",
    "Breivikbotn",
    "Løstad",
    "Fauske",
    "Lena",
    "Øvre Årdal",
    "Finnestad",
    "Bøverbru",
    "Utgård",
    "Folldal",
    "Hoffland",
    "Vestre Toten",
    "Kongsberg",
    "Stryn",
    "Gjerstad",
    "Time",
    "Fosnavåg",
    "Løken",
    "Horten",
    "Bremsnes",
    "Fiskåbygd",
    "Blakstad",
    "Verdalsøra",
    "Kvål",
    "Melbu",
    "Harstad",
    "Missingmyr",
    "Ringerike",
    "Kodal",
    "Mosjøen",
    "Hjukse",
    "Holm",
    "Herøy",
    "Trøa",
    "Seljord",
    "Batnfjordsøra",
    "Støren",
    "Opakermoen",
    "Skeie",
    "Midtbygda",
    "Gol",
    "Geilolie",
    "Krøderen",
    "Liland",
    "Gladstad",
    "Årøysund",
    "Lakselv",
    "Velde",
    "Arna",
    "Røyken",
    "Kløfta",
    "Vikvågen",
    "Nesgrenda",
    "Sletta",
    "Elverum",
    "Os",
    "Svortland",
    "Skånevik",
    "Darbu",
    "Straume",
    "Vadfoss/Helle",
    "Nordal",
    "Møvik",
    "Røros",
    "Åsgårdstrand",
    "Eina",
    "Bybrua",
    "Stranda",
    "Fillan",
    "Gretteåsen",
    "Fjell",
    "Ballstad",
    "Færder",
    "Gullhaug",
    "Sykkylven",
    "Rørvik",
    "Bjerka",
    "Trolla",
    "Segalstad Bru",
    "Skoge/Møvik",
    "Glomstein",
    "Herre",
    "Søvik",
    "Hjelset",
    "Borgen",
    "Innfjorden",
    "Solsletta",
    "Selvik",
    "Nedstrand",
    "Vatne",
    "Andselv",
    "Tomter",
    "Kroksund",
    "Ullensaker",
    "Romedal Sentrum",
    "Lindås",
    "Gangstadhaugen",
    "Ski",
    "Øyslebø",
    "Sandane",
    "Storsand",
    "Nittedal",
    "Rosendal",
    "Hamre",
    "Svelgen",
    "Sørreisa",
    "Nordre Follo",
    "Røn",
    "Ferkingstad",
    "Kirkevoll/Brekkeåsen",
    "Rostadneset",
    "Mysen",
    "Hvittingfoss",
    "Sandefjord",
    "Gvarv",
    "Vestbygda",
    "Aukra",
    "Lørenskog",
    "Kaupanger",
    "Askøy",
    "Raufoss",
    "Sandnessjøen",
    "Folkestad",
    "Amtmannsnes",
    "Sokna",
    "Fjellfoten",
    "Rophus",
    "Gamvik",
    "Korsvik",
    "Lensbygda",
    "Brandal",
    "Skogsvågen",
    "Fedje",
    "Vingrom",
    "Sandnes",
    "Ringvål",
    "Løkenfeltet",
    "Mære",
    "Nordagutu",
    "Ingeberg",
    "Tonstad",
    "Rustad",
    "Rødberg",
    "Ikornnes",
    "Eikelandsosen",
    "Båtsfjord",
    "Skjønhaug",
    "Storås",
    "Svelvik",
    "Selbekken",
    "Frekhaug",
    "Sånum",
    "Vikevåg",
    "Geilo",
    "Nesoddtangen",
    "Solfjellsjyen",
    "Volda",
    "Lillestrøm",
    "Kopervik",
    "Sømna",
    "Mesnali",
    "Vestby",
    "Løiten Brænderi",
    "Høle",
    "Holme",
    "Re",
    "Renbygda",
    "Manger",
    "Svenevik",
    "Silsand",
    "Soknedal",
    "Braskereidfoss",
    "Åneby",
    "Råholt",
    "Helgeroa/Nevlunghamn",
    "Minnesund",
    "Forsand",
    "Sørland",
    "Heggelia",
    "Teigebyen",
    "Høysand",
    "Førdesfjorden",
    "Rypefjord",
    "Søre Øyane",
    "Oppeid",
    "Jessheim",
    "Hattfjelldal",
    "Haganes",
    "Karlestrand",
    "Haga",
    "Sira",
    "Larkollen",
    "Rausand",
    "Klæbu",
    "Terråk",
    "Hamnås",
    "Byrknes",
    "Bud",
    "Fuglevik",
    "Miland",
    "Nordheim-Kyte",
    "Røra Stasjon",
    "Høie",
    "Brekstad",
    "Brumunddal",
    "Mogrenda",
    "Mosvik",
    "Dokka",
    "Birkeland",
    "Sunndalsøra",
    "Bogen",
    "Våk",
    "Strømsnes",
    "Brandsøy",
    "Burfjord",
    "Karlshus",
    "Oltedal",
    "Gilja",
    "Hestvika",
    "Nesna",
    "Stavanger",
    "Flekke",
    "Hesseng",
    "Kolnes",
    "Hamar",
    "Ramsøy",
    "Kabelvåg",
    "Hardbakke",
    "Reinsvoll",
    "Hestnes",
    "Straumgjerde",
    "Vear",
    "Karasjok",
    "Koppang",
    "Flesland",
    "Leirvik",
    "Otta",
    "Sveio",
    "Fiksdal",
    "Ottersbo",
    "Revetal/Bergsåsen",
    "Ørsta",
    "Ådalsbruk",
    "Monssveen",
    "Sundgot",
    "Hylla",
    "Sortland",
    "Lalm",
    "Bismo",
    "Eikefjord",
    "Hovin",
    "Sommarøy",
    "Våler",
    "Lillesand",
    "Holmsbu",
    "Roa",
    "Andebu",
    "Bagn",
    "Fagerliåsen/Poverudbyen",
    "Hommersåk",
    "Lunde",
    "Tveit",
    "Sviland",
    "Rubbestadneset",
    "Åmot",
    "Eikeland",
    "Sandve",
    "Uskedal",
    "Aurdal",
    "Skarpengland",
    "Brønnøysund",
    "Hasvik",
    "Moen",
    "Bærum",
    "Lier",
    "Levanger",
    "Spydeberg",
    "Sparbu",
    "Haugo",
    "Raudeberg",
    "Skarde",
    "Åkrehamn",
    "Farestad",
    "Hesthagen",
    "Botten",
    "Rælingen",
    "Dimmelsvik",
    "Verningen",
    "Årset",
    "Sylling",
    "Bjørkelangen",
    "Kårvåg",
    "Fåberg",
    "Tyristrand",
    "Malmheim",
    "Skien",
    "Sulitjelma",
    "Hemnes",
    "Oppdal",
    "Børsa",
    "Ytre Enebakk",
    "Etne",
    "Hauknes",
    "Sæbøvik",
    "Kirkenes",
    "Siggerud",
    "Farstad",
    "Myra",
    "Sørbygdafeltet",
    "Tromsdalen",
    "Harpefoss",
    "Fardalen",
    "Bø",
    "Fitjar",
    "Søfteland",
    "Hammarsland",
    "Torhaug",
    "Henningsvær",
    "Hanøy",
    "Ringebu",
    "Skomrak",
    "Hafslo",
    "Hjørungavåg",
    "Jansberg",
    "Lofthus",
    "Roverud",
    "Vikøy",
    "Lundamo",
    "Tromsø",
    "Avaldsnes",
    "Ler",
    "Sandbumoen",
    "Vanvikan",
    "Trones",
    "Liabø",
    "Fossbergom",
    "Dalen",
    "Tælavåg",
    "Vågåmo",
    "Geitnes",
    "Skotbu",
    "Follafoss",
    "Hof",
    "Skibotn",
    "Austmarka (Kongsvinger)",
    "Brønnøy",
    "Bratsberg",
    "Selje",
    "Gata",
    "Askim",
    "Dombås",
    "Åmot/Geithus",
    "Kinsarvik",
    "Vigrestad",
    "Leitebakk",
    "Hønefoss",
    "Oslo",
    "Høllen",
    "Steinshamn",
    "Bangsund",
    "Vik",
    "Valestrandfossen",
    "Sigerfjord",
    "Hell",
    "Ekne",
    "Grimstad",
    "Isfjorden",
    "Havøysund",
    "Slidre",
    "Evenskjær",
    "Harestua",
    "Fåvang",
    "Skulestadmoen",
    "Dragsund",
    "Vassøy",
    "Jondal",
    "Sundbyfoss",
    "Lyefjell",
    "Småland",
    "Høylandet",
    "Padlene",
    "Melhus",
    "Grong",
    "Skatval",
    "Leland",
    "Osøyro",
    "Askje",
    "Høyanger",
    "Hoelsand",
    "Toft",
    "Fagernes",
    "Hellerud",
    "Volleberg",
    "Andenes",
    "Hansnes",
    "Kjøllefjord",
    "Våge",
    "Sand",
    "Vadsø",
    "Kjøpmannsskjær",
    "Bjervamoen",
    "Evje",
    "Hausvik",
    "Byglandsfjord",
    "Forset",
    "Søgne",
    "Voll",
    "Helgelandsmoen",
    "Slettebrotane",
    "Setskog",
    "Eidfjord",
    "Kragerø",
    "Pollestad",
    "Rabben-Veivågen",
    "Skjold",
    "Torsteinsvik",
    "Svorkmo",
    "Skjærhalden",
    "Biri",
    "Torget",
    "Arendal",
    "Kirkenær",
    "Vigeland",
    "Tjøme",
    "Hov",
    "Østhusvik",
    "Østenstad",
    "Botngård",
    "Fjellsrud",
    "Svarstad",
    "Hammarvika",
    "Vestnes",
    "Halden",
    "Steinvåg",
    "Hareid",
    "Rena",
    "Inndyr",
    "Jelsnes",
    "Valle",
    "Årås",
    "Høvåg",
    "Ranemsletta",
    "Trondheim",
    "Ørje",
    "Ås",
    "Moldekleiv",
    "Andslimoen",
    "Olderdalen",
    "Svøo",
    "Kvelde",
    "Vuku",
    "Lysøysund",
    "Dale",
    "Myklebost",
    "Trofors",
    "Storsteinnes",
    "Sandvika",
    "Nykirke",
    "Blomvåg",
    "Snåsa",
    "Kil",
    "Disenå",
    "Sneltvedt",
    "Otnes",
    "Naustdal",
    "Forbregd/Lein",
    "Åmli",
    "Gibostad",
    "Kilsund",
    "Spetalen",
    "Judaberg",
    "Spongdal",
    "Håkvik",
    "Slevik",
    "Nybergsund",
    "Hol",
    "Jortveit",
    "Mosterhamn",
    "Måløy",
    "Silvalen",
    "Øvre Eiker",
    "Vaksdal",
    "Misje",
    "Senjehopen",
    "Hallingby",
    "Lampeland",
    "Sunde/Valen",
    "Røldal",
    "Kristiansund",
    "Giske",
    "Grov",
    "Kalvåg",
    "Kampå",
    "Tretten",
    "Hovden",
    "Randaberg",
    "Grinde",
    "Berkåk",
    "Nordvågen",
    "Olden",
    "Smestad",
    "Eivindvik",
    "Torvikbukt",
    "Lundermoen",
    "Stokmarknes",
    "Follebu",
    "Prestfoss",
    "Nesbyen",
    "Asker",
    "Vinstra",
    "Rød",
    "Kirkebygda",
    "Øverbø",
    "Tanem",
    "Stordal",
    "Tau",
    "Leira",
    "Kolvereid",
    "Gravdal",
    "Skålevik",
    "Kleive",
    "Ørnes",
    "Kirkegrenda",
    "Magnor",
    "Bjørnafjorden",
    "Haugland",
    "Varhaug",
    "Beisfjord",
    "Vingnes",
    "Fotlandsvåg",
    "Storebø",
    "Ringsaker",
    "Åkrene",
    "Byflaten",
    "Gran/Ringstad",
    "Krossberg",
    "Svalia",
    "Torvika",
    "Sandvoll",
    "Undheim",
    "Namsos",
    "Løpsmarka",
    "Ringvoll",
    "Nordbøåsane",
    "Lødingen",
    "Steinkjer",
    "Langangen",
    "Solbakken-Sofienberg",
    "Lørenfallet",
    "Momoen",
    "Brønnsletten",
    "Florø",
    "Skiptvet",
    "Brattvåg",
    "Hommelvik",
    "Kåfjordbotn",
    "Neskollen",
    "Uggdalseidet",
    "Lindeberg",
    "Modum",
    "Tangen",
    "Langevåg",
    "Kråkstad",
    "Ulefoss",
    "Myre",
    "Sagvåg",
    "Alvdal",
    "Korgen",
    "Jevnaker",
    "Brårud",
    "Austmarka",
    "Klokkarstua",
    "Nærsnes",
    "Førde",
    "Slemsrud",
    "Løten",
    "Viksøyri",
    "Lyngdal",
    "Herøysund",
    "Seimsdalen",
    "Kongsvinger",
    "Hæen",
    "Glosli",
    "Mehamn",
    "Honningsvåg",
    "Movik",
    "Skogrand",
    "Yli",
    "Tynset",
    "Leinesfjord",
    "Torvvik",
    "Larvik",
    "Rindal",
    "Ramsund",
    "Skreia",
    "Krokeidet",
    "Tingvollvågen",
    "Malvik",
    "Stikkaåsen",
    "Gjøvik",
    "Berger",
    "Skodje",
    "Lovund",
    "Flateby",
    "Hordnes",
    "Ballangen",
    "Brunstad",
    "Askvoll",
    "Kvernaland",
    "Sundvollen",
    "Kleppe/Verdalen",
    "Buvika/Ilhaugen",
    "Nordhus",
    "Hakadal",
    "Granrudmoen",
    "Knarrevik/Straume",
    "Gaupne",
    "Ogna",
    "Sarpsborg",
    "Ise",
    "Aksdal",
    "Ibestad",
    "Skotterud",
    "Krossen",
    "Narvik",
    "Kyrksæterøra",
    "Løding",
    "Langhaugane",
    "Risør",
    "Brandbu/Jaren",
    "Glomfjord",
    "Hole",
    "Gomnes",
    "Knappskog",
    "Tomteråsen",
    "Korsvegen",
    "Tvedestrand",
    "Sola",
    "Lyngseidet",
    "Storbakken",
    "Slåttevik",
    "Nærland",
    "Skudeneshavn",
    "Sætre",
    "Viggja",
    "Tveitsund",
    "Bjertnestunet",
    "Ulvik",
    "Mo I Rana",
    "Ersfjordbotn",
    "Isebakke",
    "Stjørdalshalsen",
    "Hauge",
    "Stavern",
    "Moi",
    "Heggenes",
    "Øye",
    "Sande",
    "Råde",
    "Sørumsand",
    "Rjukan",
    "Krossneset",
    "Frogner",
    "Molde",
    "Vangsvik",
    "Vestre Jakobselv",
    "Fyresdal",
    "Bryggja",
    "Opphaug",
    "Rissa",
    "Oma",
    "Odda",
    "Skogn",
    "Svinndal",
    "Linnestad",
    "Tornes",
    "Eide",
    "Frommereid",
    "Setermoen",
    "Vossevangen",
    "Norderhaug",
    "Eidsvoll",
    "Hålandsmarka",
    "Langørjan",
    "Bamble",
    "Kolbu",
    "Nærbø",
    "Eidsvåg",
    "Holevik",
    "Remøy",
    "Hegra",
    "Grua",
    "Bokn",
    "Tana Bru",
    "Trømborg",
    "Forland",
    "Jørstadmoen",
    "Aure",
    "Byrkjelo",
    "Såstadbråten",
    "Stenebyen",
    "Mebonden",
    "Beitostølen",
    "Skjeggestadåsen",
    "Sogndalsfjøra",
    "Nypan",
    "Furuflaten",
    "Ølensvåg",
    "Hundorp",
    "Tomra",
    "Løkken",
    "Torpo",
    "Bekkjarvik",
    "Vang",
    "Nordlia",
    "Knarvik",
    "Brusand",
    "Hvasser",
    "Flekkefjord",
    "Haugesund",
    "Tinn Austbygd",
    "Ulsteinsvik",
    "Bjerkvik",
    "Hammerfest",
    "Rossnes",
    "Blæstadgrenda",
    "Kyrkjebø",
    "Vestre Åmøy",
    "Sætrebakkane",
    "Granvin",
    "Klepp",
    "Uthaug",
    "Porgrunn",
    "Skivika",
    "Tverrelvdalen",
    "Gryllefjord",
    "Vikebø",
    "Sistranda",
    "Ulstein",
    "Vollen",
    "Leikong",
    "Lervik",
    "Klokkarvik",
    "Vassenden",
    "Tennevoll",
    "Anglavika",
    "Kvam",
    "Alta",
    "Vennesla",
    "Nordfjordeid",
    "Maura",
    "Slitu",
    "Ilseng",
    "Snurråsen",
    "Flisa",
    "Årnes",
    "Svene",
    "Fjellstad",
    "Eltonåsen",
    "Tyssedal",
    "Kautokeino",
    "Sponvika",
    "Bomansvik"
)

fun getAllCities(cities: List<String>): MutableList<CityName>{
    val listOfCities = emptyList<CityName>().toMutableList()
    for(city in cities){
        listOfCities.add(CityName(city, "Norway"))
    }
    return listOfCities
}