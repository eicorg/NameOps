# NameOps

A naming convention system for EIC (Electron-Ion Collider) device nomenclature.

## Overview

This repository provides services to create, generate, and validate standardized device names for EIC (Electron-Ion Collider) components. The system helps ensure consistent naming across all EIC devices and infrastructure.

## Contents

- **[Naming Convention](naming-convention.md)** - Detailed specification for device naming syntax and requirements
- **Resources** - Supporting documents and reference materials
  - EIC-SEG-RSI-009 REV1 specification document
  - ES SWN lattice data
  - Naming examples
  - SWN abbreviation database

## Quick Reference

The nomenclature syntax for EIC devices is:

```
aa:bb-ddpp.zz_nn-ss
```

### Key Elements
- **aa** - Area (installation location)
- **bb** - Specific area within location
- **dd** - Device function
- **pp** - Position number
- **zz** - Secondary position (for devices with horizontal/vertical positioning)
- **nn** - Append number (connection points)
- **ss** - Signal classification

For detailed syntax requirements and element descriptions, see the [Naming Convention](naming-convention.md) document.

## Features

- **Name Generation** - Create standardized device names following EIC conventions
- **Name Validation** - Verify device names comply with naming standards
- **Repository Access** - Utilize approved abbreviations and naming databases

## Resources

All reference documents and databases are located in the `resources/` directory.

## Documentation

- [Naming Convention Specification](naming-convention.md) - Complete syntax requirements and guidelines

## Date

Last updated: March 19, 2026
