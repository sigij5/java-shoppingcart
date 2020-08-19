package com.lambdaschool.shoppingcart.controllers;

import com.lambdaschool.shoppingcart.exceptions.ResourceNotFoundException;
import com.lambdaschool.shoppingcart.handlers.HelperFunctions;
import com.lambdaschool.shoppingcart.models.Cart;
import com.lambdaschool.shoppingcart.models.Product;
import com.lambdaschool.shoppingcart.models.User;
import com.lambdaschool.shoppingcart.services.CartService;
import com.lambdaschool.shoppingcart.services.UserAuditing;
import com.lambdaschool.shoppingcart.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carts")
public class CartController
{
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private HelperFunctions helperFunctions;

    @Autowired
    private UserAuditing userAuditing;

    @GetMapping(value = "/user", produces = {"application/json"})
    public ResponseEntity<?> listAllCarts(HttpServletRequest request,
                                          Authentication authentication)
    {
        User u = userService.findByUsername(authentication.getName());
        return new ResponseEntity<>(u.getCarts(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/cart/{cartId}",
            produces = {"application/json"})
    public ResponseEntity<?> getCartById(
            @PathVariable
                    Long cartId)
    {
        Cart p = cartService.findCartById(cartId);
        return new ResponseEntity<>(p,
                                    HttpStatus.OK);
    }

    @PostMapping(value = "/create/product/{productid}")
    public ResponseEntity<?> addNewCart(HttpServletRequest request,
                                        Authentication authentication,
                                        @PathVariable long productid)
    {
//        User dataUser = new User();
//        dataUser.setUserid(userid);

        User u = userService.findByUsername(authentication.getName());

        Product dataProduct = new Product();
        dataProduct.setProductid(productid);

        cartService.save(u, dataProduct);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PutMapping(value = "/update/cart/{cartid}/product/{productid}")
    public ResponseEntity<?> updateCart(@PathVariable long cartid,
                                        @PathVariable long productid)
    {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Cart dataCart = new Cart();
            dataCart.setCartid(cartid);
//            if (dataCart.getUser().getUsername().equalsIgnoreCase(authentication.getName()))
//            {
            Product dataProduct = new Product();
            dataProduct.setProductid(productid);

            cartService.save(dataCart, dataProduct);
            return new ResponseEntity<>(HttpStatus.OK);
//            }
    }

    @DeleteMapping(value = "/delete/cart/{cartid}/product/{productid}")
    public ResponseEntity<?> deleteFromCart(@PathVariable long cartid,
                                            @PathVariable long productid)
    {
        Cart dataCart = new Cart();
        dataCart.setCartid(cartid);

        Product dataProduct = new Product();
        dataProduct.setProductid(productid);

        cartService.delete(dataCart, dataProduct);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
