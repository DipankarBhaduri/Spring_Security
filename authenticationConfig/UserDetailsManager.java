package com.HRNavigator.authenticationConfig;

import com.HRNavigator.models.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsManager implements org.springframework.security.provisioning.UserDetailsManager {

    @Autowired
    private ContextEventAppConfig db;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Query query = new Query(Criteria.where("email").is(s));
        boolean isExist =  db.mongoTemplate().exists(query, User.class);
        if(isExist){
            User user = db.mongoTemplate().findOne(query, User.class);
            if (user != null) {
                return (UserDetails) user;
            }
        }
        throw new BadCredentialsException("Request User Doest exist");
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }
}
