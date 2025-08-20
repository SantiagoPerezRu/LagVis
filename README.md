# LagVis - Tu Guía de Derechos Laborales en España

[![GitHub license](https://img.shields.io/badge/license-Proprietary-blue.svg)](LICENSE)
[![GitHub last commit](https://img.shields.io/github/last-commit/SantiagoPerezRu/LagVis)](https://github.com/SantiagoPerezRu/LagVis/commits/main)

## 💡 Sobre el Proyecto

**LagVis** es una aplicación móvil diseñada para simplificar el acceso a la información laboral en España. Nace de la necesidad de hacer más comprensibles los derechos y deberes tanto para trabajadores como para empleadores, evitando así conflictos y malentendidos causados por el desconocimiento de la normativa, especialmente de los complejos convenios colectivos.

La aplicación ofrece una interfaz intuitiva y fácil de usar, donde cualquier persona puede consultar de forma rápida y sencilla aspectos clave de la legislación laboral, como salarios, vacaciones, permisos y procedimientos en caso de despido. Mi objetivo es democratizar el acceso a esta información crucial, presentándola de forma clara, con resúmenes comprensibles y ejemplos prácticos.

Este proyecto ha sido desarrollado como mi Trabajo de Fin de Grado (TFG) para el Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM).

## ✨ Características Principales

* **Registro y Autenticación Segura:** Permite a los usuarios crear y gestionar sus cuentas de forma segura.
* **Buscador de Convenios Colectivos:** Localiza convenios específicos filtrando por sector laboral y comunidad autónoma.
* **Visualización Simplificada de Convenios:** Presenta los puntos clave de cada convenio (salarios mínimos, días libres, derechos básicos) de manera clara y resumida.
* **Noticias Laborales Actualizadas:** Acceso a una sección con las últimas novedades y cambios en la normativa laboral.
* **Calculadoras Integradas:** Herramientas para estimar indemnizaciones por despido (para trabajadores) y costes de despido (para empleadores).
* **Acceso Directo a la Vida Laboral:** Redirección a la web oficial de la Seguridad Social para consultar el informe de vida laboral.
* **Calendario Laboral por Comunidad Autónoma:** Una funcionalidad extra que muestra los festivos y días importantes específicos de cada región.

## 🛠️ Tecnologías Utilizadas

El desarrollo de LagVis se ha realizado utilizando un conjunto de tecnologías modernas y eficientes para garantizar la robustez y escalabilidad de la aplicación:

* **Frontend (Aplicación Android):**
    * **Lenguaje:** Java
    * **IDE:** Android Studio
    * **Diseño de UI:** XML
* **Backend (Servicio en la Nube):**
    * **Tecnología:** PHP
    * **Despliegue Serverless:** Google Cloud Run
    * **Contenerización:** Docker
* **Base de Datos:**
    * **Tipo:** MySQL
    * **Servicio en la Nube:** Google Cloud SQL
* **Autenticación y Datos Básicos:**
    * **Plataforma:** Firebase
* **Control de Versiones:**
    * **Plataforma:** Git & GitHub

## Instalación y Puesta en Marcha

Para que la aplicación "LagVis" funcione correctamente, es necesario seguir una serie de pasos que incluyen la preparación del entorno, la configuración de la base de datos y el despliegue del servicio backend. Esta guía detallada cubre los requisitos del sistema y las instrucciones para cada componente.

### 1. Requisitos del Sistema

* **Hardware de Desarrollo:**
    * PC o portátil con al menos 8 GB de RAM (recomendado 16 GB+), procesador Intel Core i5 / AMD Ryzen 5 o superior, y SSD.
    * Smartphone Android físico para pruebas (Android 8.0 Oreo o superior).
* **Software de Desarrollo:**
    * Sistema Operativo (Windows, macOS, Linux).
    * Android Studio (última versión estable).
    * Java Development Kit (JDK) compatible con Android Studio.
    * SDK de Android (instalado vía Android Studio).
    * Docker Desktop. (No ncesario pero recomendable.)
    * Google Cloud SDK (gcloud CLI). (No ncesario pero recomendable.)
    * Git. (No ncesario pero recomendable.)
    * Navegador Web (para GCP Console). (No ncesario pero recomendable.)

## 📄 Licencia

Este proyecto está protegido bajo la **LagVis License - Uso No Comercial** (2025) escrita por Santiago Pérez Díaz-Rubín.

El software está disponible únicamente para fines educativos, personales y académicos. Se permite su visualización, estudio, ejecución y modificación bajo estas condiciones:

- Está terminantemente prohibido cualquier uso comercial del software sin autorización expresa y por escrito del autor.
- No se permite la redistribución ni la venta, total o parcial, del software o de sus derivados, sin el consentimiento del autor.
- Se debe conservar el aviso de copyright en todas las copias o porciones sustanciales del software.

Para obtener permisos comerciales, distribución o licencias especiales, contacte al autor directamente.


## 📧 Contacto

Para cualquier consulta o colaboración, no dudes en contactarme: 

**Santiago Pérez Díaz-Rubín**
Email: `santiago.perez.rub@gmail.com`




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
