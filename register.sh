CID=`docker ps -aqf "name=tango-cs"`
echo $CID
docker exec $CID /usr/local/bin/tango_admin --add-server PreExperimentDataCollector/dev PreExperimentDataCollector dev/xenv/predator