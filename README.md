# Nadia Config Center

## 更新日志
- [changelog](changelog/README.md)

## 组件介绍
- [nadia-client](nadia-client/README.md)
- [nadia-server](nadia-server/README.md)

## Release-Note
- v1.0

## 主要目标：

- 兼容springboot框架，业务系统无需代码变更
- 快速接入，jar包引用
- 配置动态更新，实时生效
- 支持集群分组，配置差异化
- 提供配置统一变更平台，支持平台用户的权限管理和配置变更的权限管理

#### 部署图
![avatar](docs/pic/服务部署图.png)

#### 项目构成
> nadia-config 项目根目录

>> nadia-client 客户端

>> nadia-core 通用代码

>> nadia-server 服务端

>> nadia-web 前端页面

>> nadia-demo 客户端demo

>> docs 文档

>>> desgin 设计文档

>>> sql 服务端脚本

>>>> DDL.sql 建表

>>>> DML.sql 初始数据


#### 管理平台介绍

#### 1. 登录页面
![avatar](docs/pic/登录.png)

#### 2. 注册页面
![avatar](docs/pic/注册.png)

#### 3. 主界面
###### 根据登录用户的角色不同，菜单按钮会有相应变化
![avatar](docs/pic/主页面.png)

#### 4. 配置管理--元数据管理
###### 元数据管理是所有配置的基础，客户端使用配置项时需要指定相关的元数据
![avatar](docs/pic/metadata.png)

###### 名词解释
* Application 系统名称，客户端可以指定使用任意一套系统下的配置。
* Group 组名称，客户端指定系统后，仍需要指定使用系统下的某个组。若不强制指定，客户端启动后会默认使用Default组下的配置，后期可使用组切换功能切换至不同组。

###### 功能说明
* Add 新增Application，同时新增默认Group(Default)。
* Delete 删除Application\Group信息。当前Group如果正在被使用，或当前组为Default组且存在非Default组时，不能删除。Defualt组删除后将同时删除Application。
* Compare 比较同一Application下不同Group的配置差异，可多选，但至少选两个组。
![avatar](docs/pic/compare.png)
* Edit 修改Application信息
* Group 新增组。可选择从某个组复制所有已发布配置项，或生产新的空组。
![avatar](docs/pic/addGroup.png)
* Instances 可将某实例调整至另一组。
![avatar](docs/pic/switch.png)
* Inscance 查看弄实例当前的配置与服务端配置的差异。
![avatar](docs/pic/instance.png)

#### 5. 配置管理--配置项管理
###### 配置项管理提供具体配置的增删改查、发布等功能
![avatar](docs/pic/configs.png)
###### 名词解释
* Key 配置项的key，与客户端中@Value({key})相对应
* Value 值
* Status 
    * new 新建配置
    * edited 配置已变更，带审批
    * approved 配置审批通过，可发布
    * published 配置已发布
    * removing 配置删除，待审批
    * deleted 配置已删除，不显示
    * invalid 配置无效，不显示
    
###### 功能说明
* Add 新增配置，可选择配置的可见权限。若不选择，则该配置为当前登录用户的角色可见。需要审批。
* Delete 删除配置，需要审批。
* Export 导出配置。
* Import 导入配置，需要审批。
* Edit 编辑配置，需要审批。
* Instance 查看当前配置的使用情况。
![avatar](docs/pic/instanceConfig.png)
* History 配置修改历史
![avatar](docs/pic/history.png)

#### 6. 配置管理--配置项分配
###### 配置项分配提供全局的角色配置可见权限设置。
![avatar](docs/pic/allocate.png)
###### 功能说明
* Allocate 分配角色可见配置
![avatar](docs/pic/allocateConfigs.png)

#### 7. 消息中心--操作日志
###### 提供管理平台所有增删改的操作日志查询和回溯。
![avatar](docs/pic/operationLog.png)

#### 8. 消息中心--任务中心
###### 提供审批查看、审批操作(通过、拒绝)等功能。
* Pending Task 待审批列表，提供审批通过、决绝，审配内容查看等功能。
![avatar](docs/pic/pendingTask.png)
* Processing Task 审批进度列表，提供审批进度的查询。
![avatar](docs/pic/processingTask.png)
* Complete Task 完成任务列表，提供历史审批通过、拒绝的任务的查询。
![avatar](docs/pic/completeTask.png)

#### 9. 消息中心--操作日志
###### 提供客户端上报日志的查询。
![avatar](docs/pic/clientLog.png)

#### 10. 用户中心--角色管理
###### 提供角色的增删改查，角色的菜单权限、按钮权限的设置。
![avatar](docs/pic/role.png)

#### 11. 用户中心--用户管理
###### 提供用户的增删改查，用户的角色分配设置。
![avatar](docs/pic/user.png)
