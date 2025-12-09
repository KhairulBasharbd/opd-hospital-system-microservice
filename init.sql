CREATE DATABASE auth_db;
CREATE DATABASE doctor_db;
CREATE DATABASE appointment_db;

CREATE USER auth_user WITH PASSWORD 'auth_pass';
CREATE USER doctor_user WITH PASSWORD 'doctor_pass';
CREATE USER appointment_user WITH PASSWORD 'appointment_pass';

GRANT ALL PRIVILEGES ON DATABASE auth_db TO auth_user;
GRANT ALL PRIVILEGES ON DATABASE doctor_db TO doctor_user;
GRANT ALL PRIVILEGES ON DATABASE appointment_db TO appointment_user;
