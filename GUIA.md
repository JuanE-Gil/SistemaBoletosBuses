# Guía del proyecto — SistemaBoletosBuses

> Guía de referencia del sistema: estructura, funciones, reglas de negocio y flujo de trabajo.
> Sirve como base para redactar/mejorar el informe **TALLER PROGRAMACION.md**.
> El diagrama UML del informe no se modifica desde esta guía.

---

## 1. Compilación y ejecución

### Requisitos
- **JDK 21** — es el SDK del proyecto (definido en `.idea/misc.xml`: `project-jdk-name="21"`). El bytecode esperado es la **versión 65**.

### Opción A — IntelliJ IDEA
1. **Build → Rebuild Project** (genera las clases en `out/production/SistemaBoletosBuses/`).
2. Ejecutar `app.Main`.

> **Si aparece `class file has wrong version 69.0, should be 65.0`:** significa que los `.class` de `out/` los generó un `javac` más nuevo (Java 25) que el SDK 21. Solución: borrar la carpeta `out/` y hacer **Rebuild Project**. IntelliJ no recompila solo porque los `.class` son más nuevos que los `.java`.

### Opción B — Terminal (PowerShell)
```powershell
# Compilar (siempre con --release 21 para no pisar la salida de IntelliJ)
javac --release 21 -encoding UTF-8 -d out\shell-check (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object FullName)

# Ejecutar
java -cp out\shell-check app.Main
```
- `--release 21` fuerza bytecode versión 65 aunque el `javac` del PATH sea más nuevo.
- `out/shell-check/` es una carpeta aparte: **no** se compila directamente en `out/production/` (esa es de IntelliJ).
- `out/` está en `.gitignore`: se puede borrar sin impacto en git.

### Persistencia
Los datos viven **solo en memoria**: al iniciar se cargan 2 viajes demo (`V001`, `V002`) y al salir no se guarda nada. Cada ejecución arranca de cero.

---

## 2. Estructura del código

```
SistemaBoletosBuses/
├── TALLER PROGRAMACION.md      # Informe del trabajo final (separado de esta guía)
├── GUIA.md                     # Este documento
├── .idea/                      # Configuración de IntelliJ (SDK 21)
├── out/                        # Artefactos de build (ignorado por git)
└── src/
    ├── app/
    │   ├── Main.java           # Navegación: menú principal, submenús y datos demo
    │   ├── Consultas.java       # Flujos del submenú Consultas (buscar, mapa)
    │   ├── Boletos.java         # Flujos del submenú Boletos (vender, anular, reprogramar)
    │   └── Registro.java        # Flujos del submenú Registro (bus y viaje)
    ├── model/
    │   ├── Ruta.java           # Origen, destino y tarifa
    │   ├── Bus.java            # Placa, mapa de asientos y cupo de equipaje
    │   ├── Asiento.java        # Butaca individual (número + estado)
    │   ├── Viaje.java          # Código, ruta, bus, fecha y hora
    │   ├── Cliente.java        # DNI, nombre, apellido, correo, teléfono
    │   ├── Maleta.java         # Peso y cálculo de sobrepeso
    │   ├── Pago.java           # Monto, medio de pago y fecha de venta
    │   └── Boleto.java         # Emisión: cliente, viaje, asiento, pago, maletas
    ├── service/
    │   ├── GestorVentas.java   # Núcleo: alta de viajes, venta, anulación, reprogramación
    │   └── Reportes.java       # Reportes estáticos: total, por ruta, por fecha
    ├── util/
    │   └── Validador.java      # Validaciones (DNI, correo, fecha, hora, peso) y lectura de entrada
    └── exceptions/
        └── AsientoOcupadoException.java  # Excepción al intentar ocupar un asiento tomado
```

### Paquetes
| Paquete | Responsabilidad |
|---|---|
| `model` | Datos y sus invariantes (qué es un viaje, un boleto, una maleta…) |
| `service` | Lógica de negocio: orquesta modelo + validaciones y lanza excepciones |
| `util` | Validación de entradas y lectura reintentable desde consola |
| `exceptions` | Excepción de dominio específica (`AsientoOcupadoException`) |
| `app` | Presentación: menú, prompts y captura de errores hacia el usuario |

---

## 3. Clases y funciones

### 3.1 `model.Ruta`
Recorrido con su tarifa. `toString` → `Lima - Arequipa (S/ 80.0)`.

**Atributos:** `origen` · `destino` (String) · `tarifa` (double)

| Método | Función |
|---|---|
| `Ruta(origen, destino, tarifa)` | Crea la ruta |
| `getOrigen() / getDestino() / getTarifa()` | Accesores |
| `toString()` | Formo para mostrar en listados y boletos |

### 3.2 `model.Bus`
Un bus por viaje: matriz de asientos + contador de maletas.

**Atributos:** `MAX_MALETAS` (`static final` = 20) · `placa` (String) · `asientos` (matriz `Asiento[][]`) · `maletasCargadas` (int, inicia 0)

| Método | Función |
|---|---|
| `Bus(placa, filas, columnas)` | Crea la matriz de asientos (`F1C1`, `F1C2`…). **Valida dimensiones**: filas 1–40, columnas 1–6 (si no, `IllegalArgumentException`) |
| `copia()` | Instancia **nueva** con la misma placa/dimensiones pero estado limpio: cada viaje tiene su propio mapa de asientos y su propio cupo de equipaje |
| `existe(fila, columna)` | ¿La coordenada está dentro del rango? (base 1) |
| `asientoLibre(fila, columna)` | Consulta el estado de la butaca |
| `ocuparAsiento / liberarAsiento(fila, columna)` | Cambia el estado de la butaca |
| `maletasDisponibles()` | `MAX_MALETAS − maletasCargadas` |
| `cargarMaleta() / descargarMaleta()` | Incrementa/decrementa el contador de equipaje |
| `contarAsientosLibres()` | Libres totales (se muestra en cada viaje) |
| `mostrarMapaAsientos()` | Plano `[ ]` libre / `[X]` ocupado |

### 3.3 `model.Asiento`
Butaca individual.

**Atributos:** `numeroAsiento` (String) · `estado` (boolean, `true` = ocupado)

| Método | Función |
|---|---|
| `Asiento(numeroAsiento)` | Crea libre (`estado = false`) |
| `ocupar() / liberar()` | Cambia el estado |
| `estaLibre()` | `!estado` |
| `getNumeroAsiento() / getEstado()` | Accesores |

### 3.4 `model.Viaje`
Salida programada. `toString` → `V001 | Lima - Arequipa (S/ 80.0) | 15/11/2026 20:00 | Libres: 19`.

**Atributos** (todos `final`, inmutables tras el alta): `codigo` · `ruta` · `bus` · `fecha` · `hora` (String)

| Método | Función |
|---|---|
| `Viaje(codigo, ruta, bus, fecha, hora)` | Crea el viaje (fecha `dd/MM/yyyy`, hora `HH:mm`) |
| `getCodigo() / getRuta() / getBus() / getFecha()` | Accesores (el código busca sin distinguir mayúsculas) |

### 3.5 `model.Cliente`
**Atributos:** `dni` · `nombre` · `apellido` · `correo` · `telefono` (String)

| Método | Función |
|---|---|
| `Cliente(dni, nombre, apellido, correo, telefono)` | Registra al pasajero |
| `getDni() / getNombre() / getApellido() / getCorreo() / getTelefono()` | Accesores |
| `toString()` | `Apellido, Nombre | DNI | correo | teléfono` |

### 3.6 `model.Maleta`
**Atributos:** `LIMITE_KG` (`static final` = 25) · `TARIFA_SOBREPESO` (`static final` = 5.0, S/ por kg excedido) · `peso` · `sobrepeso` (double)

| Método | Función |
|---|---|
| `Maleta(peso)` | Calcula el sobrepeso en el constructor: `peso > 25 ? (peso − 25) × 5 : 0` |
| `getPeso() / getSobrepeso()` | Accesores |
| `toString()` | `30.0 kg (sobrepeso: S/ 25.0)` |

### 3.7 `model.Pago`
**Atributos:** `monto` (double) · `medioPago` · `fecha` (String, fecha de venta `dd/MM/yyyy`)

| Método | Función |
|---|---|
| `Pago(monto, medioPago, fecha)` | Monto = tarifa + sobrepesos; `fecha` = fecha de venta (`dd/MM/yyyy`) |
| `getMonto() / getMedioPago() / getFecha()` | Accesores |
| `toString()` | `S/ 105.0 (EFECTIVO) 18/11/2026` |

### 3.8 `model.Boleto`
Emisión de un pasaje. Se muestra completo al vender/reprogramar; `[ANULADO]` al final si está anulado.

**Atributos:** `codigo` · `cliente` · `pago` · `maletas` (`final`) · `viaje` · `fila` · `columna` (los tres últimos mutables por reprogramación) · `activo` (boolean, `true` por defecto)

| Método | Función |
|---|---|
| `Boleto(codigo, cliente, viaje, fila, columna, pago, maletas)` | Crea el boleto (activo por defecto) |
| `getCodigo() / getCliente() / getViaje() / getFila() / getColumna() / getPago() / getMaletas()` | Accesores |
| `isActivo() / setActivo(boolean)` | Estado de anulación |
| `reprogramar(nuevoViaje, nuevaFila, nuevaColumna)` | Mueve el boleto a otro viaje/asiento (**el pago no se recalcula**) |
| `toString()` | Ficha completa: cliente, ruta, asiento, equipaje, pago |

### 3.9 `service.GestorVentas` — núcleo del sistema
Arreglos fijos con su contador de llenado.

**Atributos:** `buses[20]` · `viajes[20]` · `boletos[200]` (arreglos) · `cantidadBuses` · `cantidadViajes` · `cantidadBoletos` (int)

| Método | Función y validaciones (en orden) |
|---|---|
| `agregarBus(placa, filas, columnas)` | Alta en la flota. Rechaza **placa vacía**, **placa duplicada** (sin distinguir mayúsculas) y **capacidad llena**; las dimensiones las valida el constructor de `Bus` |
| `buscarBus(placa)` | Busca en la flota por placa (case-insensitive); `null` si no existe |
| `agregarViaje(código, ruta, placa, fecha, hora)` | Alta de viaje **referenciando un bus de la flota**: rechaza código duplicado, **placa no registrada** y capacidad llena. Crea el viaje con `plantilla.copia()` — cada viaje tiene su estado de asientos/equipaje propio — y devuelve el `Viaje` |
| `buscarViaje(código)` | **Sobrecarga 1:** exacto por código, sin distinguir mayúsculas; `null` si no existe |
| `buscarViaje(origen, destino)` | **Sobrecarga 2:** por ruta completa, sin fecha |
| `buscarViaje(origen, destino, fecha)` | **Sobrecarga 3:** añade fecha exacta; base de las otras dos |
| `venderBoleto(codViaje, cliente, fila, col, medioPago, maletas)` | Valida: viaje existe → asiento en rango → asiento libre → 1–2 maletas → pesos > 0 → cupo de equipaje → capacidad de boletos. Después: calcula monto (tarifa + sobrepesos), crea `Pago` con la fecha de hoy, **ocupa el asiento**, carga las maletas y emite el código `B0001`, `B0002`… (nunca se reutilizan) |
| `reprogramarBoleto(codBoleto, codNuevoViaje, fila, col)` | Valida: boleto activo → viaje destino existe → asiento destino libre → cupo de equipaje **solo si el bus es distinto** (si es el mismo bus, el equipaje no cambia: neto 0). Transfiere asiento y maletas entre buses y actualiza el boleto |
| `anularBoleto(código)` | Solo boletos activos: libera el asiento, descarga las maletas y marca `activo = false` |
| `listarViajes()` | Texto con todos los viajes |
| `getBoletos()` | **Copia** del arreglo de boletos (los reportes no pueden alterar el estado) |

### 3.10 `service.Reportes` — métodos estáticos
Todos trabajan sobre una copia y **solo cuentan boletos activos**.

| Método | Función |
|---|---|
| `total(Boleto[])` | Boletos vendidos + total recaudado (cierre de caja) |
| `porRuta(Boleto[])` | Agrupa por `origen - destino`: cantidad y monto |
| `porFecha(Boleto[])` | Agrupa por **fecha del viaje**: cantidad y monto |

### 3.11 `util.Validador`
| Método | Función |
|---|---|
| `validarDni(dni)` | Exactamente 8 dígitos |
| `validarCorreo(correo)` | Formato `algo@dominio.tld` |
| `validarFecha(fecha)` | `dd/MM/yyyy` **real** (rechaza `99/99/9999`, `31/02/2026`… usa `ResolverStyle.STRICT`) |
| `validarHora(hora)` | `HH:mm` estricto (`00:00`–`23:59`) |
| `validarPeso(peso)` | Finito y > 0 (rechaza `Infinity`/`NaN`) |
| `leerEntero(sc, mensaje)` | Lee un entero **reintentando** hasta recibir uno válido |
| `leerDecimal(sc, mensaje)` | Lee un decimal **reintentando** hasta recibir uno válido |

### 3.12 `exceptions.AsientoOcupadoException`
`RuntimeException` con mensaje: se lanza desde `GestorVentas` cuando el asiento ya está ocupado (venta o reprogramación) y la capturan `Boletos`/`Registro` para mostrar el error sin caer el programa.

### 3.13 `app.Main` — solo navegación
| Método | Función |
|---|---|
| `main(args)` | Crea `GestorVentas`, carga datos demo y corre el bucle del menú principal (4 categorías) |
| `mostrarMenu()` | Imprime las 4 categorías: Consultas, Boletos, Registro, Reportes |
| `submenu(sc, título, opciones...)` | Helper: imprime `--- TÍTULO ---`, numera las opciones (`1…n`), añade `0. Volver` y devuelve la opción leída |
| `submenuConsultas / submenuBoletos / submenuRegistro / submenuReportes(sc, g)` | Usan el helper y despachan en un `switch` a la clase de la categoría; **vuelven a mostrarse** hasta que se pide `0. Volver` |
| `cargarDatosDemo(g)` | Registra los buses `ABC-123` y `XYZ-789` en la flota y crea `V001` (Lima–Arequipa) y `V002` (Lima–Trujillo) referenciándolos |

### 3.14 `app.Consultas` — flujos del submenú Consultas
| Método | Función |
|---|---|
| `buscar(sc, g)` | Consultas→2: pide origen/destino/fecha y usa la sobrecarga que corresponda |
| `verMapa(sc, g)` | Consultas→3: plano del bus del viaje indicado |

### 3.15 `app.Boletos` — flujos del submenú Boletos
**Atributos:** `MEDIOS_DE_PAGO` (`static final` = {EFECTIVO, TARJETA, YAPE})

| Método | Función |
|---|---|
| `vender(sc, g)` | Boletos→1: pide viaje, cliente (DNI/correo validados), asiento, maletas y pago; captura las 3 excepciones posibles |
| `pedirMaletas(sc, g, cod)` | Pide maleta 1; ofrece la adicional **solo si** pesa ≤ 25 kg y el bus tiene cupo para 2 |
| `leerPeso(sc, mensaje)` | Pide peso validando con `validarPeso` (reintenta) |
| `anular(sc, g)` | Boletos→2 |
| `reprogramar(sc, g)` | Boletos→3: muestra el plano del viaje destino antes de pedir el asiento; captura excepciones |
| `elegirMedioPago(sc)` | 1. EFECTIVO · 2. TARJETA · 3. YAPE (reintenta fuera de rango) |

### 3.16 `app.Registro` — flujos del submenú Registro
| Método | Función |
|---|---|
| `registrarBus(sc, g)` | Registro→1: placa (no vacía, no duplicada) + filas/columnas (ver §4.8) |
| `registrarViaje(sc, g)` | Registro→2: alta de viaje con todas las validaciones; **pide la placa de un bus ya registrado** (ver §4.9) |

---

## 4. Flujo de trabajo — menú y operaciones

Menú principal (consola) — **4 categorías con submenús propios**; en cada submenú, `0. Volver` regresa al principal:

```
=== SISTEMA DE VENTA DE BOLETOS ===
1. Consultas   →  1. Ver viajes · 2. Buscar viajes (origen/destino) · 3. Ver mapa de asientos · 0. Volver
2. Boletos     →  1. Vender boleto · 2. Anular boleto · 3. Reprogramar boleto · 0. Volver
3. Registro    →  1. Registrar bus · 2. Registrar viaje · 0. Volver
4. Reportes    →  1. Reporte de ventas · 2. Reporte por ruta · 3. Reporte por fecha · 0. Volver
0. Salir
```

Cada submenú se vuelve a mostrar tras cada acción (`do-while`), hasta que se pide `0. Volver`. Opción fuera de rango o no numérica → mensaje y reintento.

### 4.1 Consultas → Ver viajes (1)
Lista todos los viajes con tarifa, fecha/hora y asientos libres. Sin entradas.

### 4.2 Consultas → Buscar viajes (2) — sobrecarga de parámetros
1. Pide **origen**, **destino** y **fecha** (vacío = cualquier fecha).
2. Si hay fecha → `buscarViaje(origen, destino, fecha)`; si no → `buscarViaje(origen, destino)`.
3. Muestra resultados o `No se encontraron viajes.`

### 4.3 Consultas → Ver mapa de asientos (3)
Pide el código del viaje y muestra el plano: `[ ]` libre, `[X]` ocupado.

### 4.4 Boletos → Vender boleto (1) — flujo completo
1. **Código de viaje** → debe existir.
2. **DNI** → 8 dígitos (rechaza antes de seguir).
3. **Nombre, apellido** → libres.
4. **Correo** → `validarCorreo` (rechaza antes de seguir).
5. **Teléfono** → libre.
6. **Fila y columna** → leídos como enteros; se validan contra el bus (rango y que esté libre).
7. **Maletas** (ver §4.10): peso 1 siempre; adicional solo si califica.
8. **Medio de pago** → 1/2/3.
9. `GestorVentas.venderBoleto` valida todo de nuevo y emite el boleto; los errores se muestran como `No se pudo vender: …` sin caer el programa.

### 4.5 Boletos → Anular boleto (2)
Pide el código; si existe y está activo: libera asiento + maletas. `Boleto no encontrado o ya anulado.` en caso contrario (doble anulación rechazada).

### 4.6 Boletos → Reprogramar boleto (3)
1. Código del boleto (debe estar activo) y código del viaje destino (debe existir).
2. Muestra el plano del viaje destino y pide nueva fila/columna.
3. Valida asiento libre y cupo de equipaje del bus destino (solo si el bus cambia).
4. Transfiere asiento y maletas; el **pago se conserva** tal cual.

### 4.7 Reportes (1, 2, 3)
| Submenú Reportes | Salida |
|---|---|
| 1 · Total | `Boletos vendidos: N \| Total recaudado: S/ X` |
| 2 · Por ruta | `Lima - Arequipa: 2 boletos \| S/ 160.0` |
| 3 · Por fecha | `15/11/2026: 1 boletos \| S/ 80.0` (agrupa por **fecha del viaje**) |

Los tres ignoran boletos anulados.

### 4.8 Registro → Registrar bus (1)
1. **Placa** → no vacía, no duplicada en la flota (case-insensitive; rechaza al instante si ya existe).
2. **Filas** → entero; el constructor de `Bus` valida **1–40**.
3. **Columnas** → entero; valida **1–6**.
4. Éxito: `Bus registrado: PL-05 (12 filas x 4 columnas)`; si no: `No se pudo registrar: …`.

### 4.9 Registro → Registrar viaje (2)
1. **Código** → no vacío y **único** (rechaza al instante si ya existe).
2. **Origen y destino** → no vacíos.
3. **Tarifa** → decimal > 0 y finito (reintenta: `0`, `-5`, `Infinity`…).
4. **Fecha** → `dd/MM/yyyy` real, estricta (reintenta: `99/99/9999`, `31/02/2026`…).
5. **Hora** → `HH:mm` estricto (reintenta: `25:99`, `8:30`…).
6. **Placa** → debe existir en la flota; si no: `No existe ese bus. Regístrelo primero (menú Registro).` **No pide filas/columnas**: el viaje hereda la ficha del bus registrado y crea su propio estado con `copia()`.
7. Si todo está bien: `Viaje registrado:` con el viaje nuevo; si no: `No se pudo registrar: …`.

> El alta valida en dos niveles: los prompts cortos en la capa de `app` (`Registro`, buena experiencia) y las reglas duras en `GestorVentas`/`Bus` (garantía del estado, válida para cualquier futuro llamador): solo `GestorVentas` instancia los `Bus` de los viajes, siempre desde la flota.

### 4.10 Venta con equipaje — detalle (Boletos → 1)
```
Peso maleta 1 → siempre
  ├─ > 25 kg  → SIN maleta adicional (sobrepeso se cobra igual)
  └─ ≤ 25 kg  → ¿adicionar? (sí/no)
       ├─ el bus debe tener cupo para 2  → si no: "Sin disponibilidad para maleta adicional."
       └─ peso 2 → se cobra su sobrepeso si lo tiene
```

---

## 5. Reglas de negocio (resumen para el informe)

| Tema | Regla |
|---|---|
| **Equipaje** | Límite **25 kg por maleta**; excedente cobra **S/ 5 por kg** |
| **Maleta adicional** | Solo si la primera pesa **≤ 25 kg** y el bus tiene **cupo para 2** («según la disponibilidad») |
| **Cupo de equipaje** | Máx. **2 maletas por boleto** y **20 maletas por bus** (`Bus.MAX_MALETAS`) |
| **Asientos** | Matriz 1-based; venta y reprogramación verifican rango y libertad; anulación libera |
| **Reprogramación** | Conserva el pago; el chequeo de cupo solo aplica si el bus cambia (mismo bus = neto 0) |
| **Anulación** | Solo boletos activos; libera asiento y equipaje; marca `[ANULADO]` |
| **Códigos** | Viaje: el dado en el alta (único). Boleto: `B0001`, `B0002`… correlativo, **nunca reutilizado** |
| **Reportes** | Solo boletos activos; por ruta, por fecha del viaje, y total |
| **Capacidades** | Flota de **20 buses** (placa única) · 20 viajes · 200 boletos · buses de 1–40 filas × 1–6 columnas |
| **Flota vs viaje** | El bus se registra **una vez** (placa, filas, columnas); cada viaje referencia esa ficha y crea su **propio estado** de asientos/equipaje con `copia()` — dos viajes con la misma placa no se afectan entre sí |
| **Búsqueda** | Sobrecarga `buscarViaje(código)` / `(origen, destino)` / `(origen, destino, fecha)`, sin distinguir mayúsculas |
| **Validaciones** | DNI 8 dígitos · correo con formato · fecha real estricta · hora estricta · peso finito > 0 |

---

## 6. Verificación (baterías ejecutadas)

Compilación limpia con `javac --release 21` y pruebas por script (entradas piped a `app.Main`):

1. **Fuzz de entradas inválidas** — menú, DNI, correo, pesos, coordenadas: todo rechazado con reintento, sin excepciones.
2. **Invariantes de estado** — doble anulación, reprogramar boleto anulado/inexistente/mismo asiento.
3. **Cupo de 20 maletas** — 10 boletos × 2 maletas: venta rechazada al llenar; reprogramación a bus lleno rechazada; mismo bus con cupo lleno permitido (neto 0).
4. **Registro de flota** — alta correcta; rechazos: placa duplicada (`ABC-123` y `abc-123`), placa vacía, dimensiones `0/41/7/-3`; flota llena (20 → 21.º rechazado).
5. **Registrar viaje** — alta correcta por placa; rechazos: código duplicado, código/origen vacíos, tarifa `0/-5/Infinity`, fecha inexistente, hora inválida, **placa no registrada** (y flujo de recuperación: registrar la placa y reintentar).
6. **Capacidad de viajes** — 18 altas seguidas (total 20) y rechazo del 21.º.
7. **Reporte por fecha** — 3 fechas, anulación (fila desaparece), reprogramación (fila migra de fecha), totales cuadrados con `total` y `porRuta`.
8. **Regresión de equipaje** — sobrepeso fraccional (25.5 kg → S/ 2.5) y extremos (30 kg → S/ 25 → boleto S/ 105).
9. **Menú por categorías** — navegación completa por los 4 submenús (consultas, boletos, reportes) con `0. Volver`, opciones inválidas y no numéricas reintentadas.
10. **Aislamiento de estado** — dos viajes con la **misma placa**: vendido un asiento en el primero, el mapa del segundo queda **libre** (prueba de `copia()`).
11. **Refactor `app/`** — tras extraer `Consultas`/`Boletos`/`Registro` de `Main` (377 → 152 líneas), las baterías 4, 5, 9 y 10 se re-ejecutaron con **los mismos guiones**: salida idéntica, sin excepciones (cero cambio de comportamiento).

---

## 7. Flujo de desarrollo (git)

- `master`: estado estable del proyecto.
- Ramas `feature/…` (casos de uso) y `refactor/…` (refactors) para cada cambio; se fusionan a `master` con **fast-forward** tras pasar las pruebas.
- Commits en español, descriptivos del caso de uso.
- Historial: `13bb3a0` refactor base → `6e79cd1` reportes → `ec288cb` equipaje → `2c571af` fixes de pruebas → `7a6ef8d` registrar viaje + reporte por fecha → `2f386ad` guía → `8506456` submenús + flota → `c85ce5b` guía de flota (fusionado a `master`) → `438b0fd` modulariza `Main` en `app/Consultas|Boletos|Registro` → `43d9406` guía modular.

---

## 8. Pendientes conocidos para el informe

*(Notas para actualizar TALLER PROGRAMACION.md — esta guía no lo modifica.)*

1. **Lista de clases UML (sección 2.1):** el informe dice *«Ruta, Bus, Viaje, Asiento, Cliente, Boleto y Vendedor»* → falta **`Maleta`** y **`Pago`**, y **sobra `Vendedor`** (el sistema no tiene roles ni login). Conviene decidir si el diagrama incluye también `GestorVentas`, `Reportes` y `Validador`.
2. **Reportes (2.1):** dice «ventas por ruta, fecha y vendedor, y cierre de caja» → ya existen **total**, **por ruta** y **por fecha**; no hay reporte por vendedor (coherente con la decisión de no tener roles).
3. **Menú:** el informe habla de «registrar rutas, buses y viajes» — ahora implementado en el **submenú Registro**: `1. Registrar bus` (placa + capacidad) y `2. Registrar viaje` (ruta, horario y placa de un bus ya registrado). Esto coincide textualmente con el 2.1 del informe («la capacidad de cada bus»).
4. **Secciones vacías del informe:** 2.2 pantallazos (5 imágenes), 3.1 estructura del código fuente (§2 de esta guía), 3.2 ejecutables, 4 conclusiones (mín. 3), bibliografía (mín. 3), anexos.
5. **Límites de diseño asumidos** (mencionar si aplica): la flota impide placas inconsistentes, pero **no se valida solapamiento** (el mismo bus con dos viajes en la misma fecha/hora); el pago no se recalcula al reprogramar; los datos no persisten entre ejecuciones.
