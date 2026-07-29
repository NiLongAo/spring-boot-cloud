package cn.com.tzy.springbootfs.config.fs.runtime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;

@Component
public class FsRuntimeConfigRefreshPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public FsRuntimeConfigRefreshPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishCompanyChanged(Long companyId) {
        if (companyId == null) {
            return;
        }
        eventPublisher.publishEvent(new FsCompanyConfigChangedEvent(companyId));
    }

    public void publishCompaniesChanged(Collection<Long> companyIds) {
        if (companyIds == null) {
            return;
        }
        companyIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .forEach(this::publishCompanyChanged);
    }
}
