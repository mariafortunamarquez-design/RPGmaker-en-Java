# RPG Maker - Edición Java

Herramienta de escritorio para diseñar mapas de rol, colocar entidades, crear
eventos con nodos, probar una partida y exportar un juego Java sencillo.

**Autora:** María Fortuna  
**Tarjeta de presentación:** <https://mariafortunamarquez-design.github.io/MariaFortuna.github.io/>

## Contenido

- [Descarga e instalación](#descarga-e-instalación)
- [Primeros pasos](#primeros-pasos)
- [Interfaz](#interfaz)
- [Crear y editar un proyecto](#crear-y-editar-un-proyecto)
- [Editor de mapas](#editor-de-mapas)
- [Entidades, objetos y jugador](#entidades-objetos-y-jugador)
- [Eventos visuales](#eventos-visuales)
- [Probar el juego](#probar-el-juego)
- [Exportar un juego](#exportar-un-juego)
- [Guardar datos y recursos](#guardar-datos-y-recursos)
- [Arquitectura del código](#arquitectura-del-código)
- [Compilar desde el código fuente](#compilar-desde-el-código-fuente)
- [Alcance y limitaciones conocidas](#alcance-y-limitaciones-conocidas)
- [Solución de problemas](#solución-de-problemas)

## Descarga e instalación

La aplicación se distribuye como un instalador de Windows:

1. Ejecuta `dist/RPGMaker-1.0.0.exe`.
2. Sigue los pasos del instalador y abre **RPG Maker** desde el menú Inicio o el
   acceso directo.
3. No hace falta instalar Java para ejecutar el instalador generado: incluye un
   entorno de ejecución privado y se instala para el usuario actual.

El instalador está destinado a Windows. La compilación del instalador desde el
código fuente requiere Windows, un JDK 25 y WiX Toolset 3.14.1. Puedes añadir
la carpeta de herramientas de WiX al `PATH` o definir `WIX` con la ruta a esa
carpeta. El aviso de Windows SmartScreen puede aparecer porque el ejecutable no
está firmado con un certificado de publicación.

El código fuente se entrega en el proyecto y, al ejecutar `build.bat`, también
se prepara `dist/RPGMaker-source.zip`.

## Primeros pasos

Al iniciar se crea un proyecto nuevo en memoria con un mapa inicial llamado
**Aldea Inicial**. Para conservar el trabajo:

1. Usa **Archivo > Guardar Como...** y elige dónde crear el archivo `.rpg`.
2. Crea o renombra los mapas desde la lista de la izquierda.
3. Selecciona tipos de casilla y dibuja el mapa en el lienzo central.
4. Añade entidades, objetos y eventos desde las pestañas del panel derecho.
5. Usa **Vista Previa** para probar el mapa y **Archivo > Exportar como Proyecto
   Java...** para generar el código de un juego.
6. Guarda de nuevo con **Ctrl+S** después de los cambios.

El programa no avisa automáticamente de cambios sin guardar. Al crear otro
proyecto o cerrar la aplicación, los cambios pendientes pueden perderse.

## Interfaz

- **Barra superior:** crear, abrir y guardar proyectos; seleccionar
  herramientas; acercar o alejar el mapa; abrir la vista previa y exportar.
- **Lista Mapas (izquierda):** seleccionar, añadir, eliminar y renombrar mapas.
- **Lienzo (centro):** pintar casillas, colocar o mover entidades y editar
  casillas mediante el menú contextual.
- **Panel derecho:** pestañas **Casillas**, **Propiedades**, **Entidades**,
  **Objetos**, **Eventos** y **Jugador**.
- **Barra inferior:** mensajes de estado, selección y coordenadas.
- **Menú Ayuda:** contiene el tutorial y **Acerca de**. La tarjeta de
  presentación de la autora abre su enlace en el navegador predeterminado.

## Crear y editar un proyecto

### Proyectos

- **Archivo > Nuevo Proyecto:** crea un proyecto con valores iniciales y un
  mapa. Solicita confirmación antes de descartar el proyecto actual.
- **Archivo > Abrir Proyecto...:** abre un archivo `.rpg`.
- **Archivo > Guardar Proyecto** o **Ctrl+S:** guarda en la ruta conocida; si
  aún no existe, abre el diálogo **Guardar Como...**.
- **Archivo > Guardar Como...:** guarda el proyecto en una ruta elegida y añade
  la extensión `.rpg` si no se ha escrito.
- **Editar > Configuración del Proyecto...:** edita el nombre, autor,
  descripción y versión que se guardan con el proyecto y se muestran en el
  menú de inventario de la vista previa.

### Mapas

La lista de la izquierda muestra el nombre y el tamaño de cada mapa. Al añadir
un mapa se eligen nombre, ID, ancho y alto. Los tamaños iniciales disponibles
son de 5 a 200 casillas por eje. **Mapa > Redimensionar Mapa Actual...** permite
cambiar sus dimensiones conservando las casillas que siguen dentro del nuevo
tamaño; las casillas nuevas se inicializan como hierba.

La pestaña **Propiedades** muestra el nombre, el tamaño y la tasa de encuentros.
También presenta selectores de conexiones norte, sur, este y oeste. En la
versión actual esos selectores no guardan conexiones funcionales, por lo que no
se deben usar como una transición jugable entre mapas.

### Herramientas del mapa

Selecciona una herramienta en la barra superior o en **Herramientas**:

| Herramienta | Uso |
| --- | --- |
| Pintar | Dibuja el tipo de casilla seleccionado en la paleta. |
| Borrar | Sustituye casillas por hierba y quita su imagen personalizada. |
| Rellenar | Rellena las casillas contiguas del mismo tipo. |
| Entidad | Coloca una entidad en la casilla seleccionada. |
| Seleccionar | Selecciona y permite arrastrar entidades existentes. |

Atajos del lienzo:

- `1` a `5`: Pintar, Borrar, Rellenar, Entidad y Seleccionar,
  respectivamente.
- `G`: muestra u oculta la cuadrícula.
- `+` o `=` y `-`: acerca o aleja el lienzo.
- `Ctrl` + rueda del ratón: cambia el zoom.
- Clic derecho sobre una casilla: alterna su transitabilidad, asigna un ID de
  evento, asigna o quita una imagen y coloca o elimina una entidad.

La paleta ofrece hierba, agua, muro, tierra, piedra, arena, bosque, puerta,
puente, lava, hielo y suelo de cueva. El indicador de color representa la
transitabilidad inicial del tipo. Clic en una muestra para seleccionarla.

**Importar...** permite seleccionar una textura PNG o GIF. Mientras esa textura
esté seleccionada se aplica a las casillas pintadas. **Quitar** desactiva la
textura de la paleta; no elimina imágenes que ya se hayan asignado a otras
casillas mediante el menú contextual.

## Entidades, objetos y jugador

### Entidades

El modo **Entidad** permite crear entidades NPC, enemigo, cofre, cartel, objeto o punto de
guardado. El diálogo inicial pide nombre e ID único. La pestaña **Entidades**
permite editar el tipo, posición, estadísticas, recompensas, diálogo, evento
asociado e imagen. Selecciona la entidad y usa **Aplicar Cambios** cuando esté
disponible. También puedes arrastrarla por el mapa con la herramienta
**Seleccionar**.

Las estadísticas de combate incluyen HP, ataque, defensa, velocidad y
recompensas de experiencia y oro. Para que un evento se active al interactuar
con una entidad, el ID escrito en su campo de evento debe coincidir exactamente
con el ID de un evento del proyecto.

### Objetos

La pestaña **Objetos** permite añadir, eliminar y editar objetos. Sus campos
incluyen nombre, tipo, descripción, bonificaciones de ataque, defensa, HP y
velocidad, curación, precios de compra y venta, y si se puede apilar.

La pestaña de equipamiento de la vista previa es informativa: muestra el primer
objeto encontrado de cada tipo. No ofrece acciones para equipar o desequipar.

### Jugador

En **Jugador** se configuran el nombre, HP máximo, ataque, defensa, velocidad,
oro y nivel inicial, además del mapa y las coordenadas de inicio. Pulsa
**Aplicar Cambios** para transferir los valores al modelo. Comprueba que las
coordenadas iniciales estén dentro del mapa.

## Eventos visuales

La pestaña **Eventos** contiene una lista de eventos, campos para nombre, ID y
disparador, un editor de nodos y un inspector de propiedades.

1. Añade un evento y asígnale un ID estable.
2. Selecciona el disparador: interacción, pisar casilla, entrar en mapa o
   ejecución automática.
3. Haz clic derecho en el lienzo del grafo para añadir nodos de diálogo,
   acción o condición.
4. Arrastra los nodos para ordenarlos. Selecciona uno para editarlo en el
   inspector.
5. Arrastra desde el puerto de salida de un nodo hasta otro nodo para crear una
   conexión. Las condiciones tienen una salida verdadera y otra falsa.
6. Conecta el nodo **Start** al primer paso del flujo. Supr/Delete elimina el
   nodo seleccionado, excepto **Start**.

Las condiciones disponibles son **FLAG** e **ITEM**. Entre las acciones que
pueden configurarse aparecen diálogos, objetos, teletransporte, batallas,
banderas, cambios de HP/oro/experiencia, sonidos, tiendas y fin de juego.
El comportamiento real depende del motor que las ejecute; no todas las
acciones seleccionables están implementadas en la vista previa o en el juego
Java exportado. Consulta [Alcance y limitaciones conocidas](#alcance-y-limitaciones-conocidas).

## Probar el juego

Pulsa **Vista Previa** para jugar el mapa seleccionado. La vista previa trabaja
con una copia del mapa, por lo que los cambios hechos durante la partida no
modifican el mapa de edición.

- `W`, `A`, `S`, `D` o las flechas: moverse.
- `E`: interactuar con una entidad o una casilla próxima.
- `I` o `Tab`: abrir o cerrar el inventario.
- `Ctrl` + rueda del ratón: cambiar el tamaño de las casillas.
- `Esc`: cerrar la vista previa.

Las casillas no transitables bloquean el movimiento. Entrar en una casilla con
un ID de evento puede ejecutar un evento de tipo **Al Pisar**. La interacción
busca entidades y casillas vecinas; los NPC y carteles muestran su diálogo,
los cofres dan su recompensa de oro, los puntos de guardado restauran el HP y
los objetos se retiran del mapa. Los enemigos inician un combate por turnos con
opciones de atacar, defender o huir.

Si el mapa tiene una ruta WAV configurada en los datos, la vista previa intenta
reproducirla y el menú permite ajustar el volumen. La opción actual de importar
música guarda una ruta de archivo y no copia el audio al proyecto.

## Exportar un juego

Usa **Archivo > Exportar como Proyecto Java...** y elige una carpeta de salida.
El exportador crea `Main.java`, `GameData.java`, `GameEngine.java`,
`compilar.bat` y `ejecutar.bat`.

En Windows, con un JDK instalado:

1. Abre la carpeta exportada.
2. Ejecuta `compilar.bat`; los `.class` se escriben en la subcarpeta `classes`.
3. Ejecuta `ejecutar.bat`.

La exportación es un punto de partida de código Java sencillo, no un paquete
equivalente al editor ni una copia completa de su simulación. Lee las
limitaciones indicadas a continuación antes de depender de eventos, imágenes o
funciones avanzadas en el juego exportado.

## Guardar datos y recursos

Los proyectos `.rpg` se escriben como propiedades Java. El guardado conserva
los metadatos básicos del proyecto y del jugador, casillas y dimensiones de
los mapas, estadísticas y diálogo de las entidades, objetos y grafos de
eventos con sus parámetros.

Los recursos seleccionados mediante diálogos se guardan como rutas, no como
archivos incrustados. Mantén las imágenes y los WAV en una ubicación estable y
haz copias de esos archivos junto al proyecto si lo trasladas a otro equipo.
Algunas rutas y propiedades del modelo no están incluidas en la serialización
actual; comprueba la lista de [limitaciones conocidas](#alcance-y-limitaciones-conocidas).

## Arquitectura del código

El proyecto sigue una separación MVC parcial:

- `model/`: proyecto, mapas, casillas, entidades, jugador, objetos, eventos y
  nodos del grafo. `GameProject` es el agregado raíz y `GameMap` administra la
  cuadrícula y las entidades.
- `controller/MainController.java`: estado del proyecto y operaciones para
  crear, abrir, guardar, exportar y cambiar ciertos datos.
- `editor/`: vistas Swing. `MainFrame` compone la ventana y coordina menús,
  formularios y paneles; los paneles de mapa, propiedades, entidades, objetos,
  eventos y jugador son vistas especializadas.
- `editor/node/`: dibujo y edición visual de grafos de eventos.
- `io/ProjectSerializer.java`: serialización manual del archivo `.rpg`.
- `preview/`: simulación de movimiento, interacción, inventario y combate.
- `export/JavaCodeExporter.java`: generación de las fuentes del juego
  simplificado.
- `util/`: caché de imágenes y reproducción de audio.

La separación no es estricta: algunos paneles Swing modifican directamente el
modelo a través de `MainFrame`, y `MainFrame` conserva manejadores de acciones
y diálogos. `MainController` centraliza operaciones de proyecto, pero no todas
las interacciones de la interfaz pasan por él.

## Compilar desde el código fuente

Requisitos para crear el instalador de Windows:

- Windows.
- JDK 25 (incluyendo `javac`, `jar` y `jpackage`).
- WiX Toolset 3.14.1 disponible para `jpackage` en `PATH` o mediante la
  variable `WIX`.

Configura `JAVA_HOME` para que apunte al JDK 25 y ejecuta desde la carpeta del
proyecto. Si WiX no está en `PATH`, define `WIX` como la carpeta que contiene
`candle.exe` y `light.exe` (por ejemplo, `set "WIX=C:\tools\wix314"`):

```bat
build.bat
```

El proceso compila las fuentes, crea la salida `.class` dentro de las carpetas
de paquetes, prepara el JAR de entrada de `jpackage` y produce:

- `dist/RPGMaker-1.0.0.exe`: instalador de Windows con runtime Java.
- `dist/RPGMaker-source.zip`: código fuente y documentación sin archivos
  `.class`.

Los `.class` son artefactos de compilación; no son necesarios para conservar ni
compartir el código fuente.

## Alcance y limitaciones conocidas

Estas limitaciones se deducen de la implementación actual; no implican que los
datos visibles en la interfaz se guarden o ejecuten automáticamente:

1. **Guardado parcial de propiedades del modelo.** `ProjectSerializer` no
   escribe las conexiones entre mapas, la música de fondo, los ID de evento ni
   las imágenes de casillas y entidades, los objetos de caída, el inventario
   inicial, ni las banderas/variables globales. El tipo de casilla y el bloqueo
   manual de una casilla sí se serializan. Los selectores de conexión de mapa
   tampoco actualizan el modelo actualmente.
2. **Recursos externos.** Las rutas de textura, imagen y música dependen de
   archivos que deben seguir disponibles en el mismo camino. El guardado no
   crea una carpeta de recursos ni los copia.
3. **Eventos en la vista previa.** Se ejecutan los disparadores de interacción
   y pisar; los disparadores de entrar en mapa y ejecución automática no se
   invocan desde el flujo actual. El motor de vista previa reconoce banderas,
   objetos, teletransporte, oro, curación/daño e inicio de combate. Otras
   acciones disponibles en el selector, como dar experiencia, reproducir
   sonido, tienda y fin de juego, no tienen una implementación equivalente en
   ese motor.
4. **Exportador simplificado.** `GameData.java` contiene los datos básicos de
   los mapas y estadísticas de entidades, pero `GameEngine.java` solo permite
   recorrer el primer mapa y no implementa los grafos de eventos, el inventario,
   combate, imágenes personalizadas o música. La vista previa del editor tiene
   más funciones que el juego exportado.
5. **Sin seguimiento de cambios.** No existe indicador de cambios pendientes
   ni guardado automático. Guarda manualmente antes de cerrar o cambiar de
   proyecto.

## Solución de problemas

- **El instalador no se crea:** comprueba `JAVA_HOME`, que JDK 25 sea el usado
  por `javac` y `jpackage`, y que WiX Toolset 3.14.1 esté instalado. En la
  consola, revisa el error que deja `build.bat`.
- **El instalador indica que Java no está instalado:** usa el instalador
  generado por este proyecto, que empaqueta su runtime, y no una salida
  `app-image` independiente.
- **No aparece una textura o una música:** verifica que el archivo original
  siga en la ruta almacenada y en un formato admitido (PNG/GIF o WAV).
- **El evento no se ejecuta:** confirma que el ID de la casilla o entidad
  coincide exactamente con el ID del evento y que el disparador corresponde a
  la acción de prueba.
- **No se puede guardar o abrir:** confirma que la carpeta permita lectura y
  escritura y conserva una copia del `.rpg` antes de editarlo manualmente.
- **El enlace de la autora no se abre:** selecciona el texto del enlace desde
  **Ayuda > Acerca de** y comprueba que Windows tenga navegador predeterminado
  y acceso a Internet.

---

Desarrollado por **María Fortuna** ·
<https://mariafortunamarquez-design.github.io/MariaFortuna.github.io/>
