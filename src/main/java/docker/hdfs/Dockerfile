# Creates pseudo distributed hadoop 3.1.2
#
# docker build -t effeerre/hadoop .

FROM ubuntu
USER root

# install dev tools
RUN apt-get update
RUN apt-get install -y curl tar sudo openssh-server rsync openjdk-8-jre vim net-tools nano

RUN ssh-keygen -q -N "" -t rsa -f /root/.ssh/id_rsa 
RUN cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

# # java
ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd64
ENV PATH $PATH:$JAVA_HOME/bin

# # hadoop
COPY ./hadoop-3.1.2.tar.gz . 
RUN tar -zxf hadoop-3.1.2.tar.gz -C /usr/local/ ; rm hadoop-3.1.2.tar.gz
RUN cd /usr/local && ln -s ./hadoop-3.1.2 hadoop

# definizione di variabili d ambiente, variabili globali nel so.
ENV HADOOP_COMMON_HOME /usr/local/hadoop
ENV HADOOP_HDFS_HOME /usr/local/hadoop
ENV HADOOP_HOME /usr/local/hadoop
ENV HADOOP_MAPRED_HOME /usr/local/hadoop
ENV HADOOP_YARN_HOME /usr/local/hadoop
ENV HADOOP_CONF_DIR /usr/local/hadoop/etc/hadoop
ENV YARN_CONF_DIR $HADOOP_HOME/etc/hadoop
ENV PATH $PATH:$HADOOP_HOME/bin
ENV HDFS_NAMENODE_USER "root"
ENV HDFS_DATANODE_USER "root"
ENV HDFS_SECONDARYNAMENODE_USER "root"
ENV YARN_RESOURCEMANAGER_USER "root"
ENV YARN_NODEMANAGER_USER "root"


ADD config/hadoop-env.sh $HADOOP_HOME/etc/hadoop/hadoop-env.sh
ADD config/core-site.xml $HADOOP_HOME/etc/hadoop/core-site.xml
ADD config/hdfs-site.xml $HADOOP_HOME/etc/hadoop/hdfs-site.xml
ADD config/workers $HADOOP_HOME/etc/hadoop/workers

# riscrivo il file hadoop_end.sh con la giusta variavile d ambiente java
RUN rm $HADOOP_HOME/etc/hadoop/hadoop-env.sh
RUN cd $HADOOP_HOME/etc/hadoop echo; echo "export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/" >>hadoop-env.sh



# 
ADD config/ssh_config /root/.ssh/config
RUN chmod 600 /root/.ssh/config
RUN chown root:root /root/.ssh/config
# 
ADD config/bootstrap.sh /usr/local/bootstrap.sh
RUN chown root:root /usr/local/bootstrap.sh
RUN chmod 700 /usr/local/bootstrap.sh
# 
ENV BOOTSTRAP /usr/local/bootstrap.sh
# 
CMD /usr/local/bootstrap.sh

# # Hdfs ports
EXPOSE 9866 9867 9870 9864 9868 9820 9000
# # Mapred ports
# EXPOSE 10020 19888
# #Yarn ports
# EXPOSE 8030 8031 8032 8033 8040 8042 8088
# #Other ports
# EXPOSE 49707 2122
#
# SSH
EXPOSE 22
