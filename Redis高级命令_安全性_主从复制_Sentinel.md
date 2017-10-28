
## Redis高级命令及特性 ##

info
	#查看redis服务器的信息
	info #查看完整信息
	info Keyspace #查看keyspace部分的信息
	info Server #查看服务器信息
	info Clients #查看连接的客户端信息

dbsize 查看数据库的key数量

config get 实时传输收到的请求（返回相关的配置信息）

	config get dbfilename 得到数据库的RDB文件名
	config get * 返回所有配置

flushdb 清空当前数据库
flushall 清空所有数据库

## Redis的安全 ##

因为redis速度相当快，所以在一台比较好的服务器下，一个外部用户在一秒内可以进行15W次的密码尝试，这意味着你需要设置非常强大的密码来防止暴力破解。

vi编辑redis.conf文件，找到下面进行保存修改

	################################## SECURITY ###################################
	
	# Require clients to issue AUTH <PASSWORD> before processing any other
	# commands.  This might be useful in environments in which you do not trust
	# others with access to the host running redis-server.
	#
	# This should stay commented out for backward compatibility and because most
	# people do not need auth (e.g. they run their own servers).
	#
	# Warning: since Redis is pretty fast an outside user can try up to
	# 150k passwords per second against a good box. This means that you should
	# use a very strong password otherwise it will be very easy to break.
	#
	# requirepass foobared
	requirepass myP@ssword

重启服务器 pkill redis-server

	[root@mini bin]# vi ../etc/redis.conf 
	[root@mini bin]# netstat -nltp | grep 6379
	tcp        0      0 0.0.0.0:6379                0.0.0.0:*                   LISTEN      1628/./redis-server 
	tcp        0      0 :::6379                     :::*                        LISTEN      1628/./redis-server 
	[root@mini bin]# pkill redis-server
	[root@mini bin]# netstat -nltp | grep 6379
	[root@mini bin]# pwd
	/usr/local/share/redis/bin
	[root@mini bin]# ./redis-server /usr/local/share/redis/etc/redis.conf 
	[root@mini bin]# ./redis-cli 
	127.0.0.1:6379> keys *
	(error) NOAUTH Authentication required.
	#使用auth <password>
	127.0.0.1:6379> auth my_password
	OK

	#还有一种方式直接登录授权
	redis-cli -a my_password

## 主从复制 ##

主从复制
1. Master可以拥有多个slave
2. 多个slave可以连接同一个master外，还可以连接其他slave
3. 主从复制不会阻塞master：在同步数据时，master还可以继续处理client请求
4. 提供系统的伸缩性

主从复制过程：
1. slave与master建立连接，发送sync同步命令
2. master开启一个后台进程，将数据库快照保存到文件中，同时master主进程会开始收集新的写命令并缓存
3. 后台完成保存后，就将文件发送给slave
4. slave将此文件保存到硬盘上

主从复制配置：
1. clone服务器之后修改slave的IP地址
2. 修改配置文件：/usr/local/redis/etc/redis.conf
	1. slaveof <master_ip> <master_port>
	2. masterauth <master_password> #如果master没有设置密码，可以省略此操作

使用info查看role角色，即可知主服务或从服务。

## 哨兵 ##

有了主从复制的实现以后，我们如果想对主从服务器进行监控，那在redis2.6以后提供了一个“哨兵”的机制，在2.6版本中的哨兵为1.0版本，并不稳定，会出现各种各样的问题。在2.8以后版本哨兵功能才稳定起来。

顾名思义，哨兵的含义就是监控redis系统的运行状态。其主要功能有两点：
1. 监控主数据库和从数据库是否正常运行
2. 主数据库出现故障时，可以自动将从数据库转换为主数据库，实现自动切换。

实现步骤：在其中一台从服务器配置sentinel.conf
1. copy文件sentinel.conf到/usr/local/redis/etc/中
2. 修改sentinel.conf文件

	sentinel monitor 名称 IP 端口 投票选举次数
	sentinel monitor <master_name> <ip> <port> <n>
	默认1秒检查一次，这里配置超时5000毫秒为宕机
	sentinel down-after-milliseconds <master_name> 5000
	sentinel failover-timeout <master_name> 900000
	sentinel parallel-syncs <master_name> 2
	sentinel can-failover <master_name> yes



3. 启动sentinel哨兵

	/usr/local/redis/bin/redis-server /usr/local/redis/etc/sentinel.conf --sentinel &

4. 查看哨兵相关信息的命令

	/usr/local/redis/bin/redis-cli -h 192.168.1.175 -p 26379 info Sentinel

5. 关闭主服务器查看集群信息

	/usr/local/redis/bin/redis-cli -h 192.168.1.174 -p 6379 shutdown


### sentinel.conf示例 ###

	# port <sentinel-port>
	# The port that this sentinel instance will run on
	port 26379
	
	# dir <working-directory>
	# Every long running process should have a well-defined working directory.
	# For Redis Sentinel to chdir to /tmp at startup is the simplest thing
	# for the process to don't interfere with administrative tasks such as
	# unmounting filesystems.
	dir /usr/local/redis/etc/
	
	# sentinel monitor <master-name> <ip> <redis-port> <quorum>
	#
	# Tells Sentinel to monitor this master, and to consider it in O_DOWN
	# (Objectively Down) state only if at least <quorum> sentinels agree.
	#
	# Note that whatever is the ODOWN quorum, a Sentinel will require to
	# be elected by the majority of the known Sentinels in order to
	# start a failover, so no failover can be performed in minority.
	#
	# Slaves are auto-discovered, so you don't need to specify slaves in
	# any way. Sentinel itself will rewrite this configuration file adding
	# the slaves using additional configuration options.
	# Also note that the configuration file is rewritten when a
	# slave is promoted to master.
	#
	# Note: master name should not include special characters or spaces.
	# The valid charset is A-z 0-9 and the three characters ".-_".
	sentinel monitor mymaster 127.0.0.1 6379 2
	
	# sentinel down-after-milliseconds <master-name> <milliseconds>
	#
	# Number of milliseconds the master (or any attached slave or sentinel) should
	# be unreachable (as in, not acceptable reply to PING, continuously, for the
	# specified period) in order to consider it in S_DOWN state (Subjectively
	# Down).
	#
	# Default is 30 seconds.
	sentinel down-after-milliseconds mymaster 30000
	
	# sentinel parallel-syncs <master-name> <numslaves>
	#
	# How many slaves we can reconfigure to point to the new slave simultaneously
	# during the failover. Use a low number if you use the slaves to serve query
	# to avoid that all the slaves will be unreachable at about the same
	# time while performing the synchronization with the master.
	sentinel parallel-syncs mymaster 1
	
	# sentinel failover-timeout <master-name> <milliseconds>
	#
	# Specifies the failover timeout in milliseconds. It is used in many ways:
	#
	# - The time needed to re-start a failover after a previous failover was
	#   already tried against the same master by a given Sentinel, is two
	#   times the failover timeout.
	#
	# - The time needed for a slave replicating to a wrong master according
	#   to a Sentinel current configuration, to be forced to replicate
	#   with the right master, is exactly the failover timeout (counting since
	#   the moment a Sentinel detected the misconfiguration).
	#
	# - The time needed to cancel a failover that is already in progress but
	#   did not produced any configuration change (SLAVEOF NO ONE yet not
	#   acknowledged by the promoted slave).
	#
	# - The maximum time a failover in progress waits for all the slaves to be
	#   reconfigured as slaves of the new master. However even after this time
	#   the slaves will be reconfigured by the Sentinels anyway, but not with
	#   the exact parallel-syncs progression as specified.
	#
	# Default is 3 minutes.
	sentinel failover-timeout mymaster 180000
