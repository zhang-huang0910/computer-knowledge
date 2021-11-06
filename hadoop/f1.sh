#!/bin/bash
case $1 in
  start)
     for i in hadoop102 hadoop103;do
	   echo "=================$i================"
	   ssh $i "nohup /opt/module/flume/bin/flume-ng agent -n aq -c /opt/module/flume/conf/ -f /opt/module/flume/jobs/taildir_kafka.conf >/dev/null 2>&1 & "
	   done
;;
  stop)
	for i in hadoop102 hadoop103;do
	echo "=================$i================"
	ssh $i "ps -ef | awk '/taildir_kafka.conf/ && !/awk/{print \$2}'| xargs kill -9 "
	done
;;
esac
