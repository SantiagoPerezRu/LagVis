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

    %% Jerarquía de Fragmentos
    Fragment <|-- BaseFragment
    BaseFragment <|-- FirstFragment
    BaseFragment <|-- SecondFragment
    BaseFragment <|-- ThirdFragment
    BaseFragment <|-- NoticiasGuardadasFragment
    Fragment <|-- FourthFragment

    %% Interfaces
    MainActivity ..|> NavigationView.OnNavigationItemSelectedListener
    SecondFragment ..|> NewsApiService.NoticiasCallback

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
        +onCreate(Bundle)
        -loginUserAccount()
    }

    %% Fragmentos
    class NoticiasGuardadasFragment {
        -List~NewsItem~ listaNoticiasGuardadas
        -FirebaseAuth auth
        +newInstance()
    }

    class SecondFragment {
        +newInstance(String, String)
        +onNoticiasObtenidas(List~NewsItem~)
        +onNoticiasError(String)
    }

    %% Clases de Modelo
    class NewsItem {
        +String title
        +String link
        +String pubDate
        +String creator
        +NewsItem(String, String, String, String)
        +toString() String
    }
