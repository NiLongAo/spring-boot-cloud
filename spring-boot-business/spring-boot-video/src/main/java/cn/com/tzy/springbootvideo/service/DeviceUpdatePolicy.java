package cn.com.tzy.springbootvideo.service;

import cn.com.tzy.springbootentity.dome.video.Device;

import java.util.Objects;

public final class DeviceUpdatePolicy {

    private DeviceUpdatePolicy() {
    }

    public static boolean requiresOffline(Device before, Device after) {
        if (before == null || after == null) {
            return false;
        }
        return !Objects.equals(before.getDeviceId(), after.getDeviceId())
                || !Objects.equals(before.getTransport(), after.getTransport())
                || !Objects.equals(before.getIp(), after.getIp())
                || !Objects.equals(before.getPort(), after.getPort())
                || !Objects.equals(before.getHostAddress(), after.getHostAddress())
                || !Objects.equals(before.getPassword(), after.getPassword())
                || !Objects.equals(before.getSdpIp(), after.getSdpIp())
                || !Objects.equals(before.getLocalIp(), after.getLocalIp())
                || !Objects.equals(before.getStreamMode(), after.getStreamMode())
                || !Objects.equals(before.getHeartBeatInterval(), after.getHeartBeatInterval())
                || !Objects.equals(before.getHeartBeatCount(), after.getHeartBeatCount())
                || !Objects.equals(before.getExpires(), after.getExpires())
                || !Objects.equals(before.getSubscribeCycleForCatalog(), after.getSubscribeCycleForCatalog())
                || !Objects.equals(before.getSubscribeCycleForMobilePosition(), after.getSubscribeCycleForMobilePosition())
                || !Objects.equals(before.getSubscribeCycleForAlarm(), after.getSubscribeCycleForAlarm())
                || !Objects.equals(before.getSsrcCheck(), after.getSsrcCheck())
                || !Objects.equals(before.getMediaServerId(), after.getMediaServerId());
    }
}
