package io.segmentme.core.service.common

import io.segment.security.mock.AccessServiceMock
import io.segmentme.core.SpringCoreDataApplication
import io.segmentme.core.service.configuration.test.ResourceHolder
import io.segmentme.redis.config.MessagePublisher
import lombok.extern.slf4j.Slf4j
import org.spockframework.spring.SpringBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import static org.mockito.Mockito.mock

@Slf4j
@SpringBootTest(classes = SpringCoreDataApplication.class)
@ActiveProfiles("test")
@Import([ResourceHolder.class])
@ComponentScan("io.segmentme")
abstract class BaseTestWithContext extends AccessServiceMock {

    @SpringBean
    protected MessagePublisher messagePublisher = mock(MessagePublisher.class)
}
