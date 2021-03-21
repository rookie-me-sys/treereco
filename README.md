# 基于无人机影像的树木信息提取和三维显示技术

## 技术介绍

### ODM

ODM是基于Linux平台的用于处理空中无人机图像的开源工具包，用于处理原始UAV图像到**点云**、**数字表面模型**、**纹理数字表面模型**、**正射影像**、**分类点云**、**数字高程模型**等数据，其成果数据包含真是地理坐标。

### Cesium

Cesium是一个用于开发三维WebGIS客户端的开源JavaScript开发包。



### SpringBoot



### Mybatis



## 设计流程

![image-20210317143059861](F:\IntellJWorkspace\version-01\README.assets\image-20210317143059861.png)

## 数据库

数据库中表设计如下：

- users	用户表
	- id
	- nickname
	- password
	- createtime
- treerepo	用户所上传UAV图像的保存位置
	- userid
	- uuid
- odmcontainer	用户所属的odm处理进程信息
	- containerName
	- containerId
	- status
- etc.



## Mybatis+MySql

### 链接

`jdbc:mysql://localhost:3306/treereco?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC`

### 功能

**users**

- 获得所有用户的信息
- **根据userid获得用户**
- **根据nickname获得用户**
- 向users表添加新用户
- 更改用户密码
- 获取最大id

**odmcontainer**

- 查询所有容器
- 根据用户id查询容器
- 根据用户nickname查询容器    [joint query]
- 根据容器id查询所属用户    [admin]
- 根据容器名查询所属用户
- **插入容器**（上传完成后容器名为uuid名）
- **修改容器状态**

**treerepo**

- 根据用户查询所有uuid
- **根据uuid查询用户**
- **添加一条数据**（上传完成后将uuid）



## 文件上传

### 组件

文件上传使用以下组件：

- commons-io
- commons-fileupload

### 功能说明

为了增强文件上传的实用性，创建一个FileuploadUtils工具类。其中包含upload核心方法。

该方法内部产生一个uuid，并在上传完成之后返回该uuid用于写入数据库。

uuid也作为文件上传的位置，如上传根目录为`/user/data`则图片保存在`/user/data/[uuid]`

同理，odm产生的obj文件等生成在输出根目录的`uuid`次级目录下。

### 流程示意

![image-20210317142939283](F:\IntellJWorkspace\version-01\README.assets\image-20210317142939283.png)

### 异常处理

若上传的请求中没有图片文件，uuid返回“null”，可用于在Process执行docker指令之前判断是否执行该指令。



## Docker控制

### 组件

docker控制使用以下组件：

- docker-java

### 功能说明

**getContainerIdByName**

由于使用Process创建的docker容器不能直接得到容器id，因此使用docker-java通过传入自定义的容器名获得ContainerId，如果找不到容器则返回空字符串。

此功能核心函数为docker API下的`listContainersCmd()`

**stopContainer**

只能单纯停止容器的运行，会导致停止后获得不到容器而时区控制的权限

但是对于带有`--rm`的odm命令，一旦stop就意味着remove

**removeCotainer**

该函数不推荐使用，只能用于移除容器。备用。

**stopAndRemoveContainer**

推荐用于不带`--rm`的容器

**removeContainerById**

根据容器id删除容器（很好用）：）



### 注意

- 使用`listContainersCmd()`获得的Container对象下获得的name前包含一个斜杠`/`

因此在遍历判断容器名之前应该加上左斜杠

- 由于docker容器操作的滞后性，所有停止类的容器操作最后需要加上判断是否已经停止的判断



## ContainerStarter

**runContainer**

创建容器，需要传入uuid，作为容器名，以及作为路径的依据

在执行`runtime.exec(exec)`后，需要循环调用`getContainerIdByName`直到能够取到返回的containerId为止，说明已经成功创建

```java
while(docker.getContainerIdByName(uuid).equals("")){
    Thread.sleep(1000);
    System.out.println("wait");
}
```





## Application.properties

包含：

- **文件上传路径配置**
- **odm输出位置**



## ContainerController

**/repoinfo**

构建一个内部类ContainerInfo，用于填入该用户下每一个容器的信息，包含：

- 容器uuid（名）
- 用户id
- 容器ID
- 容器状态

容器的id通过从docker中获取和mysql中获取的对比来决定最终id值

由于docker容器停止后，docker-java将获取不到该容器的信息，因此不仅需要从docker中获取id也要在mysql中获取历史id，若在mysql中查询不到id，则说明容器从来都没有被创建过，id赋值为从docker中获取的id。

若在mysql和docker中都存在id，则分为两种情况：

- 容器停止了，docker中id为空，则id赋值为mysql中的历史id
- 容器正在运行，取docker中id



**/startstop**

控制停止和启动

通过判断是否能够获取到容器状态来判断是否存在该运行中容器

在执行启动操作之前，为保证一组UAV数据只对应一个容器，先判断数据库中是否存在该名的容器，若有则删除该容器：

- 删除docker下容器
- 删除数据库容器

停止





### odm容器状态：

1. ""=未创建
2. 运行
3. 停止，在数据库中存在id但是status返回空