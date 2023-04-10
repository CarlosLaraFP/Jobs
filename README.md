# Scala Jobs Online Marketplace

Full-stack functional programming with ZIO (backend) and Laminar (frontend).

Currently creating a Scala microservice for managing "jobs" in an online marketplace (create, list, update, and delete jobs).

# TODO:

* More extensive coverage of ZIO Prelude (such as ZPure and other functional data types & abstractions)
* Property-based testing
* Dependency injection with ZLayers
* Doobie for PostgreSQL DB layer
* Remaining HTTP methods
* Laminar frontend

See below once we implement Doobie:

# PostgreSQL Setup

Before running JobsApp or JobSpec, please run these commands to set up PostgreSQL locally (Mac terminal):

1. /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
2. brew install postgresql
3. initdb /usr/local/var/postgres
4. pg_ctl -D /usr/local/var/postgres start
5. createdb jobsdb
6. psql jobsdb
7. CREATE USER postgres WITH LOGIN PASSWORD 'postgres';
8. GRANT ALL PRIVILEGES ON DATABASE jobsdb TO postgres;
9. \q

When you are done:

10. pg_ctl stop -D /usr/local/var/postgres
