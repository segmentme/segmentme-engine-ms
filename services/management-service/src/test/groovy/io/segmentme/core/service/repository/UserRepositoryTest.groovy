package io.segmentme.core.service.repository


import io.segmentme.core.service.common.BaseTestWithContext
import io.segmentme.core.service.helper.UserHelper
import io.segmentme.management.service.domain.user.User
import io.segmentme.management.service.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired

class UserRepositoryTest extends BaseTestWithContext {

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private UserRepository repository

    def cleanup() {
        repository.deleteAll()
    }

    def 'save user '() {
        given:
        User user = userHelper.createUser()
        when:
        def save = repository.save(user)
        then:
        def found = repository.findById(save.getId())
        found.get().id == save.id

    }

    def 'find user by email'() {
        given:
        User user = userHelper.createUser()
        when:
        def save = repository.save(user)
        then:
        def found = repository.findByEmail(user.getEmail())
        found.get().id == save.id
    }

}
