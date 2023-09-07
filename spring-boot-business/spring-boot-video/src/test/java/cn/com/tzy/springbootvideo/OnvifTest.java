package cn.com.tzy.springbootvideo;

import be.teletask.onvif.DiscoveryManager;
import be.teletask.onvif.OnvifManager;
import be.teletask.onvif.listeners.*;
import be.teletask.onvif.models.*;
import be.teletask.onvif.responses.OnvifResponse;
import cn.hutool.json.JSONUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取局域网内的Onvif 设备信息
 */
public class OnvifTest {

    public static void main(String[] args) throws InterruptedException {
        List<OnvifDevice> deviceList = new ArrayList<>();
        //发现设备
        findDevice(deviceList);
    }


    private static void findDevice(List<OnvifDevice> deviceList){
        // 发现ONVIF设备实例
        DiscoveryManager discoveryManager = new DiscoveryManager();
        discoveryManager.discover(new DiscoveryListener() {
            @Override
            public void onDiscoveryStarted() {
                System.out.println("Device discovery started.");
            }
            @Override
            public void onDevicesFound(List<Device> devices) {
                for (Device device1 : devices) {
                    OnvifDevice device = (OnvifDevice) device1;
                    System.out.println("Device found: " + device.getHostName());
                    System.out.println("Device found: " + device.getType());
                    System.out.println("Device found: " + device.getUsername());
                    System.out.println("Device found: " + device.getPassword());
                    System.out.println("Device found: " + device.getPath());
                    System.out.println("Device found: " + JSONUtil.toJsonStr(device.getAddresses()));
                    deviceList.add(device);
                }
                try {
                    operateDevice(deviceList);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void operateDevice(List<OnvifDevice> deviceList) throws InterruptedException {
        OnvifManager executor = new OnvifManager();
        executor.setOnvifResponseListener(new OnvifResponseListener() {
            @Override
            public void onResponse(OnvifDevice onvifDevice, OnvifResponse response) {
                System.out.println("Device onResponse: " + onvifDevice.getHostName());
                System.out.println("Device OnvifResponse: " + JSONUtil.toJsonStr(response));
            }

            @Override
            public void onError(OnvifDevice onvifDevice, int errorCode, String errorMessage) {
                System.out.println("Device onError: " + onvifDevice.getHostName());
                System.out.println("Device OnvifDeviceInformation: "+errorMessage+":errorMessage");
            }
        });
        for (OnvifDevice onvifDevice : deviceList) {
            onvifDevice.setUsername("admin");
            onvifDevice.setPassword("admin");
            //获取设备服务请求信息
            executor.getServices(onvifDevice, new OnvifServicesListener() {
                @Override
                public void onServicesReceived(OnvifDevice onvifDevice, OnvifServices paths) {
                    System.out.println("Device onServicesReceived: " + onvifDevice.getHostName());
                    System.out.println("Device OnvifServices: " + JSONUtil.toJsonStr(paths));
                }
            });
            //获取设备基本信息
            executor.getDeviceInformation(onvifDevice, new OnvifDeviceInformationListener() {
                @Override
                public void onDeviceInformationReceived(OnvifDevice device, OnvifDeviceInformation deviceInformation) {
                    System.out.println("Device onDeviceInformationReceived: " + device.getHostName());
                    System.out.println("Device OnvifDeviceInformation: " + JSONUtil.toJsonStr(deviceInformation));
                }
            });
            //获取设备配置文件
            executor.getMediaProfiles(onvifDevice, new OnvifMediaProfilesListener() {
                @Override
                public void onMediaProfilesReceived(OnvifDevice device, List<OnvifMediaProfile> mediaProfiles) {
                    System.out.println("Device onMediaProfilesReceived: " + device.getHostName());
                    System.out.println("Device OnvifMediaProfile: " + JSONUtil.toJsonStr(mediaProfiles));
                    executor.getMediaStreamURI(onvifDevice, mediaProfiles.get(0), new OnvifMediaStreamURIListener() {
                        //获取设备流媒体信息
                        @Override
                        public void onMediaStreamURIReceived(OnvifDevice device, OnvifMediaProfile profile, String uri) {
                            System.out.println("Device onMediaStreamURIReceived: " + device.getHostName());
                            System.out.println("Device OnvifMediaProfile: " + JSONUtil.toJsonStr(profile));
                            System.out.println("Device uri: " + uri);
                        }
                    });
                }
            });
            executor.destroy();
        }
        Thread.sleep(60000);
    }
}
