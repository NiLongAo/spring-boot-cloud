package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootfs.config.fs.runtime.FsCompanyConfigChangedEvent;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshListener;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshPublisher;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FsRuntimeConfigRefreshTest.TestConfig.class)
class FsRuntimeConfigRefreshTest {

    @Autowired
    private TestTransactionService transactionService;

    @Autowired
    private FsRuntimeConfigService runtimeConfigService;

    @BeforeEach
    void resetMock() {
        reset(runtimeConfigService);
    }

    @Test
    void refreshRunsAfterCommit() {
        transactionService.publishAndCommit(7L);
        verify(runtimeConfigService).refreshCompany(7L);
    }

    @Test
    void refreshDoesNotRunAfterRollback() {
        assertThrows(RuntimeException.class, () -> transactionService.publishAndRollback(7L));
        verify(runtimeConfigService, never()).refreshCompany(anyLong());
    }

    // ---- inner config ----

    @Configuration
    @EnableTransactionManagement
    static class TestConfig {

        @Bean
        public PlatformTransactionManager transactionManager() {
            return new NoopTransactionManager();
        }

        @Bean
        public FsRuntimeConfigRefreshPublisher refreshPublisher(ApplicationEventPublisher pub) {
            return new FsRuntimeConfigRefreshPublisher(pub);
        }

        @Bean
        public FsRuntimeConfigRefreshListener refreshListener(FsRuntimeConfigService svc) {
            return new FsRuntimeConfigRefreshListener(svc);
        }

        @Bean
        public FsRuntimeConfigService runtimeConfigService() {
            return mock(FsRuntimeConfigService.class);
        }

        @Bean
        public TestTransactionService testTransactionService(FsRuntimeConfigRefreshPublisher publisher) {
            return new TestTransactionService(publisher);
        }
    }

    static class TestTransactionService {

        private final FsRuntimeConfigRefreshPublisher publisher;

        TestTransactionService(FsRuntimeConfigRefreshPublisher publisher) {
            this.publisher = publisher;
        }

        @Transactional
        public void publishAndCommit(Long companyId) {
            publisher.publishCompanyChanged(companyId);
        }

        @Transactional(rollbackFor = RuntimeException.class)
        public void publishAndRollback(Long companyId) {
            publisher.publishCompanyChanged(companyId);
            throw new RuntimeException("forced rollback");
        }
    }

    /** 内存事务管理器，不访问数据库，用于测试 @TransactionalEventListener 行为 */
    static class NoopTransactionManager extends AbstractPlatformTransactionManager {

        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected boolean isExistingTransaction(Object transaction) {
            return false;
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
            // no-op
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
            // no-op: AbstractPlatformTransactionManager handles synchronization callbacks
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
            // no-op
        }
    }
}
