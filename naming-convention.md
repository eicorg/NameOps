# Naming Convention Specification

## 3. Syntax Requirements

The generalized nomenclature syntax is:

```
aa:bb-ddpp.zz_nn-ccss
```

### Naming Elements

| Element | Description | Applies To |
|---------|-------------|-----------|
| aa | Area from naming repository | Beamline & Non-beamline |
| bb | Specific area from naming repository (+ number) | Non-beamline only |
| dd | Device from naming repository | Beamline & Non-beamline |
| pp | Position number | Beamline & Non-beamline |
| zz | Secondary position number | Non-beamline only |
| nn | Appended number | Beamline & Non-beamline |
| cc | Controller device from naming repository | Beamline only |
| ss | Signal classification from naming repository | Beamline & Non-beamline |

---

## 3.1 Lattice Device Syntax Requirements

For beamline components and directly controlling/reading devices:

```
aa-ddpp_nn-ccss
```

### 3.1.1 Area (aa)

The area is defined by the machine prefix.

### 3.1.2 Device (dd)

The device is defined by the component function.

### 3.1.3 Position (pp)

The position is defined by the S-coordinate distance (in meters) from the machine prefix start.

### 3.1.4 Append Number (nn)

An append number is applied when multiple instances of the same device type are present within a single meter section.

This number indicates the sequential occurrence of the device within that section.

### 3.1.5 Controller (cc)

The controller identifies devices that perform direct control or readback functions.

### 3.1.6 Signal (ss)

The signal is used when it is necessary to specify a signal type.

---

## 3.2 Non-Lattice Device Syntax Requirements

Naming for non-lattice devices is based on physical location:

```
aa:bb-ddpp.zz_nn-ss
```

### 3.2.1 Area (aa) and Specific Area (bb)

- **Area (aa):** Defined by the device's installation location.
  - Tunnel devices use the predefined machine prefix.
  - Building devices use approved building abbreviations from the naming repository.
- **Specific Area (bb):** Defines a distinct location within an area.

### 3.2.2 Device (dd)

The device is defined by the component function.

### 3.2.3 Position (pp) and Secondary Position (zz)

- **Position (pp):** Used when more than one device type exists within the same area or specific area.
- **Secondary Position (zz):** Used when a device has both horizontal and vertical positioning.

### 3.2.4 Append Number (nn)

Used to identify connection points on a device.

### 3.2.5 Signal (ss)

Used when it is necessary to specify a signal type.
