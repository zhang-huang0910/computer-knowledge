#!/bin/bash
case $1 in
"start"){
	for i in hadoop000 hadoop001 hadoop002
	do
	         echo ---------- kafka $i start  --------------
 		 ssh $i  "/opt/module/kafka/bin/kafka-server-start.sh -daemon /opt/module/kafka/config/server.properties"
	done
}
;;
"stop"){
 	for i in hadoop000 hadoop001 hadoop002
        do
                 echo ---------- kafka $i stop  --------------
                 ssh $i  "/opt/module/kafka/bin/kafka-server-stop.sh"
        done
}
;;
esac
