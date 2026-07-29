package cn.com.tzy.springbootfs.config.fs.runtime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class FsRuntimeConfigRefreshListener {

    private final FsRuntimeConfigService runtimeConfigService;

    public FsRuntimeConfigRefreshListener(FsRuntimeConfigService runtimeConfigService) {
        this.runtimeConfigService = runtimeConfigService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCompanyChanged(FsCompanyConfigChangedEvent event) {
        runtimeConfigService.refreshCompany(event.getCompanyId());
    }
}
