# Website
Source code for Valinor website

Full-stack functional programming with ZIO (backend) and Laminar (frontend).

Create a Scala service for managing "jobs". It should be possible to create, list, update, and delete jobs.

A job has a unique id (your choice on ID generation and representation), title, optional description, status, created timestamp, and status change timestamp.

When a job status changes, an appropriate message should be published by the API to some "external" service. (This service can be mocked). Assume that this publish service is flaky, and that publishing may need to be retried.

Additional requirements:

* The application should be wired up using ZIO layers.
* The backing server should be ZIO HTTP.
* The cases should be stored in PostgreSQL.
* Some amount of unit or integration testing (whatever is your preferred style) should be implemented.

# PostgreSQL Setup

Before running WebsiteApp or WebsiteSpec, please run these commands to set up PostgreSQL locally (Mac terminal):

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
