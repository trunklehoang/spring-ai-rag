#!/bin/sh
docker pull ankane/pgvector:latest
#docker run --name aidoclibchat-postgres -e POSTGRES_PASSWORD=sven1 -e POSTGRES_USER=sven1 -e POSTGRES_DB=aidoclibchat -p 5432:5432 -d ankane/pgvector
#docker run --name aidoclibchat-postgres-ollama -e POSTGRES_PASSWORD=sven1 -e POSTGRES_USER=sven1 -e POSTGRES_DB=aidoclibchat -p 5432:5432 -d ghcr.io/postgresml/postgresml:2.7.12 -v postgresml_data:/var/lib/postgresql
docker run \
    -v postgresml_data:/var/lib/postgresql \
    -p 5433:5432 \
    -p 8000:8000 \
    ghcr.io/postgresml/postgresml:2.7.12 \
    sudo -u postgresml psql -d postgresml
# docker start aidoclibchat-postgres
# docker stop aidoclibchat-postgres
# docker start aidoclibchat-postgres-ollama
# docker stop aidoclibchat-postgres-ollama