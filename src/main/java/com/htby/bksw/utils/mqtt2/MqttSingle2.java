package com.htby.bksw.utils.mqtt2;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * 单例模式
 * @author 周西栋
 * @date
 * @param
 * @return
 */
@Slf4j
public class MqttSingle2 {

    // 连接参数
    private AutoMqttProperties2 autoMqttProperties = new AutoMqttProperties2();

    // mqtt连接
    private MqttClient mqttClient;

    /************************************************************************************* 单例 **************************************************************************************/
    private static MqttSingle2 mqttSingle = new MqttSingle2();

    private MqttSingle2(){
        mqttClient = getClient();
    }

    public static MqttSingle2 getInstance(){
        return mqttSingle;
    }
    /************************************************************************************* 单例 **************************************************************************************/

    // 创建连接
    private MqttClient getClient(){
        MqttClient client = null;
        try {
            // host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(autoMqttProperties.getHost(),
                    autoMqttProperties.getClientid()+"_"+System.currentTimeMillis(),
                    new MemoryPersistence());
        } catch (MqttException e) {
            log.error("与mqtt代理连接失败！");
            log.error("异常信息为：{}",e.getMessage());
        }
        // MQTT的连接设置
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(false);
        // 设置连接的用户名
        options.setUserName(autoMqttProperties.getUsername());
        // 设置连接的密码
        options.setPassword(autoMqttProperties.getPassword().toCharArray());
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(autoMqttProperties.getTimeout());
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(autoMqttProperties.getKeepalive());
        //设置断开后重新连接
        options.setAutomaticReconnect(true);
        try {
            // 回调
            client.setCallback(new PushCallback2());
            // 连接
            client.connect(options);
        } catch (MqttException e) {
            log.error("创建连接失败！");
            log.error("异常信息为：{}",e.getMessage());
        }
        log.info("我创建了新的连接(2)，连接信息是：{}",client.toString());
        return client;
    }

    // 重连
    public MqttClient reLink(){
        getClient();
        return mqttClient;
    };

    // 体外提供连接
    public MqttClient getMqttClient(){
        return mqttClient;
    }
}
