package cn.com.tzy.springbootfs.manage.system;

import cn.com.tzy.springbootentity.param.fs.common.FsLongStatusParam;
import cn.com.tzy.springbootentity.param.fs.common.FsStringStatusParam;
import cn.com.tzy.springbootentity.vo.fs.common.FsOptionVo;
import cn.com.tzy.springbootfs.config.fs.runtime.FsCompanyConfigChangedEvent;
import cn.com.tzy.springbootfs.config.fs.runtime.FsRuntimeConfigRefreshPublisher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class FsCommonContractTest {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    @AfterAll
    static void closeValidatorFactory() {
        VALIDATOR_FACTORY.close();
    }

    @Test
    void emptyLongStatusParamHasTwoValidationViolations() {
        Set<ConstraintViolation<FsLongStatusParam>> violations = VALIDATOR.validate(new FsLongStatusParam());

        assertEquals(2, violations.size());
        assertEquals(new HashSet<>(Arrays.asList("id", "status")), propertyPaths(violations));
    }

    @Test
    void emptyStringStatusParamHasTwoValidationViolations() {
        Set<ConstraintViolation<FsStringStatusParam>> violations = VALIDATOR.validate(new FsStringStatusParam());

        assertEquals(2, violations.size());
        assertEquals(new HashSet<>(Arrays.asList("id", "status")), propertyPaths(violations));
    }

    @Test
    void optionVoBuilderMapsAllFields() {
        FsOptionVo option = FsOptionVo.builder()
                .id("1")
                .name("name")
                .code("code")
                .status(1)
                .build();

        assertEquals("1", option.getId());
        assertEquals("name", option.getName());
        assertEquals("code", option.getCode());
        assertEquals(1, option.getStatus());
    }

    @Test
    void publishingCompaniesChangedPublishesDistinctNonNullCompanies() {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        FsRuntimeConfigRefreshPublisher publisher = new FsRuntimeConfigRefreshPublisher(eventPublisher);
        ArgumentCaptor<FsCompanyConfigChangedEvent> eventCaptor =
                ArgumentCaptor.forClass(FsCompanyConfigChangedEvent.class);

        publisher.publishCompaniesChanged(Arrays.asList(1L, 1L, null, 2L));

        verify(eventPublisher, times(2)).publishEvent(eventCaptor.capture());
        List<Long> companyIds = eventCaptor.getAllValues().stream()
                .map(FsCompanyConfigChangedEvent::getCompanyId)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList(1L, 2L), companyIds);
    }

    @Test
    void companyConfigChangedEventRejectsNullCompanyId() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new FsCompanyConfigChangedEvent(null)
        );

        assertEquals("companyId", exception.getMessage());
    }

    private static Set<String> propertyPaths(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }
}
