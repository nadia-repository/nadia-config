### 更新日期 2021-02-11
1. 功能升级
   * 客户端支持多Application，提升配管理的灵活性，解决现有冗余配置的问题；
   * 升级客户端在服务端Metadata中注册的信息，提升客户端支持多Application的可读性；
   * 优化服务端剔除超时客户端的逻辑；
   * 放开服务端配置项大小的限制；
    
2. bug修改
    * 修改redis线程池数量，解决获取连接失败问题；
    * 修复服务端删除缓存时，远程缓存删除失败问题；
    * 修复配置变更后，客户端回调方法报错问题；
    * 修复客户端维持信条时，由于异常导致的心跳失败问题；
    * 修复@NadiaConfig注解中，排除功能导致的异常问题；
    * 修复服务端配置项不能为空的问题；
    * 修复客户端上报日志后，服务端无法正常消费的问题；