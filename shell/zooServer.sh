#!/bin/bash
case $1 in
"start"){
	for i in hadoop000 hadoop001 hadoop002
	do
	         echo ---------- zookeeper $i start  --------------
 		 ssh $i  "/opt/module/zookeeper-3.5.8/bin/zkServer.sh start"
	done
}
;;
"stop"){
	for i in hadoop000 hadoop001 hadoop002
	do
		echo ---------- zookeeper $i stop  --------------
		ssh $i  "/opt/module/zookeeper-3.5.8/bin/zkServer.sh stop"
	done
}
;;
"status"){
	for i in hadoop000 hadoop001 hadoop002
	do
		echo ----------zookeeper $i status --------------
		ssh $i  "/opt/module/zookeeper-3.5.8/bin/zkServer.sh status"
	done
}
;;
esac
