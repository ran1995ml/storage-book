#!/bin/bash
my_array=(
rccp201-22b.iad5.prod.conviva.com
rccp201-22c.iad5.prod.conviva.com
rccp201-23c.iad5.prod.conviva.com
rccp201-23d.iad5.prod.conviva.com
rccp202-22c.iad5.prod.conviva.com
rccp202-22d.iad5.prod.conviva.com
rccp203-22b.iad5.prod.conviva.com
rccp203-22c.iad5.prod.conviva.com
rccp204-22a.iad5.prod.conviva.com
)
command="echo '############## df ########################';"
command+='df -h /;'
command+="echo '############## memory #################################';"
command+='free -g;'
command+="echo '############## cpu ####################################';"
command+='cat /proc/cpuinfo | grep processor;'
command+="echo '############## version ################################';"
command+='lsb_release -a;'
command+="echo '############## telnet #################################';"
command+="echo -e '\x1dclose\x0d' | telnet cc.imply.io 443;"
command+="echo '############## finish ################################';"
for i in "${my_array[@]}"
do
        echo -e "\nstart check server:$i"
    ssh -i /Users/rwei/Documents/conviva_cm_private_key.pem root@$i $command
    echo -e "end check server:$i \n"
done