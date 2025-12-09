# OPD Hospital System - Database Infrastructure

This folder contains the Docker configuration for running the databases.

## Quick Start

```bash
# Start all databases
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f

# Stop databases
docker-compose down

# Stop and remove data volumes (CAUTION: deletes all data)
docker-compose down -v
```

## Database Details

| Service | Database | Type | Port | Database Name |
|---------|----------|------|------|---------------|
| Auth Service | PostgreSQL | SQL | 5432 | `opd_auth_db` |
| Doctor Service | PostgreSQL | SQL | 5432 | `opd_doctor_db` |
| Appointment Service | PostgreSQL | SQL | 5432 | `opd_appointment_db` |
| Billing Service | MongoDB | NoSQL | 27017 | `opd_billing_db` |

## Default Credentials

| Database | Username | Password |
|----------|----------|----------|
| PostgreSQL | `opd_user` | `opd_password` |
| MongoDB | `opd_user` | `opd_password` |

> **Note**: For production, update credentials in `.env` file and add it to `.gitignore`.

---

## Connecting with Database Tools

### PostgreSQL with pgAdmin

1. **Download pgAdmin**: https://www.pgadmin.org/download/
2. **Add New Server**:
   - Right-click "Servers" → "Register" → "Server"
   - **General Tab**:
     - Name: `OPD Hospital DB`
   - **Connection Tab**:
     - Host: `localhost`
     - Port: `5432`
     - Username: `opd_user`
     - Password: `opd_password`
3. **Click Save**
4. Browse databases: `opd_auth_db`, `opd_doctor_db`, `opd_appointment_db`

### PostgreSQL with DBeaver

1. **Download DBeaver**: https://dbeaver.io/download/
2. **Create New Connection**:
   - Click "Database" → "New Database Connection"
   - Select "PostgreSQL"
3. **Connection Settings**:
   - Host: `localhost`
   - Port: `5432`
   - Database: `opd_auth_db` (or any of the DBs)
   - Username: `opd_user`
   - Password: `opd_password`
4. **Click "Test Connection"** → **"Finish"**

### MongoDB with MongoDB Compass

1. **Download MongoDB Compass**: https://www.mongodb.com/products/compass
2. **New Connection**:
   - Connection String: 
     ```
     mongodb://opd_user:opd_password@localhost:27017/?authSource=admin
     ```
3. **Click "Connect"**
4. Browse database: `opd_billing_db`

### Command Line Access

```bash
# PostgreSQL (requires psql installed)
psql -h localhost -p 5432 -U opd_user -d opd_auth_db

# MongoDB (requires mongosh installed)
mongosh "mongodb://opd_user:opd_password@localhost:27017/opd_billing_db?authSource=admin"
```

---

## Troubleshooting

### Port already in use
```bash
# Check what's using the port
netstat -ano | findstr :5432  # Windows
lsof -i :5432                  # Linux/Mac

# Change port in docker-compose.yml if needed
ports:
  - "5433:5432"  # Use 5433 on host
```

### Container won't start
```bash
# Check logs
docker-compose logs postgres
docker-compose logs mongodb

# Restart with fresh data
docker-compose down -v
docker-compose up -d
```

### Connection refused
1. Ensure Docker containers are running: `docker-compose ps`
2. Ensure firewall allows connections on ports 5432 and 27017
3. Wait for containers to fully start (check health status)
