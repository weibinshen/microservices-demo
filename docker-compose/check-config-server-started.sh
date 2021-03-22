#!/bin/bash
# check-config-server-started.sh

# depends_on from Docker is not enough for us,
# because that only checks if the Docker container is up,
# not whether our application inside the container is up.
# We need to make sure we run 'chmod +x check-config-server-started.sh'

apt-get update -y

yes | apt-get install curl

curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health)

echo "result status code:" "$curlResult"

while [[ ! $curlResult == "200" ]]; do
  >&2 echo "Config server is not up yet!"
  sleep 2
  curlResult=$(curl -s -o /dev/null -I -w "%{http_code}" http://config-server:8888/actuator/health)
done

# move on with launching our service. This is the original entrypoint.
# To run bash shell on a specific container, use 'docker exec -it <containerID> /bin/bash'
./cnb/lifecycle/launcher