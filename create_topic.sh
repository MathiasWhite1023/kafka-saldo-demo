#!/usr/bin/env bash
TOPIC=${TOPIC:-contas}
BOOTSTRAP=${BOOTSTRAP_SERVERS:-localhost:9092}
docker-compose exec -T kafka bash -c "kafka-topics --create --topic $TOPIC --bootstrap-server $BOOTSTRAP --partitions 1 --replication-factor 1 --config cleanup.policy=compact" || true
echo "Tópico '$TOPIC' criado (compactado)."
