简单的API任务调度系统
------------
![image](https://raw.githubusercontent.com/lihengming/simple-web-scheduler/master/ui.jpg)

##### 如何使用

创建任务，输入要调用的API地址，输入表达式即可按照表达式([CORN语法](https://www.baidu.com/s?wd=corn%E8%A1%A8%E8%BE%BE%E5%BC%8F&rsv_spt=1&rsv_iqid=0xcc7f668f00002243&issp=1&f=8&rsv_bp=1&rsv_idx=2&ie=utf-8&rqlang=cn&tn=baiduhome_pg&rsv_enter=1&oq=%2526lt%253BORN&rsv_t=cc93cyAGHuQwt0Td0k4lDhdRnm2%2BmFPcFlwDKYfic%2F2OEDcLeDO%2BxVKJDXuEIXibh%2BL6&inputT=3570&rsv_pq=e75fffd200001119&rsv_sug3=63&rsv_sug1=39&rsv_sug7=100&rsv_sug2=0&rsv_sug4=3570))对该API进行调用。
(任务将通过Spring JPA持久化到数据库，表名为```task```，如果你需要更换数据库清修改```application.properties```)


##### 技术选型

Spring Boot + Vue.js + Bootstrap

