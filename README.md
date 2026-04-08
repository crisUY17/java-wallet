# java-wallet
Aplicación de escritorio para gestión de finanzas personales, desarrollada en Java 21 con Maven y una interfaz gráfica en Java Swing.

## Características

- Gestión de múltiples cuentas en distintas monedas (Pesos Uruguayos, Dólares, UI)
- Registro de ingresos y egresos por cuenta
- Transferencias entre cuentas con integridad transaccional (rollback automático ante fallos)
- Historial de transacciones por cuenta y global
- Balance total agrupado por moneda
- Persistencia de datos con SQLite

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje principal |
| Maven | Build y gestión de dependencias |
| SQLite (xerial) | Persistencia local |
| Java Swing | Interfaz gráfica de escritorio |
| JUnit 5 | Tests unitarios e integración |
| Launch4j | Generación de ejecutable Windows (.exe) |

## Arquitectura

El proyecto sigue una arquitectura en capas con separación clara de responsabilidades:

```
src/main/java/com/cristian/wallet/
├── dao/          # Acceso a datos (interfaces + implementaciones SQLite)
├── model/        # Entidades del dominio (Account, Transaction, Currency, TipoTransaccion)
├── service/      # Lógica de negocio (Controller)
└── ui/           # Interfaz de usuario (Swing + IController)
```

- **`model`** — Entidades puras sin dependencias externas
- **`dao`** — Interfaces (`IAccountDAO`, `ITransactionDAO`) con implementaciones SQLite. La inyección de conexión permite testear con BD en memoria sin tocar el código de producción
- **`service`** — `Controller` implementa `IController` y orquesta la lógica de negocio. No conoce detalles de persistencia ni de UI
- **`ui`** — `SwingUI` y paneles de Swing. Dependen únicamente de `IController`, lo que permite intercambiar la implementación sin tocar la interfaz

## Requisitos

- Java 21 o superior
- Maven 3.8 o superior

## Instalación y ejecución

### Windows
Descargá el instalador `java-wallet.exe` desde [Releases](https://github.com/crisUY17/java-wallet/releases) y ejecutalo directamente. Requiere Java 21 instalado.

### Desde el código fuente
```bash
# Clonar el repositorio
git clone https://github.com/crisUY17/java-wallet.git
cd java-wallet

# Compilar y generar ejecutables
mvn package

# Ejecutar JAR (multiplataforma)
java -jar target/java-wallet-1.0-SNAPSHOT-jar-with-dependencies.jar

# O usar el ejecutable de Windows generado en
target/java-wallet.exe
```

## Decisiones de diseño

**Inyección de dependencias manual** — Los DAOs se inyectan al Controller por constructor, lo que facilita el testing y permite cambiar la implementación de persistencia sin modificar la lógica de negocio.

**Integridad transaccional en transferencias** — Las transferencias entre cuentas utilizan transacciones SQL con `commit`/`rollback` para garantizar que si falla alguno de los dos movimientos, ninguno se persiste.

**Cascade delete** — Al eliminar una cuenta se eliminan automáticamente todas sus transacciones mediante `ON DELETE CASCADE` en SQLite (requiere `PRAGMA foreign_keys = ON`).

**Interfaces en todas las capas** — `IController`, `IAccountDAO` e `ITransactionDAO` permiten intercambiar implementaciones sin afectar al resto del sistema.