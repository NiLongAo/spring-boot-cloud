package cn.com.tzy.springbootstartervideobasic.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VideoException extends Exception{

    public VideoException(String message){
        super(message);
    }

}
