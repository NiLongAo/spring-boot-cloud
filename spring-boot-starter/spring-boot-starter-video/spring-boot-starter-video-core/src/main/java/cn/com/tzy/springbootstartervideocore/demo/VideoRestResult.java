package cn.com.tzy.springbootstartervideocore.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.function.Supplier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoRestResult<T> extends DeferredResult<T> {
    private Long timeoutValue;

    public VideoRestResult(@Nullable Long timeoutValue, Supplier<?> timeoutResult) {
        super(timeoutValue,timeoutResult);
        this.timeoutValue = timeoutValue;
    }

}
