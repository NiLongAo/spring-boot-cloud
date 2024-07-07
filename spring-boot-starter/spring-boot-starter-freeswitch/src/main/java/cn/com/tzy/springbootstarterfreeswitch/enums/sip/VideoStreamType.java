package cn.com.tzy.springbootstarterfreeswitch.enums.sip;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum VideoStreamType {
		//音频流
		CALL_AUDIO_PHONE(1,8,"CALL_AUDIO_PHONE"),
		//视频流
		CALL_VIDEO_PHONE(2,98,"CALL_VIDEO_PHONE"),
		//推流APP参数
		PUSH_RTP_STREAM(3,0,"PUSH_RTP_STREAM"),
		//推音频流
		PUSH_AUDIO_RTP_STREAM(3,8,"PUSH_AUDIO_RTP_STREAM"),
		//推视频流
		PUSH_VIDEO_RTP_STREAM(4,98,"PUSH_VIDEO_RTP_STREAM"),
		//拨打视频电话
		PLAYBACK(5,0,"PLAYBACK"),
	    // 后续删除
		DOWNLOAD(6,0,"DOWNLOAD")
	;

	private final int value;
	private final int pt;
	private final String name;

	VideoStreamType(int value, int pt,String name) {
		this.value = value;
		this.pt = pt;
		this.name = name;
	}

	public static String getName(int value){
		return MAP.get(value);
	}
	private static final Map<Integer, String> MAP = new HashMap<>();
	static {
		for (VideoStreamType s : VideoStreamType.values()) {
			MAP.put(s.getValue(), s.getName());
		}
	}
}