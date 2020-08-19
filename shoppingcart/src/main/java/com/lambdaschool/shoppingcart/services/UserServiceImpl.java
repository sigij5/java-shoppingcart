package com.lambdaschool.shoppingcart.services;

import com.lambdaschool.shoppingcart.exceptions.ResourceFoundException;
import com.lambdaschool.shoppingcart.exceptions.ResourceNotFoundException;
import com.lambdaschool.shoppingcart.handlers.HelperFunctions;
import com.lambdaschool.shoppingcart.models.Cart;
import com.lambdaschool.shoppingcart.models.Role;
import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.models.UserRoles;
import com.lambdaschool.shoppingcart.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Service(value = "userService")
public class UserServiceImpl
        implements UserService
{
    /**
     * Connects this service to the users repository
     */
    @Autowired
    private UserRepository userrepos;

    @Autowired
    private CartService cartService;

    @Autowired
    private HelperFunctions helperFunctions;

    @Override
    public List<User> findAll()
    {
        List<User> list = new ArrayList<>();
        /*
         * findAll returns an iterator set.
         * iterate over the iterator set and add each element to an array list.
         */
        userrepos.findAll()
                .iterator()
                .forEachRemaining(list::add);
        return list;
    }

    @Override
    public User findUserById(long id)
    {
        return userrepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
    }

    @Override
    public User findByUsername(String username){
        User u = userrepos.findByUsername(username.toLowerCase());
        if(u == null){
            throw new ResourceNotFoundException("User name " + username + " not found");
        }
        return u;
    }

    @Transactional
    @Override
    public void delete(long id)
    {
        userrepos.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User id " + id + " not found!"));
        userrepos.deleteById(id);
    }

    @Transactional
    @Override
    public User save(User user)
    {
        User newUser = new User();

        newUser.setUsername(user.getUsername());
        newUser.setPasswordNoEncrypt(user.getPassword());
        newUser.setComments(user.getComments());

        if (user.getCarts()
                .size() > 0)
        {
            throw new ResourceFoundException("Carts are not added through users");
        }
        return userrepos.save(newUser);
    }

    @Transactional
    @Override
    public User update(
            User user,
            long id) {
        User currentUser = findUserById(id);
        if (helperFunctions.isAuthorizedToMakeChanges(currentUser.getUsername())) {
            if (user.getUsername() != null) {
                currentUser.setUsername(user.getUsername()
                        .toLowerCase());
            }

            if (user.getPassword() != null) {
                currentUser.setPasswordNoEncrypt(user.getPassword());
            }

            if (user.getComments() != null) {
                currentUser.setComments(user.getComments()
                        .toLowerCase());
            }

//            if (user.getRoles()
//                    .size() > 0) {
//                currentUser.getRoles()
//                        .clear();
//                for (UserRoles ur : user.getRoles()) {
//                    Role addRole = roleService.findRoleById(ur.getRole()
//                            .getRoleid());
//
//                    currentUser.getRoles()
//                            .add(new UserRoles(currentUser, addRole));
//                }
//            }

            if (user.getCarts()
                    .size() > 0) {
                currentUser.getCarts()
                        .clear();
                for ( Cart c : user.getCarts()) {
                    currentUser.getCarts()
                            .add(new Cart());
                }
            }

            return userrepos.save(currentUser);
        } else {
            throw new ResourceNotFoundException("User not authorized");
        }
    }
}
