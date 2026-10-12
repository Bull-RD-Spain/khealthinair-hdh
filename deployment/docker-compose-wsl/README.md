# K-HEALTHinAIR HDH Module (Health Data Hub - CDR) — Local Deployment

Adaptation of the HDH module Docker Compose for local development on WSL2 + Podman, integrated with the other platform modules.

> If the base environment is not set up, follow the guide: [WSL2 + Podman environment installation guide](./guide-podman-wsl2_with_troubleshoothing.md)

## Services

| Service | Image | Local port | Description |
|---------|-------|------------|-------------|
| `hdh-cdr-db` | `postgres:17.0` | 5433 | HAPI FHIR database (PostgreSQL) |
| `hdh-cdr` | `hdh-cdr` (local build from `../../hdh-cdr`) | 8090 | HAPI FHIR server |
| `hdh-cdr-api` | `hdh-cdr-api` (local build from `../../hdh-cdr-api`) | 8091 | CDR API (optional, via profile) |

## Changes with respect to the original

| Original | Local adaptation | Reason |
|----------|------------------|--------|
| `postgres:17.0` | `docker.io/library/postgres:17.0` | FQDN for Podman |
| `khealthinair/hdh-cdr` (private registry) | Image built locally (`build: ../../hdh-cdr`) | No dependency on a private registry |
| Port 5432 (PostgreSQL) | Port 5433 | Port 5432 is used by the AQDR platform-db |
| Port 8080 (hdh-cdr) | Port 8090 | Avoid conflicts with other services |
| Port 8081 (hdh-cdr-api) | Port 8091 | Consistency |
| Network `khealthinair-hdh-net` | Network `khealthinair-net` | Network shared between modules |
| Profile `donotstart` | Profile `hdh-api` | More descriptive name |
| `CDR_HAPI_FHIR_SERVER_ADDRESS` with port 8080 | Port 8090 | Aligned with the local port mapping |

## Usage

```bash
cd ~/khealthinair-hdh/deployment/docker-compose

# Start the base services (db + FHIR server)
docker-compose up -d

# Also start the API (optional)
docker-compose --profile hdh-api up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f

# Stop
docker-compose down

# Full reset
docker-compose down -v
```

## Access

| Interface | URL |
|-----------|-----|
| HAPI FHIR Server | http://localhost:8090/hdh-cdr |
| CDR API (with profile) | http://localhost:8091/hdh-cdr-api |

## Network

The services communicate through the shared `khealthinair-net` network. This network is created automatically if it does not exist, or reused if another module (AQDR, DAQSR) has already created it.

## Ports for the whole platform

| Port | Service | Module |
|------|---------|--------|
| 3000 | Grafana | AQDR |
| 5432 | PostgreSQL (platform-db) | AQDR |
| 5433 | PostgreSQL (hdh-cdr-db) | HDH |
| 8083 | AQDR API | AQDR |
| 8084 | Adminer | AQDR |
| 8086 | InfluxDB | AQDR |
| 8090 | HAPI FHIR Server | HDH |
| 8091 | CDR API | HDH |
| 9000 | MinIO | DAQSR |
| 9001 | MinIO Console | DAQSR |

## Troubleshooting

### PostgreSQL: port conflict

If port 5433 is already in use, check with `ss -tlnp | grep 5433` and change it in `docker-compose.yaml`.

### HAPI FHIR is slow to start

The HAPI FHIR server may take several minutes to start the first time (it creates the database schema). The healthcheck has a `start_period: 30s`, but it may need more. Monitor it with `docker-compose logs -f hdh-cdr`.

### Corporate proxy

If curl hangs, make sure the proxy exceptions are configured in `~/.bashrc`:

```bash
export no_proxy="localhost,127.0.0.1,10.89.0.0/16"
export NO_PROXY="localhost,127.0.0.1,10.89.0.0/16"
```
