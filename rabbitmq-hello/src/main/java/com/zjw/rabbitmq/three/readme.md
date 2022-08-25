消息手动应答
```java
//手动应答 autoAck=false
channel.basicConsume(QueueUtils.HELLO_QUEUE, false, deliverCallback, cancelCallback);
```
不公平分发
```java
//设置不公平分发
channel.basicQos(1);
```
