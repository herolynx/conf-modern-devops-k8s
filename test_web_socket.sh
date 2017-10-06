#!/bin/bash

export URL="$1"

if [ -z "${URL}" ]; then
    echo "URL is unset. Run ./test_web_socket.sh <URL>"
    exit 1
fi

echo
echo "**********************"
echo "Checking given address:"
curl -L -k $URL/hello

echo
echo "**********************"
echo "Establishing web-socket connection:"
curl --include \
     --no-buffer \
     --header "Connection: Upgrade" \
     --header "Upgrade: websocket" \
     --header "Host: $URL" \
     --header "Origin: $URL" \
     --header "Sec-WebSocket-Key: SGVsbG8sIHdvcmxkIQ==" \
     --header "Sec-WebSocket-Version: 13" \
     -L -k \
     $URL/web-socket