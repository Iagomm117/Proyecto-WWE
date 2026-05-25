FROM mysql:8.0

COPY /init /docker-entrypoint-initdb.d/

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=wwe_db
ENV MYSQL_USER=admin
ENV MYSQL_PASSWORD=admin123