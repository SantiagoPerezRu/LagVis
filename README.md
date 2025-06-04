# LagVis Documentación

## Diagrama de Clases

```mermaid
classDiagram
    %% Jerarquía de Actividades
    AppCompatActivity <|-- BaseActivity
    BaseActivity <|-- LoginActivity
    BaseActivity <|-- RegisterActivity
    BaseActivity <|-- AdvancedFormRegister
    BaseActivity <|-- Convenio
    AppCompatActivity <|-- MainActivity
    AppCompatActivity <|-- ActivityResultadoDespido
    AppCompatActivity <|-- ActivityDatosGeneralesDespido
    AppCompatActivity <|-- ActivityDatosGeneralesFiniquito

    %% Jerarquía de Fragmentos
    Fragment <|-- BaseFragment
    BaseFragment <|-- FirstFragment
    BaseFragment <|-- SecondFragment
    BaseFragment <|-- ThirdFragment
    BaseFragment <|-- NoticiasGuardadasFragment
    Fragment <|-- FourthFragment
    Fragment <|-- CalendarioLaboral
    Fragment <|-- PaginaVidaLaboralFragment
    Fragment <|-- DatosGeneralesDespidoFragment

    %% Interfaces y Adaptadores
    MainActivity ..|> NavigationView.OnNavigationItemSelectedListener
    SecondFragment ..|> NewsApiService.NoticiasCallback
    RecyclerView.Adapter <|-- HolidayAdapter

    %% APIs y Servicios
    class NagerDateApi {
        +getPublicHolidays(int year, String countryCode, String countyCode)
    }

    class NewsApiService {
        <<interface>>
        +onNoticiasObtenidas(List~NewsItem~)
        +onNoticiasError(String)
    }

    %% Clases Base
    class BaseActivity {
        #onCreate(Bundle)
        +showCustomToast(String, Drawable)
    }

    class BaseFragment {
        -Context context
        #onCreate(Bundle)
        #onCreateView(LayoutInflater, ViewGroup, Bundle)
    }

    %% Actividades Principales
    class MainActivity {
        -DrawerLayout drawerLayout
        -NavigationView navigationView
        -Toolbar toolbar
        -FrameLayout frameLayout
        +onCreate(Bundle)
    }

    class LoginActivity {
        -FirebaseAuth auth
        -EditText emailTextView
        -EditText passwordTextView
        -CheckBox checkboxRemember
        +onCreate(Bundle)
        -loginUserAccount()
    }

    class Convenio {
        -TextView tvTitulo
        -TextView tvResumenGeneral
        +onCreate(Bundle)
        -inicializarVistas()
        -cargarConvenioDesdeXML()
    }

    %% Modelos de Datos
    class NewsItem {
        +String title
        +String link
        +String pubDate
        +String creator
        +NewsItem(String, String, String, String)
        +toString() String
    }

    class PublicHoliday {
        -String date
        -String localName
        -String name
        -String countryCode
        +getDate() String
        +getLocalName() String
    }

    %% Adaptadores
    class HolidayAdapter {
        -List~PublicHoliday~ holidayList
        +setHolidayList(List~PublicHoliday~)
        +onCreateViewHolder(ViewGroup, int)
        +onBindViewHolder(HolidayViewHolder, int)
    }

    %% Relaciones
    HolidayAdapter --> PublicHoliday
    CalendarioLaboral --> HolidayAdapter
    SecondFragment --> NewsItem
    NoticiasGuardadasFragment --> NewsItem
