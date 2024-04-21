#!/bin/bash

HOSTS=(
     druid-etl-perftune-1.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-2.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-4.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-10.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-11.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-12.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-13.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-14.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-15.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-16.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-17.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-18.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-19.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-20.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-21.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-22.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-23.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-24.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-25.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-26.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-28.us-east4.prod.gcp.conviva.com
     druid-etl-perftune-29.us-east4.prod.gcp.conviva.com
)

private_key=/Users/rwei/Documents/conviva_cm_private_key.pem
#private_key=conviva_cm_private_key.pem
jar_path=/Users/rwei/IdeaProjects/storage-etl/assembly-jars/session-summary-converter_1.8.37-SNAPSHOT.jar
#jar_path=./session-summary-converter_1.8.37-SNAPSHOT.jar
jar_file=session-summary-converter_1.8.37-SNAPSHOT.jar
#layout_path=/Users/rwei/.kube/druidLayoutV1_1.8.37-SNAPSHOT.json
#layout_file=druidLayoutV1_1.8.37-SNAPSHOT.json

for HOST in "${HOSTS[@]}"; do
	#ssh -i ${private_key} -o StrictHostKeyChecking=no -tq "root@$HOST" "mkdir -p /conviva/data/log"
	scp -i ${private_key} ${jar_path} root@${HOST}:/conviva/data/log/${jar_file}
#	scp -i ${private_key} ${layout_path} root@${HOST}:/usr/local/conviva/hourly_etl/conf/${layout_file}
	#ssh -i ${private_key} -o StrictHostKeyChecking=no -tq "root@$HOST" "cp /conviva/data/log/session-summary-converter_1.8.3.jar /conviva/data/log/session-summary-converter_1.8.3-fix.jar"
done