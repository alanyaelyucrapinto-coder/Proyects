# Ejercicio 11 — Padrón de migrantes

CRUD en JavaFX para el registro de personas en proceso de migración.

## Campos
- Nombre completo (TextField)
- País de origen (TextField)
- Tipo de visa (ComboBox editable)
- Fecha de ingreso (DatePicker)
- Estatus migratorio (RadioButton): Regular, Irregular, Solicitante de asilo

## Funciones
Crear, consultar, modificar y eliminar. Búsqueda por nombre, país, estatus o ID.
Validaciones: campos obligatorios, estatus elegido y fecha de ingreso no futura.

## Ejecutar
JDK 21. Abre `SysVentas` en IntelliJ como proyecto Maven, o:

```bash
./mvnw clean javafx:run
```

Los datos se guardan en memoria.
