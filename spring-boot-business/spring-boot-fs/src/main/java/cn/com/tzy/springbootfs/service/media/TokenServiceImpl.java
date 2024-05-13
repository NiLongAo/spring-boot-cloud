package cn.com.tzy.springbootfs.service.media;

import cn.com.tzy.springbootstarterfreeswitch.service.media.TokenService;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Override
    public boolean authentication(String token) {
        return false;
    }
}
