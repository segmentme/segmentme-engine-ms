package io.segmentme.core.service.repository

import io.segmentme.core.SpringCoreDataApplication
import io.segmentme.core.service.helper.UserHelper
import io.segmentme.management.domain.user.User
import io.segmentme.management.service.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import spock.lang.Specification


@ComponentScan("io.segmentme")
@SpringBootTest(classes = SpringCoreDataApplication.class)
class UserRepositoryTest extends Specification {

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private UserRepository repository

    def 'save user '() {
        given:
        User user = userHelper.createUser()
        when:
        def save = repository.save(user)
        then:
        def found = repository.findById(save.getId())
        found.get() == save

    }

    def 'find user by email'() {
        given:
        User user = userHelper.createUser()
        when:
        def save = repository.save(user)
        then:
        def found = repository.findByEmail(user.getEmail())
        found.get() == save
    }

}
