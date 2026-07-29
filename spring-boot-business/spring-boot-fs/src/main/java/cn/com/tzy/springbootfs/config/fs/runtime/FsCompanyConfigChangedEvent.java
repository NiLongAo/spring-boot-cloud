package cn.com.tzy.springbootfs.config.fs.runtime;

import lombok.Getter;

import java.util.Objects;

@Getter
public class FsCompanyConfigChangedEvent {

    private final Long companyId;

    public FsCompanyConfigChangedEvent(Long companyId) {
        this.companyId = Objects.requireNonNull(companyId, "companyId");
    }

}
