FROM mysql:8.3.0

COPY ./quartz-table.sql /docker-entrypoint-initdb.d/

