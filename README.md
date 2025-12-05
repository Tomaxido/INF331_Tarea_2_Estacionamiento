# INF331_Tarea_2_Estacionamiento

Desarrollo de un sistema que gestiona el cobro de un estacionamiento para distintos tipos de vehículos.

# Calculadora de Tarifas de Estacionamiento

Aplicación de consola en Java que gestiona tickets de estacionamiento y calcula el monto a pagar según tipo de vehículo, tiempo estacionado, tope diario y descuento de fin de semana.

## Diseño general

Se modelan los tickets, la lógica de negocio y la interfaz de consola de manera separada:

- `modelo.Ticket`: representa un ticket con `id`, `patente`, `tipoVehiculo`, `fechaHoraEntrada`, `fechaHoraSalida`, `estado` y `montoCobrado`.
- `modelo.TipoVehiculo`: enum con `AUTO`, `MOTO`, `CAMIONETA`.
- `modelo.EstadoTicket`: enum con `ABIERTO` y `CERRADO`.
- `servicio.CalculadoraTarifas`: encapsula las reglas de cálculo de minutos, bloques de 30 minutos, tarifas por tipo de vehículo, tope diario ($15.000) y descuento de fin de semana (10 % usando la fecha de entrada).
- `servicio.GestorTickets`: administra los tickets en memoria usando un mapa, asigna `id` incrementales, registra entradas y salidas, lista tickets abiertos/cerrados y calcula el total recaudado por día (usando la **fecha de salida**).
  - Además valida que la patente no sea nula ni vacía y tenga a lo más 6 caracteres; en caso contrario lanza una excepción de argumento inválido.
- `AplicacionEstacionamiento`: programa de consola (CLI) que muestra el menú principal, pide datos al usuario y llama a los servicios para registrar entradas/salidas, listar tickets, ver detalle y mostrar el total recaudado del día.

## Requisitos

- Java 21
- Maven 3.x

Para verificar versiones:

```bash
java -version
mvn -version
```

## Cómo compilar y ejecutar

### Compilar el proyecto

Desde la raíz del proyecto:

```bash
mvn clean compile
```

### Ejecutar las pruebas

```bash
mvn test
```

Ejemplo de salida:

```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running cl.pruebas.estacionamiento.servicio.CalculadoraTarifasTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.067 s -- in cl.pruebas.estacionamiento.servicio.CalculadoraTarifasTest
[INFO] Running cl.pruebas.estacionamiento.servicio.GestorTicketsTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.019 s -- in cl.pruebas.estacionamiento.servicio.GestorTicketsTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO]
[INFO] --- jacoco:0.8.11:report (report) @ calculadora-estacionamiento ---
[INFO] Loading execution data file C:\Users\Tomax\Documents\Universidad\2025\2025-2\PruebasSW\Tarea 2\INF331_Tarea_2_Estacionamiento\target\jacoco.exec
[INFO] Analyzed bundle 'calculadora-estacionamiento' with 6 classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.345 s
[INFO] Finished at: 2025-12-05T01:01:58-03:00
[INFO] ------------------------------------------------------------------------
```

### Generar el JAR y ejecutar la aplicación de consola

Para empaquetar:

```bash
mvn package
```

Esto genera un JAR en `target/` (por ejemplo `target/calculadora-estacionamiento-1.0.0-SNAPSHOT.jar`).

Para ejecutar la aplicación por consola:

```bash
java -cp target/calculadora-estacionamiento-1.0.0-SNAPSHOT.jar cl.pruebas.estacionamiento.AplicacionEstacionamiento
```

## Menú principal (CLI)

Al ejecutar la aplicación se muestra un menú de texto:

```bash
1. Registrar entrada de vehículo  
2. Registrar salida de vehículo (calcular cobro)  
3. Listar tickets abiertos  
4. Listar tickets cerrados  
5. Mostrar detalle de un ticket  
6. Mostrar total recaudado del día  
7. Salir  
```

Resumen de comportamiento de cada opción:

- **1. Registrar entrada de vehículo**  
  - Solicita la patente y el tipo de vehículo (AUTO, MOTO, CAMIONETA).  
  - Valida que la patente, una vez recortada (`trim`), no esté vacía y tenga a lo más 6 caracteres.  
  - Si la patente no es válida, se informa el error por consola y no se crea el ticket.  
  - Si es válida, se registra un ticket con estado `ABIERTO` usando la fecha y hora actual como `fechaHoraEntrada`.

- **2. Registrar salida de vehículo**  
  - Solicita el `id` del ticket a cerrar.  
  - Si el ticket no existe, se informa el error.  
  - Si el ticket ya está cerrado, se informa que no se puede cerrar nuevamente.  
  - Si el ticket está abierto, se calcula el monto a pagar según las reglas de negocio y se marca el ticket como `CERRADO`, usando la fecha y hora actual como `fechaHoraSalida`.

- **3. Listar tickets abiertos**  
  - Muestra los tickets cuyo estado es `ABIERTO`, ordenados por `id`.  
  - Si no hay tickets abiertos, se muestra un mensaje indicando que no existen registros.

- **4. Listar tickets cerrados**  
  - Muestra los tickets cuyo estado es `CERRADO`, con información de entrada, salida y monto cobrado, ordenados por `id`.  
  - Si no hay tickets cerrados, se muestra un mensaje indicando que no existen registros.

- **5. Mostrar detalle de un ticket**  
  - Solicita el `id` del ticket.  
  - Si el ticket no existe, se informa por consola.  
  - Si existe, se muestran: patente, tipo de vehículo, fecha/hora de entrada y:
    - Si el ticket está **abierto**: se indica que sigue abierto y se muestra el tiempo estacionado como “en curso”.
    - Si el ticket está **cerrado**: se muestra la fecha/hora de salida, el **tiempo estacionado en minutos** (duración entre entrada y salida) y el monto cobrado.

- **6. Mostrar total recaudado del día**  
  - Toma la fecha actual y calcula la suma de `montoCobrado` para todos los tickets cerrados cuya **fecha de salida** corresponde al día actual.  
  - Muestra el total recaudado en pesos.

- **7. Salir**  
  - Termina la ejecución de la aplicación.

## Reglas de cálculo de tarifas

Al registrar la salida de un vehículo se aplican las siguientes reglas:

1. **Duración del estacionamiento**  
   - Se calcula la duración en minutos entre `fechaHoraEntrada` y `fechaHoraSalida`.  
   - Si la duración es menor o igual a 0 minutos, la operación se considera inválida y se lanza una excepción.

2. **Bloques de tiempo**  
   - El cobro se hace por bloques de 30 minutos, redondeando hacia arriba.  
   - Ejemplos:
     - 1 a 30 min → 1 bloque  
     - 31 a 60 min → 2 bloques  
     - 61 a 90 min → 3 bloques, etc.

3. **Tarifa por tipo de vehículo (por bloque de 30 minutos)**  
   - AUTO: $800  
   - MOTO: $500  
   - CAMIONETA: $1.000  

4. **Tope diario**  
   - El monto total a pagar por un ticket no puede exceder $15.000.  
   - Si el cálculo por bloques supera este valor, se cobra el tope.

5. **Descuento fin de semana**  
   - Si la **fecha de entrada** del ticket corresponde a sábado o domingo, se aplica un 10 % de descuento al valor final (después de aplicar el tope diario si corresponde).  
   - El descuento se redondea hacia abajo al entero más cercano.

Toda esta lógica está concentrada en `CalculadoraTarifas` y se valida con pruebas unitarias.

## Pruebas y cobertura

Las pruebas unitarias se implementan con JUnit 5 y cubren:

- Cálculo de bloques y tarifas por tipo de vehículo.
- Aplicación del tope diario.
- Aplicación del descuento de fin de semana.
- Manejo de duraciones inválidas.
- Flujo de creación y cierre de tickets.
- Restricciones al cerrar tickets inexistentes o ya cerrados.
- Cálculo del total recaudado usando la fecha de salida.
- Validación de patentes con longitud máxima de 6 caracteres.

La cobertura se mide con **JaCoCo**, configurado como plugin de Maven. El reporte se genera automáticamente al ejecutar:

```bash
mvn test
```

Y se puede revisar abriendo en el navegador:

```text
target/site/jacoco/index.html
```

### ¿Qué tipo de cobertura se ha medido y por qué?

JaCoCo entrega principalmente **cobertura de líneas** y **cobertura de ramas**. En este proyecto el foco está en la cobertura de **líneas**, para asegurar que la lógica central del negocio (cálculo de tarifas, validaciones, cambios de estado y consultas) efectivamente se ejecuta durante los tests. Además se revisa la cobertura de ramas para comprobar que los casos borde importantes (duración inválida, ticket inexistente, ticket ya cerrado, aplicación del tope y del descuento de fin de semana) tienen pruebas que recorren los diferentes caminos de decisión.

## Licencia

MIT License

Copyright (c) 2025 Tomas Castillo Castillo