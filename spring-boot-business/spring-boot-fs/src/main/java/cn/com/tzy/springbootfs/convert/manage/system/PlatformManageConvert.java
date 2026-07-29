package cn.com.tzy.springbootfs.convert.manage.system;

import cn.com.tzy.springbootentity.dome.fs.Platform;
import cn.com.tzy.springbootentity.param.fs.system.PlatformSaveParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootentity.vo.fs.system.PlatformDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PlatformManageConvert {

    PlatformManageConvert INSTANCE = Mappers.getMapper(PlatformManageConvert.class);

    Platform convert(PlatformSaveParam param);

    PlatformDetailVo convert(Platform entity);

    List<FsOptionVo> convertOptions(List<Platform> entities);

    default FsOptionVo convertOption(Platform entity) {
        if (entity == null) {
            return null;
        }
        return FsOptionVo.builder()
                .id(entity.getId() == null ? null : entity.getId().toString())
                .name(entity.getName())
                .code(entity.getLocalIp())
                .status(entity.getStatus())
                .build();
    }
}
