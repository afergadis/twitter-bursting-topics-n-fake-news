package gr.ntua.repository;

import gr.ntua.domain.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by aris on 12/6/2017.
 */

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
public interface UserRepository extends CrudRepository<User, Long> {
}
