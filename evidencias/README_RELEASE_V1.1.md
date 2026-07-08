# README release academico v1.1

Este paquete local prepara la evidencia complementaria del Capitulo 4 posterior al cierre tecnico `a5947c69399a4355fab06514def75cd27a64c5fb`.

## Versiones y consistencia

- El tag historico `v1.0-cap4-final-evidence` continua apuntando al commit `a5947c69399a4355fab06514def75cd27a64c5fb`.
- La evidencia unitaria complementaria C4-EV-009 se mantiene asociada al commit `a3a44494adb149e6567b01c2e2bcb5572aa98102`.
- Las nuevas pruebas end-to-end y de fallos asincronos pertenecen a la rama de ajustes finales y a commits posteriores.
- No se reescribio historial y no se modifico el tag anterior.

## Contenido recomendado para release permanente

- `evidencias/capitulo4-anexos-finales/05_pruebas_backend/pago_simulado_negativo_e2e/`
- `evidencias/capitulo4-anexos-finales/04_rabbitmq/fallos_asincronos/`
- `evidencias/capitulo4-anexos-finales/05_pruebas_backend/REPORTE_JWT_SEGURIDAD_Y_LIMITACIONES.md`
- `evidencias/capitulo4-anexos-finales/08_drp/REPORTE_TERRAFORM_BACKEND_LOCAL_LIMITACIONES.md`
- `evidencias/capitulo4-anexos-finales/09_trazabilidad_capitulo4/TRAZABILIDAD_ITERACIONES_COMMITS_VERSIONES.md`
- `evidencias/capitulo4-anexos-finales/09_trazabilidad_capitulo4/MATRIZ_COMPLETA_CASOS_PRUEBA.md`
- `evidencias/capitulo4-anexos-finales/08_drp/aws_cap4_ajustes_final/`

## Tag y release propuestos

- Tag propuesto: `v1.1-cap4-final-evidence`
- Mensaje de tag propuesto: `Release Capitulo 4 final evidence with tutor-requested E2E and asynchronous failure tests`
- Titulo de release propuesto: `Capitulo 4 Final Evidence Package v1.1`

## Acciones manuales pendientes

1. Integrar la rama de ajustes finales mediante PR.
2. Crear el tag anotado `v1.1-cap4-final-evidence` sobre el commit final integrado.
3. Crear el GitHub Release asociado al tag.
4. Adjuntar `evidencias/capitulo4-anexos-finales-release-v1.1.zip`.
5. Verificar los enlaces persistentes desde una ventana sin autenticacion si el repositorio sera publico.
6. Depositar una copia del ZIP en el repositorio institucional o medio archivistico definido por la universidad.

