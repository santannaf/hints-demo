#!/bin/bash

set -e

echo "Starting Database (Postgres, Redis, Oracle)..."
docker compose -f docker-compose.database.yaml up -d

echo ""
echo "Starting Kafka (Zookeeper, Kafka, Schema Registry, Control Center)..."
docker compose -f docker-compose.kafka.yaml up -d

echo ""
echo "Starting OpenTelemetry (Collector, Tempo, Loki, Prometheus, Grafana, Jaeger, Zipkin)..."
docker compose -f docker-compose.opentelemetry.yaml up -d

echo ""
echo "All services started."
