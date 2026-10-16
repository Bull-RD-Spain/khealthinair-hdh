# K-HEALTHinAIR Health Data Hub (HDH)

## Description

The K-HEALTHinAIR Health Data Hub (HDH) includes the subcomponents in charge of the aggregation and maganement of the clinical data that comes from the participants clinical historial, questionnaires and monitoring.

### The Clinical Data Repository (CDR) (`hdh-cdr-db` and `hdh-cdr`)

Based on PostgreSQL and HAPI-FHIR project, provides storage for the clinical data, in a standarized HL7 FHIR.

### The Clinical Data Repository API (CDR-API) (`hdh-cdr-api`)

Based on FastAPI and fhirclient, provides the proect interface to ingest and access the clinical data, based on the project reference data model, abstracting from the standarized HL7 FHIR.

## Prerequisites

In order to run this platform, you should have this project code, which contains several sub-componets, checked out from this public repository (https://github.com/Bull-RD-Spain/khealthinair-hdh).

## License

Copyright © 2021-2026 Bull SAS

SPDX-License-Identifier: GPL-3.0-or-later

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. See [LICENSE.txt](LICENSE.txt) for the full text, and [THIRD_PARTY_LICENSES.md](THIRD_PARTY_LICENSES.md) for third-party components.
