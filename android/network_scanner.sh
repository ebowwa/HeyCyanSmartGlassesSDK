#!/bin/bash
# Network scanner to find media server on glasses
# Run this while connected to glasses WiFi with official app

GLASSES_IP="192.168.3.31"

echo "Scanning common ports on $GLASSES_IP..."

# Common web/media server ports
PORTS="80 8080 8000 8001 8888 9000 3000 5000 554 1935 8554"

for PORT in $PORTS; do
    echo -n "Checking port $PORT... "
    nc -zv -w 1 $GLASSES_IP $PORT 2>&1 | grep -q succeeded
    if [ $? -eq 0 ]; then
        echo "OPEN"
        # Try HTTP request
        curl -s -m 2 "http://$GLASSES_IP:$PORT/" > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo "  -> HTTP server responding!"
            curl -s -m 2 "http://$GLASSES_IP:$PORT/" | head -20
        fi
    else
        echo "closed"
    fi
done

echo -e "\nTrying common media endpoints..."
ENDPOINTS="/ /media /files /dcim /DCIM /api/media /api/files /list /download"

for ENDPOINT in $ENDPOINTS; do
    for PORT in 80 8080; do
        URL="http://$GLASSES_IP:$PORT$ENDPOINT"
        echo -n "Testing $URL... "
        RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" -m 2 "$URL")
        if [ "$RESPONSE" != "000" ]; then
            echo "HTTP $RESPONSE"
            if [ "$RESPONSE" == "200" ]; then
                echo "  -> Success! Found endpoint: $URL"
                curl -s "$URL" | head -20
            fi
        else
            echo "timeout"
        fi
    done
done