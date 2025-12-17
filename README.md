data Esta capa es responsable del acceso a los datos, ya sean remotos (API) o locales.

•📁 data/modelContiene las clases de datos (POKOs - Plain Old Kotlin Objects) que representan los objetos de negocio de la aplicación.

◦User.kt: Modelo para un usuario de la aplicación.

◦Post.kt: Modelo para una publicación del foro.◦Comentarios.kt: Modelo para un comentario en una publicación.

◦Article.kt, Source.kt: Modelos para las noticias obtenidas de la API GNews.

◦Game.kt, GameListResponse.kt: Modelos para los juegos obtenidos de la API RAWG.

◦ErrorResponse.kt: Modelo para interpretar los mensajes de error del servidor.

📁 data/remoteContiene todo lo relacionado con la comunicación de red.

◦ApiService.kt: Interfaz de Retrofit que define los endpoints de tu API propia (login, registro, posts).

◦ExternalApiService.kt: Interfaz de Retrofit para la API de RAWG (juegos).

◦GNewsApiService.kt: Interfaz de Retrofit para la API de GNews (noticias).

◦RetrofitInstance.kt, ExternalRetrofitInstance.kt y GNewsRetrofitInstance.kt: Objetos singleton que configuran y proveen una única instancia de Retrofit para cada API.

◦LoginRequest.kt y RegisterRequest.kt: Modelos de datos específicos para las peticiones de la API.

📁 repositoryEsta capa actúa como intermediaria entre la capa de datos y los ViewModels. Su función es ser la única fuente de verdad para los datos de la aplicación.

•AuthRepository.kt: Gestiona la lógica de autenticación (login, registro).

•PostRepository.kt: Gestiona todo lo relacionado con las publicaciones y comentarios.

•GameRepository.kt: Gestiona la obtención de datos de la API de juegos.

•NewsRepository.kt: Gestiona la obtención de datos de la API de noticias.

📁 viewModelLos ViewModels contienen la lógica de negocio y gestionan el estado de la interfaz de usuario. Se comunican con los repositorios para obtener datos y los exponen a la UI a través de StateFlow.

•AuthViewModel.kt: Gestiona el estado de autenticación (login, registro, usuario actual) y la validación de los campos.

•PostViewModel.kt: Gestiona el estado de las listas de posts, el post seleccionado, los comentarios y las acciones del usuario (votar, añadir a favoritos).

•GameViewModel.kt: Obtiene y expone la lista de juegos populares.•NewsViewModel.kt: Obtiene y expone la lista de noticias.

📁 ui

Esta capa contiene todos los componentes de la interfaz de usuario, escritos en Jetpack Compose.

•📁 ui/screensContiene las funciones Composable que representan cada pantalla completa de la aplicación. Estas pantallas son, en su mayoría, "tontas": observan el estado del ViewModel y reaccionan a él.

◦WelcomeScreen.kt: Pantalla de bienvenida con opciones para iniciar sesión o registrarse.

◦LoginScreen.kt y RegisterScreen.kt: Pantallas para la autenticación.

◦ProfileScreen.kt: Pantalla de perfil del usuario, con la opción de cambiar la foto.

◦PopularScreen.kt y CommunityScreen.kt: Muestran las listas de posts.

◦PostDetailScreen.kt: Muestra el detalle de una publicación y sus comentarios.

◦GamesScreen.kt y NewsScreen.kt: Muestran los datos de las APIs externas.

📁 ui/themeDefine el tema visual de la aplicación (colores, tipografía, formas) según los principios de Material Design 3.

◦Theme.kt: Aplica el tema a toda la aplicación.

◦Color.kt y Type.kt: Definen la paleta de colores y los estilos de tipografía.

📁 navigationContiene la lógica de navegación de la aplicación.

•AppNavigation.kt: El corazón de la navegación. Define el NavHost con todas las rutas posibles, gestiona la barra de navegación inferior y centraliza la creación y paso del AuthViewModel compartido.

📁 testContiene los tests unitarios del proyecto.

•AuthViewModelTest.kt, GameViewModelTest.kt, etc.: Archivos de test que verifican la lógica de los ViewModels de forma aislada, usando mocks para simular las dependencias de los repositorios.

•rules: Contiene reglas personalizadas de JUnit para facilitar el testing de componentes que usan corrutinas o LiveData.
