#!/bin/bash

until mongosh --host mongo:27017 --eval 'quit(0)' &>/dev/null; do
  echo "Waiting for MongoDB to start..."
  sleep 5
done

echo "MongoDB is ready. Initializing replica set..."

mongosh --host mongo:27017 -u "$MONGO_USER" -p "$MONGO_PASSWORD" --authenticationDatabase admin <<EOF
rs.initiate({
  _id: "rs0",
  members: [
    { _id: 0, host: "mongo:27017" }
  ]
})
EOF

echo "Replica set initialized successfully."