#!/usr/bin/env bash

: "${HOSTNAME:?Need to set HOSTNAME non-empty (can also run the container with 'docker run --hostname=...' specified or use its analogous docker-compose 'hostname:...' property)}"
: "${VHOST_CORNER_HOSTNAME:?Need to set VHOST_CORNER_HOSTNAME non-empty}"
: "${NODES_COUNT:?Need to set NODES_COUNT non-empty}"
: "${NODE_HOSTNAMES:?Need to set NODE_HOSTNAMES non-empty}"
: "${NODE_PORT_NUMBERS:?Need to set NODE_PORTS non-empty}"

configureBalancedMembers() {
	echo "Configuring load balancer..."

	local nodeHostnames=(${NODE_HOSTNAMES//,/ })
	local nodePortNumbers=(${NODE_PORT_NUMBERS//,/ })

	if (( NODES_COUNT < 2 )); then
		echo "The number of nodes must be at least 2: NODES_COUNT=${NODES_COUNT}"
		exit 1
	fi

	if (( ${#nodeHostnames[@]} != NODES_COUNT )); then
		echo "Please provide the same number of nodes and node hostnames: NODES_COUNT=${NODES_COUNT}, NODE_HOSTNAMES=${NODE_HOSTNAMES}"
		exit 1
	fi

	if (( ${#nodePortNumbers[@]} != NODES_COUNT )); then
		echo "Please provide the same number of nodes and node port numbers: NODES_COUNT=${NODES_COUNT}, NODE_PORT_NUMBERS=${NODE_PORT_NUMBERS}"
		exit 1
	fi

	local BALANCER_MEMBERS=""
	for (( nodeNumber = 0; nodeNumber < NODES_COUNT; nodeNumber++ )); do
		BALANCER_MEMBERS="${BALANCER_MEMBERS}\n    $(printf 'BalancerMember http://%s:%s route=xt%s min=1 max=50 connectiontimeout=1000ms keepalive=on retry=0 timeout=60 ttl=30' \
"${nodeHostnames[nodeNumber]}" "${nodePortNumbers[nodeNumber]}" "${nodeNumber}")"
	done

	sed -i.bak "{
		s#PLACEHOLDER_SERVER_NAME#${VHOST_CORNER_HOSTNAME}#g
		s#PLACEHOLDER_BALANCER_MEMBERS#${BALANCER_MEMBERS}#g
	}" /usr/local/apache2/conf/vhosts.d/vhost_corner.conf

	sed -i.bak "{
		s#PLACEHOLDER_SERVER_HOSTNAME#${HOSTNAME}#g
	}" /usr/local/apache2/conf/httpd.conf
}

cd ~ || exit 13
if [ ! -f ".initialized" ]; then
	echo "Initializing..."

	configureBalancedMembers

	cd - || exit 13
	touch ~/.initialized
else
	echo "Resuming..."
fi

exec "$@"