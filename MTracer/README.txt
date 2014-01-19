MTracer is a white box-based trace-oriented monitoring platform, which is lightweight and efficiency. MTracer adopts the client-server framework. The data are stored in database on the monitor server, where a paralell strategy and several optimizations are introduced to speed up the storing process. A web-based frontend for visualizing, called MTracer-Viz, is also provided, and some advanced functions are integrated in this frontend to do deeper data analyses.  More details of MTracer can be referred to corresponding paper "MTracer: a trace-oriented monitoring framework for medium-scale distributed systems".

We developed MTracer based on X-Trace [1], so many code files in this project are name with a "xtrace" inside. Data are stored in MySQL. The frontend is implemented with JSP and using GraphViz for visualization.

In the rest, we use a demo to show the process of deploying MTracer and MTrace-Viz.
The machines we used are 3 VMs hosted on CloudStack with the OS of CentOS 6.3 64bit:
	MonitorServer: the monitor server on which the MTracer and MTracer-Viz are deployed.
	MonitorClient_1 and MonitorClient_2: the monitored nodes for deploying the chatting demo.

0. Prepare
	a. Upload MTracer and MTracer-Viz to MonitorServer:/root; upload MTracer to MonitorClient_1:/root and MonitorClient_2:/root;
	b. Shutdown the firewall on all 3 nodes
		service iptables stop
	c. Install JDK on all 3 nodes, we installed with jdk-7u11-linux-x64.tar.gz and JAVA_HOME=/usr/lib/jvm/jdk1.7.0_11.

1. MTracer
	a. Install MySQL and set user "root" with a password "root"
		yum install mysql-server
		service mysqld start
		mysqladmin -u root password root
	b. Set the monitor server address
		vi MTracer/backend.sh
		and set UDPSOURCE=MonitorServer:7831 at about line 3
	
2. MTracer-Viz
	a. Install GraphViz
		yum install graphviz
	b. Set JAVA_HOME
		vi MTracer-Viz/apache-tomcat-6.0.35/bin/catalina.sh
		and set JAVA_HOME=/usr/lib/jvm/jdk1.7.0_11 at about line 86
	c. Set the directory of web
		vi /apache-tomcat-6.0.35/conf/server.xml
		and set <Context path="" docBase="/root/MTracer-Viz/webapps" debug="5" reloadable="true" crossContext="true" /> at about line 144
	d. Add flanagan.jar for supporting the diagnosis function
		cp MTracer-Viz/webapps/WEB-INF/lib/flanagan.jar $JAVA_HOME/jre/lib/ext/

3. Test
	a. Start MTracer
		sh MTracer/backend.sh &
	b. Start MTracer-Viz
		sh MTracer-Viz/apache-tomcat-6.0.35/bin/startup.sh
	c. Start demo server on MonitorClient_1
		java -Dxtrace.udpdest=MonitorServer:7831 -cp /root/MTracer/bin/ chatapp.Server
		where xtrace.udpdest is the address of the monitor server
	d. Start demo client on MonitorClient_2
		client2£ºjava -Dxtrace.udpdest=MonitorServer:7831 -DserverAddress=MonitorClient_1 -cp /root/MTracer/bin/ chatapp.Client
		where serverAddress is the address of server in this chatting demo.
	e. input some sentences on MonitorClient_2, where a sentence with word "bye" inside will terminal the demo.
	f. Scan the collected traces and related information in local browser with a url of "http://MonitorServer:8080/"

* Contact "jwzhou at nudt dot edu dot cn" if any problem about MTracer.


[1] R. Fonseca, G. Porter, R. H. Katz, S. Shenker, and I. Stoica, ¡°X-Trace: A pervasive network tracing framework,¡± in Proc. of USENIX NSDI, 2007, pp. 271¨C284.