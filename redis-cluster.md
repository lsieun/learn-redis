
## redis集群搭建 ##

在redis 3.0以前，提供了Sentinel工具来监控各Master的状态：如果Master异常，则会做主从切换，将slave作为master，将master作为slave。其配置也是稍微的复杂，并且在各方面表现一般，在发生主从切换的时候，可能还需要1、2秒的时间的，就可能造成写入数据失败。现在redis 3.0已经支持集群的容错功能，并且非常简单。

下面我们来进行学习redis 3.0如何搭建集群。

集群搭建：至少要三个master

第一步：创建一个文件夹redis-cluster，然后在其下分别创建6个文件夹如下：
（1）mkdir -p /usr/local/redis-cluster
（2）mkdir 7001、mkdir 7002、mkdir 7003、mkdir 7004 mkdir 7005 mkdir 7006

	[root@mini ~]# mkdir -p /usr/local/redis-cluster/{7001,7002,7003,7004,7005,7006}
	[root@mini ~]# cd /usr/local/redis-cluster/
	[root@mini redis-cluster]# ls
	7001  7002  7003  7004  7005  7006

第二步：把之前的redis.conf配置文件分别copy到700*下，进行修改各个文件内容，也就是对700*下的每一个copy的redis.conf文件进行修改！如下
	（1）daemonize yes
	（2）port 700* （分别对每个机器的端口号进行设置）
	（3）bind 192.168.80.30（必须绑定当前机器的ip，不然会无限悲剧下去哇。。。深坑勿入！！！）
	（4）dir /usr/local/redis-cluster/700*/（指定数据文件存放位置，必须要指定不同的目录位置，不然会丢失数据，深坑勿入！！！）
	（5）cluster-enabled yes（启动集群模式，开始玩耍）
	（6）cluster-config-file nodes700*.conf（）
	（7）cluster-node-timeout 5000
	（8）appendonly yes

13:49 查看redis的配置 （22）Redis集群搭建 20160215

第三步：把修改后的配置文件，分别copy到各个文件夹下，注意每个文件都要修改端口号，并且nodes文件也要不相同！

第四步：由于redis集群需要使用ruby命令，所以我们需要安装ruby
	(1) yum install ruby
	(2) yum install rubygems
	(3) gem install redis （安装redis和ruby接口）

第五步：分别启动6个redis实例，然后检查是否启动成功
	(1) /usr/local/redis/bin/redis-server /usr/local/redis-cluster/700*/redis.conf
	(2) ps -el | grep redis 查看是否启动成功

第六步：首先到redis 3.0的安装目录下，然后执行redis-trib.rb命令
	(1)cd /usr/local/redis3.0/src
	(2) ./redis-trib.rb create --replicas 1 192.168.1.171:7001 192.168.1.171:7002 192.168.1.171:7003 192.168.1.171:7004 192.168.1.171:7005 192.168.1.171:7006

--replicas 1，其中1表示“主节点”与“从节点”的数量比例为1 

第七步：至此为止我们集群搭建成功！进行验证：
	（1）连接任意一个客户端即可：./redis-cli -c -h -p (-c表示集群模式，指定ip地址和端口号) 如：/usr/local/redis/bin/redis-cli -c -h 192.168.1.171 -p 700*
	（2）进行验证：cluster info（查看集群信息） cluster nodes（查看节点列表）
	（3）进行数据操作验证
	（4）关闭集群，则需要逐个进行关闭，使用命令： /usr/local/redis-cli -c -h 192.168.1.171 -p 700* shutdown

第八步：（补充）
	友情提示：当出现集群无法启动时，删除临时的数据文件，再次重新启动每一个redis服务，然后重新构造集群环境
	
redis-trib.rb官方操作命令：http://redis.io/topics/cluster-tutorial
推荐博客： http://blog.51yip.com/nosql/172.html/comment-page-1


