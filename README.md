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

使用`listContainersCmd()`获得的Container对象下获得的name前包含一个斜杠`/`

因此在遍历判断容器名之前应该加上左斜杠



## ContainerStarter

**runContainer**

创建容器，需要传入uuid，作为容器名，以及作为路径的依据



## Application.properties

包含：

- **文件上传路径配置**
- **odm输出位置**



## ContainerController

**/repoinfo**

使用json格式返回用户上传文件后，容器以及uuid的信息



**/startstop**

控制停止和启动

